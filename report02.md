# Report 1
We describe the experiment of comparing the tests quality when running Mock test generation using an SWE Agent (Junie, Claude Code). We compare how the collected runtime information* affects the tests quality.
We used open-source backend (Ktor/Spring) repositories with MockK/mockito as a dependency to have ground truth tests.  Finding solid repositories like that was not easy: most of the open source backend applications were educational or just some personal projects. One example of a repository on which the test was conducted is spring-petclinic-kotlin.

*: As runtime information, logs of every call to a method or field (args, return, values and types) of a Service Under Test that occured during runtime were used.

There was no significant difference between Junie and Claude Agent in the tests, so the term Junie will be used without loss of generality.
We describe three cases:

1. Junie, no Runtime information: When no runtime information is provided we simply prompt it to generate tests for a specific Service using mocks. The coverage of the tests was good. Junie focused on using Spring's testing framework, which lead to "idiomatic" tests. However, the tests contained "synthetic" data that is exactly the same as in the ground truth tests. This indicates that the LLM was familiar with this repository and "remembers" the correct tests in this case.

2. Junie, with Runtime information as a necessity: We feed the RI (runtime information) and explicitly guide Junie to create tests based on it. The resulting tests closely followed the scenarios encountered in mocks. It does seem to "overfit" and ignore the scenarios that might happen, but didn't in the runtime information provided. This, however, seems to be a result of the quite specific and short runtime information provided on input. This is opposed to runtime information of a real deployed application (which I at the time of the experiment do not posses :D). There is also a need of post-processing the runtime information before feeding it to the agent – I believe that if we cluster the logs into use cases, we could help the agent generate Mocks better.
The quality of the generated tests was worse, as Junie didn't focus on using idiomatic Spring utilities. Perhaps appending the logs in the prompt messed up the context somehow.

3. Junie, with Runimte information as a suplement: We feed the RI and guide Junie to create tests, and Junie is free to decide if it is relevant. However, the LLM seemed to ignore this information, and generated output identical to output in (1).
This can also mean that the LLM was trained on this repository and understands the workflow of the App. This is might be why Junie performs worse when requested to improvise as in (2) rather than go with the fine-tuned "baseline" behaviour (1).

In conclusion, the repositories that were chosen aren't great candidates for this experiment. The LLMs were trained on popular repositories such as the one in the example. Also, the RI collected is limited, and doesn't really represent the data one can see in a real-world deployed application. We can also note that we should trim and simplify the logs so that the context window is not wasted, as well as to delegate the task of "understanding" the logs from the LLM to some other entity.---

# Report 2

We research the topic of applying AI in long-running services and the logs they produce.
A simple search immediatly yields a powerful tool from a well-known organization - [kotzilla](https://kotzilla.io/) that are known for the [koin](https://insert-koin.io/) library.
It is a powerful performance monitoring tool, designed for Kotlin, able to collect relevant runtime data at scale. The use of AI is quite limited, and their main feature is simply creating a prompt for the LLM that is built on the traced information. What is important to note here, is that there exists a market/audience that actively collects runtime information, for example with this tool. We may proceed with our experiments having this in mind.

The following agent idea occured to me as for the experiments:

**base features:**
* the agent captures the behaviour of real interfaces
* can cluster the logs into "scenarios" and "remember" them
* understands the dependencies and relations between objects 

**configurable features:**
* serves as a "fake" implementation of the captured interface (as in *mockLLM*)
* serves as fallback for the captured interface's implementation (as in *asLLM*, *catchLLM*)
* reports the issues and proposed fixes (as in *catchLLM*) 

Implementation details to follow.
