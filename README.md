# Thesis Workspace

This repository contains materials and experiments for the thesis project, including JVM runtime benchmarking data and analysis scripts.

## Structure

- `benchmark/perf_time/`: benchmark run logs and plotting scripts
- `junie_with_runtime/`: Kotlin test/runtime-related code
- `report*.md`, `notes.md`, `literature_review.md`: writing and research notes

## Benchmark Dashboard

A matplotlib dashboard is available for comparing three JVM-based runtime conditions.

Input files (example):
- `benchmark/perf_time/generated-default.txt`
- `benchmark/perf_time/generated-kllm.txt`
- `benchmark/perf_time/generated-force-kllm.txt`

Script:
- `benchmark/perf_time/plot_perf_dashboard.py`

Output image:
- `benchmark/perf_time/perf_dashboard.png`

## Setup (uv)

This project uses `uv` for Python environment/dependency management.

Install dependencies:

```bash
uv sync
```

Generate dashboard:

```bash
uv run python benchmark/perf_time/plot_perf_dashboard.py
```

Optional arguments:

```bash
uv run python benchmark/perf_time/plot_perf_dashboard.py \
  --input-dir benchmark/perf_time \
  --trim 3 \
  --output benchmark/perf_time/perf_dashboard.png
```

## Notes on Trimming

The plotting script trims the `N` fastest and `N` slowest runs per condition (default `N=3`) to reduce warmup/noise effects before computing summary statistics.
