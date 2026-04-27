#!/usr/bin/env python3
from __future__ import annotations

import argparse
import math
import re
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable

import matplotlib.pyplot as plt
import numpy as np

LINE_RE = re.compile(
    r"timestamp=(?P<timestamp>\S+)\s+duration_ms=(?P<duration>\d+)\s+success=(?P<success>\w+)$"
)


@dataclass
class RunSet:
    label: str
    source: Path
    raw: np.ndarray
    trimmed: np.ndarray

    @property
    def mean(self) -> float:
        return float(np.mean(self.trimmed))

    @property
    def median(self) -> float:
        return float(np.median(self.trimmed))

    @property
    def std(self) -> float:
        if len(self.trimmed) <= 1:
            return 0.0
        return float(np.std(self.trimmed, ddof=1))

    @property
    def p95(self) -> float:
        return float(np.percentile(self.trimmed, 95))


def pretty_label(path: Path) -> str:
    base = path.stem
    if base.startswith("generated-"):
        base = base[len("generated-") :]
    return base.replace("-", " ").title()


def parse_durations(path: Path) -> list[int]:
    durations: list[int] = []
    for lineno, line in enumerate(path.read_text(encoding="utf-8").splitlines(), start=1):
        text = line.strip()
        if not text:
            continue
        match = LINE_RE.match(text)
        if not match:
            raise ValueError(f"{path}:{lineno}: unexpected format: {text!r}")
        if match.group("success").lower() != "true":
            continue
        durations.append(int(match.group("duration")))
    if not durations:
        raise ValueError(f"{path}: no successful runs found")
    return durations


def trim_extremes(values: Iterable[int], trim_count: int) -> np.ndarray:
    arr = np.array(list(values), dtype=float)
    if trim_count <= 0:
        return arr
    if len(arr) <= 2 * trim_count:
        return arr
    return np.sort(arr)[trim_count:-trim_count]


def load_runsets(input_dir: Path, trim_count: int) -> list[RunSet]:
    files = sorted(input_dir.glob("*.txt"))
    if len(files) < 3:
        raise ValueError(f"Expected at least 3 .txt files in {input_dir}, found {len(files)}")

    runsets: list[RunSet] = []
    for file in files:
        raw = np.array(parse_durations(file), dtype=float)
        trimmed = trim_extremes(raw, trim_count)
        runsets.append(RunSet(label=pretty_label(file), source=file, raw=raw, trimmed=trimmed))
    return runsets


def pick_baseline(runsets: list[RunSet]) -> RunSet:
    for rs in runsets:
        if "default" in rs.label.lower():
            return rs
    return runsets[0]


def make_dashboard(runsets: list[RunSet], trim_count: int, output_path: Path) -> None:
    fig, axes = plt.subplots(2, 2, figsize=(15, 10), constrained_layout=True)

    ax1, ax2 = axes[0]
    ax3, ax4 = axes[1]

    # Panel 1: raw run sequence (time-series by run index)
    for rs in runsets:
        x = np.arange(1, len(rs.raw) + 1)
        ax1.plot(x, rs.raw, marker="o", linewidth=1.8, markersize=5, label=rs.label)
    ax1.set_title("Raw Runtime by Run Index")
    ax1.set_xlabel("Run #")
    ax1.set_ylabel("Duration (ms)")
    ax1.grid(alpha=0.3)
    ax1.legend()

    # Panel 2: trimmed distributions
    data = [rs.trimmed for rs in runsets]
    labels = [rs.label for rs in runsets]
    ax2.boxplot(data, tick_labels=labels, showfliers=False)
    for i, rs in enumerate(runsets, start=1):
        jitter = np.random.uniform(-0.08, 0.08, size=len(rs.trimmed))
        ax2.scatter(np.full(len(rs.trimmed), i) + jitter, rs.trimmed, s=22, alpha=0.6)
    ax2.set_title(f"Trimmed Distributions (drop best/worst {trim_count})")
    ax2.set_ylabel("Duration (ms)")
    ax2.grid(axis="y", alpha=0.3)

    # Panel 3: trimmed mean + variability, with speedup against baseline
    means = np.array([rs.mean for rs in runsets])
    stds = np.array([rs.std for rs in runsets])
    baseline = pick_baseline(runsets)
    baseline_mean = baseline.mean
    speedups = baseline_mean / means

    bars = ax3.bar(labels, means, yerr=stds, capsize=5)
    ax3.set_title(f"Trimmed Mean Runtime (baseline: {baseline.label})")
    ax3.set_ylabel("Duration (ms)")
    ax3.grid(axis="y", alpha=0.3)

    for bar, sp in zip(bars, speedups):
        ax3.text(
            bar.get_x() + bar.get_width() / 2,
            bar.get_height(),
            f"{sp:.2f}x",
            ha="center",
            va="bottom",
            fontsize=9,
        )

    # Panel 4: ECDF on trimmed runs
    for rs in runsets:
        sorted_vals = np.sort(rs.trimmed)
        y = np.arange(1, len(sorted_vals) + 1) / len(sorted_vals)
        ax4.step(sorted_vals, y, where="post", linewidth=2, label=rs.label)
    ax4.set_title("ECDF of Trimmed Runs")
    ax4.set_xlabel("Duration (ms)")
    ax4.set_ylabel("Cumulative Probability")
    ax4.grid(alpha=0.3)
    ax4.legend()

    fig.suptitle("JVM Runtime Benchmark Dashboard", fontsize=16, fontweight="bold")
    output_path.parent.mkdir(parents=True, exist_ok=True)
    fig.savefig(output_path, dpi=180)
    plt.close(fig)


def print_summary(runsets: list[RunSet], trim_count: int) -> None:
    print("\nSummary (duration in ms):")
    print(
        "{:<18} {:>8} {:>8} {:>10} {:>10} {:>10} {:>10}".format(
            "Condition", "raw_n", "used_n", "mean", "median", "std", "p95"
        )
    )
    print("-" * 78)
    for rs in runsets:
        print(
            "{:<18} {:>8} {:>8} {:>10.1f} {:>10.1f} {:>10.1f} {:>10.1f}".format(
                rs.label,
                len(rs.raw),
                len(rs.trimmed),
                rs.mean,
                rs.median,
                rs.std,
                rs.p95,
            )
        )
        if trim_count > 0 and len(rs.raw) <= 2 * trim_count:
            print(
                f"  note: {rs.label} has only {len(rs.raw)} runs; trim={trim_count} not applied for this set"
            )


def main() -> None:
    parser = argparse.ArgumentParser(
        description="Plot a runtime dashboard for JVM benchmark log files."
    )
    parser.add_argument(
        "--input-dir",
        type=Path,
        default=Path("benchmark/perf_time"),
        help="Directory containing the benchmark .txt files",
    )
    parser.add_argument(
        "--output",
        type=Path,
        default=Path("benchmark/perf_time/perf_dashboard.png"),
        help="Output image path",
    )
    parser.add_argument(
        "--trim",
        type=int,
        default=3,
        help="Number of best and worst runtimes to discard per condition",
    )
    args = parser.parse_args()

    if args.trim < 0:
        raise ValueError("--trim must be >= 0")

    runsets = load_runsets(args.input_dir, args.trim)
    make_dashboard(runsets, args.trim, args.output)
    print_summary(runsets, args.trim)
    print(f"\nSaved dashboard to: {args.output}")


if __name__ == "__main__":
    main()
