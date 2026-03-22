---
title: Challenge 003 Purge Carts Intermediate
description: Intermediate screening challenge for rewriting the cart cleanup process as an automated process while preserving existing functionality.
author: StoreTech Evaluation Team
ms.date: 2026-03-22
ms.topic: how-to
keywords:
  - spring boot
  - cart cleanup
  - refactoring
  - performance
estimated_reading_time: 8
---

## Overview

This challenge evaluates your ability to improve an existing feature that is
correct in places, but poorly implemented and difficult to maintain.

You are given a user story to improve the cart cleanup process:

    Barb has been complaining about the cart cleanup job that Ron, our junior developer, worked on last year. It takes a long time to run, you have to call a web endpoint manually to execute it, it only cleans up carts from 24 hours ago, and it deletes carts even if the cart was updated within the last 24 hours -- this should be fixed if we're already going into the code. We need changes to the functionality as soon as possible.
    
    Jerry, one of our intermediate developers, says the current implementation is **throw away**, but we still need to preserve the functionality.

## Acceptance Criteria

1. Required: The cart cleanup functionality runs as an automated process every 5 minutes.
2. Required: The implementation uses modern patterns and coding standards.
3. Required: The old purge endpoint is no longer accessible.
4. Required: Existing rules are preserved exactly after the rewrite.
5. Required: The implementation does not rely on a manual HTTP request to execute cleanup.
6. Required: The cleanup logic is covered by automated tests.
7. **Strongly Recommended**: Carts that have been updated (add/removed, etc) within the cutoff period should not be deleted.  The create date of the cart should not drive the functionality...
8. **Recommended**: **ALL** schedule and cleanup cutoff are easy to change later.
9. **Recommended**: The rewrite avoids loading unnecessary data into memory and is **measurably cleaner** than the current implementation.
10. **Nice to have**: Clean as you code

## What To Implement

Rewrite the cart cleanup flow as an automated process using standard Spring support.

## Candidate Tasks

1. Replace the current manual cleanup trigger with an automated job.
2. The job needs to run every 5 minutes.
3. Preserve the existing cleanup business rules while rewriting the implementation, and **refactor where appropriate**.  Remember, Barb wants carts updated within the cutoff to be exempt from the purge.
4. Remove or disable the old web endpoint so it is no longer accessible.
5. Refactor the cleanup code to use modern patterns and cleaner application structure.
6. Add or update tests to prove the cleanup behavior still works

## Implementation Expectations

Review the current behavior before changing anything. The screening goal is not to invent new cleanup rules. It is to modernize the implementation without breaking the rules that already exists.

Use Spring's built-in support for the baseline solution.  If you think a dedicated scheduling or job framework should be introduced, explain why.

The expected direction is:

1. Move cleanup execution away from the controller layer.
2. **Keep responsibilities clear between scheduling, orchestration, and database operations**, adhering to Single Responsibility Principle.
3. Rewrite the current implementation rather than layering new behavior on top of the existing throw-away flow.

## Hints

1. There is already a purge job in the codebase, but it is intentionally a poor implementation and should be treated as a rewrite target, not as a pattern to copy.
2. There is currently no support for automation in the application, you will have to consider this.
3. There is already test coverage around purge behavior. Use the existing tests and add to them **or improve them** rather than starting from scratch.
4. The current implementation may mix concerns such as HTTP triggering, orchestration, and persistence. Part of this challenge is separating those concerns cleanly.
5. Improve structure and performance without changing what the cleanup rules mean.
6. As this code hasn't been deployed anywhere yet, it is safe to modify the liquibase schema.

## Files To Review

Before implementing, review the existing purge-related code and tests.

```text
src/
|-- main/
|   |-- java/com/cantire/storetech/evaluation/
|   |   |-- controller/
|   |   |   `-- CartController.java
|   |   |-- job/
|   |   |   `-- PurgingJob.java
|   |   `-- model/
|   |       |-- Cart.java
|   |       `-- CartType.java
|   `-- resources/
|       `-- db.changelog/
|           `-- changelog-5.0-cart-created-at.xml
`-- test/
  `-- java/com/cantire/storetech/evaluation/
    `-- job/
      `-- PurgingJobIntegrationTest.java
```

## What We Are Evaluating

This challenge is intended to show whether you can:

* Recognize when existing code should be rewritten rather than incrementally patched
* Apply automation in a clean, performant, production-appropriate way
* Preserve logic during refactoring to prevent escaping defects
* Improve maintainability, readability, and execution flow
* Write tests that protect the cleanup behavior during change

## Deliverable Guidance

A strong solution will:

* Schedule the cleanup automatically
* Remove the manual trigger path from normal usage
* Keep the business logic intact
* Make future schedule changes straightforward
* Leave the code in better shape than it was found