**CoreBank Microservices – Architecture Decision Summary**  
**Async Patterns Comparison (Reactor vs Coroutines vs Virtual Threads)**  
**For Documentation / ADR**  
**Version:** 1.0  
**Date:** May 26, 2026  
**Context:** Phase 2 (Java + WebFlux + JPA) → Phase 4 (Kotlin) migration discussion

### 1. The Three Approaches

The conversation compared **three production-grade ways** to implement the `HomeUseCase.getAggregatedBalance()` (parallel fetch of Account + Card + Balance) in Spring Boot 4.0.6 microservices.

| Option | Name | Language / Stack | Key Mechanism | Code Style | First Stable Release |
|--------|------|------------------|---------------|------------|----------------------|
| **A** | Reactor + WebFlux | Pure Java | `Mono.zip()`, operators | Reactive functional chains | Sep 2017 (Spring 5.0 / Boot 2.0) |
| **C** | Kotlin Coroutines + WebFlux | Kotlin | `coroutineScope { async { } }` + `suspend` | Imperative-looking, structured concurrency | Oct 2018 (Kotlin 1.3) |
| **B** | Virtual Threads (Project Loom) | Pure Java | JVM lightweight threads + `spring.threads.virtual.enabled=true` | Plain blocking code | Sep 2023 (Java 21 / Boot 3.2+) |

**All three** deliver high concurrency on the same Netty/WebFlux runtime. They differ in **how they manage platform threads** and developer experience.

### 2. Thread-Centric View of the `HomeAggregate` Problem

Every request must perform **three independent DB calls in parallel** while thousands of requests arrive simultaneously.

- **A (Reactor)**: Limited pool of expensive **platform threads**. `Mono.zip()` releases the thread immediately; work is offloaded to `boundedElastic`. Requires careful operator chaining.
- **C (Coroutines)**: Same small pool of **carrier platform threads**. Each request becomes lightweight coroutines that **suspend** (pause) on I/O. Carrier threads are freed instantly; structured concurrency (`coroutineScope`) handles waiting, cancellation, and errors automatically.
- **B (Virtual Threads)**: Each request gets its own **virtual thread** (millions possible). On blocking I/O, the JVM **unmounts** the virtual thread from its carrier platform thread. Carrier is freed immediately. No `suspend`, no operators — just normal Java code.

**Platform Threads** (old/default): ~1 MB each, expensive, max ~few thousand.  
**Virtual Threads**: Few hundred bytes, cheap, millions possible. Blocking = unmount/remount (JVM magic).

### 3. When to Use Each (2026 Guidance)

**Use Mono / Reactor (Option A)** only for:
- Streaming responses (SSE, WebSocket, large Flux)
- Complex reactive pipelines needing backpressure
- Fully reactive stack (R2DBC + reactive clients)

**Use Coroutines (Option C – Phase 4)** when:
- You want reactive performance + clean, readable, imperative-style code
- Team prefers Kotlin
- Structured concurrency and excellent testing/debugging are priorities

**Use Virtual Threads (Option B)** when:
- You want **maximum simplicity** (plain blocking code)
- Staying in pure Java
- Standard REST/JSON endpoints (your `/api/home/balance`)
- JPA + Spring Data (most common case)

### 4. R2DBC vs JPA (Related Decision)

- **JPA** (used in Phase 2 & 4): Rich ORM features, easy, but **blocks** threads (JDBC underneath).
- **R2DBC**: True non-blocking reactive driver (`Mono`/`Flux` repositories). Required for pure reactive purity **before** Virtual Threads.
- **2026 recommendation**: Stick with **JPA + Virtual Threads** (or Coroutines) unless you have extreme streaming/backpressure needs. R2DBC adds complexity with fewer features.

### 5. Practical Recommendation for CoreBank

- **Phase 2 (Java)**: Used Option A because it was the best available in 2018–2022.
- **Phase 4 (Kotlin)**: Chose Option C → cleaner, safer, more maintainable code while keeping full reactive scalability.
- **Today (2026)**: For new or refactored services, the default is **Spring MVC + Virtual Threads** (Option B) or **WebFlux + Coroutines** (Option C).  
  → Both outperform the old Reactor-only approach in developer velocity and long-term maintainability with **zero runtime penalty**.

**One-line config for Virtual Threads** (works with both MVC and WebFlux):
```properties
spring.threads.virtual.enabled=true
```

### 6. Quick Analogies (Thread View)

- **Reactor**: Small pool of expensive chefs using a complex conveyor belt.
- **Coroutines**: Same chefs, but each order becomes lightweight “pausable” workers that free the stove instantly.
- **Virtual Threads**: Unlimited cheap disposable helpers — the JVM automatically reassigns the real stoves when someone waits.

### 7. Key Takeaways

- Coroutines are a **Kotlin language feature** (no equivalent syntax in Java).
- Virtual Threads are a **JVM feature** (no new keywords needed).
- `Mono` is now a **specialized tool**, not the default.
- The migration from Phase 2 → Phase 4 was **not** “just a repaint” — it delivered measurable gains in safety, readability, and maintainability while preserving the exact external contract.

**Suggested next step**: Add this summary as `docs/ADR-001-Async-Patterns.md` (or append to the Phase 4 README) and consider a small PoC migrating `core-service` to MVC + Virtual Threads for comparison.

This wraps up the entire conversation. All technical details, timelines, version compatibilities, and trade-offs are captured here for future reference.