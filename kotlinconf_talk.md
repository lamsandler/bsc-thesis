KotlinLLM: Leveraging AI for Runtime Logic Delegation in Kotlin

# Introduction
As a core part of my Bachelor Thesis, I was researching use cases and implementations for delegating logic to an LLM during application's runtime.
In simple words, I wanted "fill here".
We ended up with two exciting use cases, `asLlm` and `mockLlm`.

```kt
// Automatically parse from one type to another
val order: Order = asLlm(readlnOrNull())
```
```kt
// Create a trivial implementation of an interface
val userRepository: UserRepository = mockLlm()
```

While one part of the thesis is analyzing these use cases, let's instead focus on a more interesting part – the implementation.
We'll cover how `asLlm<F, T>()` was implemented, as it was implemented first.

# Slide structure (~15 min)

## Slide 1 — KotlinLLM in one sentence (1 min)
- "What if Kotlin could ask an LLM for logic at runtime, exactly when it needs it?"
- Quickly show `asLlm` and `mockLlm`.
- Interactive: ask audience to raise hands — "Who has wanted one more dynamic layer in Kotlin?"

## Slide 2 — The core tension (1 min)
- Why do we need to call LLM in runtime, not before or after?
- Frame it as a story: compile-time is too early, post-processing is too late, runtime is where context is alive.
- Interactive: quick audience question — "Which one would you choose first: before, during, or after runtime?"

## Slide 3 — Enter the runtime world (1.5 min)
- How do we get values from the application runtime? `JDI`.
- Explain this as the "bridge" from running app state to LLM input.
- Show a tiny pipeline visual: Runtime State -> JDI -> Prompt Input.

## Slide 4 — Generating logic is easy, loading it is hard (1.5 min)
- How to load llm-generated logic?
- Move from generated source/code idea to executable behavior.
- Story beat: "This was the first time the prototype felt real."

## Slide 5 — Make it feel native: Hot Reload (1.5 min)
- Hot Reload! Swap logic while app is running.
- Emphasize dev velocity and experimentation loop.
- Interactive: live mini demo moment (or animated sequence) of behavior changing without restart.

## Slide 6 — The bootstrap paradox (2 min)
- Now we know how to replace, but we also need some class beforehand, that will be replaced!
- Explain placeholder/preloaded class strategy.
- Story beat: "You cannot replace what does not exist."

## Slide 7 — Type defaults as a survival tool (2 min)
- What is the default value of a type?
- Why defaults are needed before real runtime values are available.
- Show 3-4 examples: primitives, objects, collections, sealed-ish fallback.
- Interactive: ask audience to guess one tricky default before revealing.

## Slide 8 — Escaping nulls safely (2 min)
- How to escape nulls?
- Explain null-handling strategy so generated logic does not crash the app.
- Story beat: "Most failures were not model quality issues, but null edges."

## Slide 9 — Full flow recap (1 min)
- End-to-end architecture from call-site to replaced runtime logic.
- Map each previous slide to one step in the pipeline.

## Slide 10 — What this means in practice (1 min)
- Return to `asLlm` and `mockLlm`.
- Show when this is powerful and when this is overkill.
- Interactive close: "Would you ship this in production for your domain?"

## Optional backup slide — Risks and guardrails
- Determinism, latency, observability, and safe fallbacks.
- Good for Q&A if the room gets very practical.
