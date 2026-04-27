## Typical presentation structure

1. **Context 1**
   - LLM features in programming workflows are already common and useful:
     - IDE assistance (e.g., GitHub Copilot / GitLab Duo): code completion, chat, refactoring and test suggestions.
     - Structured output tooling (e.g., OpenAI structured outputs, Koog structured output): map model responses into typed schemas.
   - These tools improve developer productivity and reduce boilerplate in day-to-day coding.

2. **Context 2**
   - But for the thesis narrative, this is still "boring" in one sense: most features assist *around* code authoring, not *inside* application runtime behavior.
   - Our KotlinLLM initiative focuses on runtime logic delegation: using live execution context to make decisions while the app is running.
   - *Annotation:* work done by somebody else - **minimize it!**

3. **Problem with realistic example**
   - Example project: a tool takes a GitHub repository (extendable to other providers), fetches issues, and shows ones suitable for beginners.
   - The core difficulty is that "beginner-friendly" is encoded inconsistently:
     - one repo uses `good first issue`
     - another uses `starter`, `easy`, `newcomer`, `help wanted`
     - some repos combine labels and templates, others only labels
   - Classical serializers map fields, but they do not solve semantic mismatch between repository-specific label vocabularies.

4. **Ideas and hypothesis behind your project**
   - Use `asLlm<RawIssue, Issue>()` to normalize provider issue payloads into a unified domain model.
   - The target model contains `isBeginnerFriendly` as a normalized flag inferred from repository-specific labeling conventions.
   - Hypothesis: runtime-informed `asLlm` normalization reduces manual mapping logic and adapts better to label drift across repositories.

5. **Goals and Objectives**
   - Objective 1: design a reusable issue-normalization pipeline (`RawIssue -> Issue`) using `asLlm`.
   - Objective 2: evaluate correctness and engineering cost versus deterministic hardcoded label rules.
   - Objective 3: build safe testing and fallback flow using `mockLlm` for API mocking and controlled edge-case simulation.
   - *Annotation:* high level view on your contribution - **make it short and clear!**

6. **Contributions related to objective 1**
   - Defined a provider-agnostic issue model, e.g. `Issue(id, title, state, labels, isBeginnerFriendly, provider, url)`.
   - Built a GitHub-first normalization flow with extension points for other providers.
   - Added a strict output contract so `asLlm` returns typed data that can be consumed directly by application logic.

7. **Contributions related to objective 2**
   - Created a baseline with deterministic label dictionaries and rule-based classification.
   - Compared baseline vs `asLlm` on beginner-friendly issue detection quality and maintenance effort.
   - Measured practical outcomes: classification quality (precision/recall/F1) and reduction of manual mapping updates.

8. **Contributions related to objective 3**
   - Used `mockLlm` to mock GitHub API behaviors for repeatable tests.
   - Simulated edge cases: missing labels, conflicting labels, noisy metadata, and repository label drift.
   - Added fallback handling: unresolved issues are marked for manual review instead of being misclassified.
   - *Annotation:* Details of your contribution:
     - **Focus on this part!**
     - **No rush!**
     - **Make it understandable!**

9. **Summary**
   - Main result: `asLlm` enables practical normalization of repository-specific issue semantics into a stable typed model.
   - Supporting result: `mockLlm` enables controlled, reproducible testing of API integration and edge cases.
   - Impact: less brittle glue code, clearer extension path to other providers, and safer behavior under inconsistent external data.
   - *Annotation:* Final slide with your contributions again:
     - instead of "thank you" or "Q&A"
     - consider to add some interpretation or impact (success, failure, confirmed, released, published, ...)
