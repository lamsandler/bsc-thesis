We decided that the most promising use case is mocking, since mocking tests are common and are used for capturing the current behaviour. 
One important point for this project is what the artifact of a call to LLM is and how the interaction withh LLM should look like.
We can outline three different interaction scenarios:
    1. **Forwarding**: We forward the call to some Mock to the LLM. We respond with the raw (or postprocessed) output. This approach will produce the most relevant and creative responses. The obvious downside is high price and latency (we should put numbers on this!). 
    2. **Cached Forwarding**: Same as forwarding but with addition of caching the responses for the same requests. This helps with the price and latency. The drawbacks are less creative and dynamic outputs. Another drawback is that this approach ultimately boils down to approach 3.
    3. **Response as Code**: Instead of caching the response, the LLM will produce the code that mimics the behaviour of the application based on RI. The cost and the latency is virtually zero, especially in the long run. This seemed like the "sweet spot" in terms of balancing the trade-offs of latency and cost. 

What follows is the report of the experiment and research conducted on the topic of RI enhanced mocks.
