---
title: Challenge 002 Concurrency Screening
description: Screening instructions for implementing a concurrency-safe inventory reservation flow with a required pessimistic locking approach
author: StoreTech Evaluation Team
ms.date: 2026-03-22
ms.topic: how-to
keywords:
  - spring boot
  - spring data jpa
  - concurrency
  - reservation
  - pessimistic locking
estimated_reading_time: 7
---

## Overview

This challenge evaluates your ability to implement a concurrency-safe reservation
workflow in Spring Boot with JPA.

You will complete the missing reservation flow so concurrent requests cannot
oversell inventory. The required baseline solution uses pessimistic locking.

## Why This Challenge Uses Pessimistic Locking

The exercise simulates 50 concurrent reservation attempts for a single SKU with
only 10 units available.

We require pessimistic locking for the screening baseline because it produces a
more deterministic outcome under contention and keeps the exercise focused on
transaction correctness, service design, and API behavior.

Optimistic locking can also solve this class of problem, but it adds retry and
conflict-handling concerns that are not the primary goal of this screening.

## What To Implement

### Candidate Tasks

1. Create the controller endpoint: `POST /api/reservations/reserve`
2. Complete `reserve()` in `ReservationServiceImpl`
3. Implement the inventory repository locking method in
   `InventoryRepository` using `@Lock(LockModeType.PESSIMISTIC_WRITE)`
4. Return a successful reservation response when inventory is available
5. Return the correct error response when inventory is not available

## API Contract

### Request

```json
{
  "sku": "ABC",
  "quantity": 1,
  "customerId": 123
}
```

### Success Response

* HTTP `200`
* Response includes `id`, `sku`, `quantity`, `customerId`, `status`, and
  `createdAt`

### Failure Response

* Out-of-stock returns HTTP `409`
* Validation failure or malformed request returns HTTP `400`

## Acceptance Criteria

1. Use Spring MVC, Spring Data JPA, and H2
2. Use pessimistic locking in the inventory workflow code
3. **Ensure concurrency safety so parallel requests cannot oversell inventory**
4. Prove the behavior with a Spring Boot integration test
5. Demonstrate this exact result for 50 parallel attempts against inventory 10:
   * 10 successful reservations
   * 40 failures due to out-of-stock
   * Final inventory of 0

## Implementation Expectations

Keep the implementation aligned with the current project layering:

`controller -> service -> repository -> model`

Follow the existing DTO and converter patterns already used elsewhere in the
project.

Use transaction boundaries deliberately. The critical path is:

1. Load the inventory row with a pessimistic write lock
2. Check available quantity
3. Decrement inventory when sufficient stock exists
4. Persist the reservation and updated inventory in the same transaction

## What We Are Evaluating

This challenge is not only about choosing a lock annotation. It is intended to
show whether you can:

* Apply Spring transactions correctly under concurrent load
* Keep controller, service, repository, and persistence responsibilities clear
* Handle validation and failure conditions predictably
* Write a concurrency test that proves the required behavior instead of relying
  on assumptions

## Notes

Pessimistic locking is the required baseline for this exercise.

If you want to explore an optimistic locking variant after the baseline is
working, treat that as an optional extension rather than the primary solution.