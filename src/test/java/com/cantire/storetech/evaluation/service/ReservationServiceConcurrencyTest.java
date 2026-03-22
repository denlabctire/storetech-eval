package com.cantire.storetech.evaluation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cantire.storetech.evaluation.dto.ReservationRequest;
import com.cantire.storetech.evaluation.exception.OutOfStockException;
import com.cantire.storetech.evaluation.model.InventoryItem;
import com.cantire.storetech.evaluation.repo.InventoryRepository;
import com.cantire.storetech.evaluation.repo.ReservationRepository;

@SpringBootTest
class ReservationServiceConcurrencyTest {

    private static final int ATTEMPTS = 50;
    private static final int EXPECTED_SUCCESS = 10;
    private static final int EXPECTED_FAILURE = 40;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private ExecutorService executorService;

    @Test
    void shouldReserveExactlyTenItemsWithFiftyConcurrentAttempts() throws InterruptedException, ExecutionException {
        executorService = Executors.newFixedThreadPool(ATTEMPTS);

        CountDownLatch ready = new CountDownLatch(ATTEMPTS);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(ATTEMPTS);

        List<Future<Boolean>> outcomes = new ArrayList<>();

        for (int i = 0; i < ATTEMPTS; i++) {
            long customerId = i + 1L;
            outcomes.add(executorService.submit(() -> {
                ready.countDown();
                start.await();
                try {
                    ReservationRequest request = new ReservationRequest("ABC", 1, customerId);
                    reservationService.reserve(request);
                    return true;
                } catch (OutOfStockException ex) {
                    return false;
                } finally {
                    done.countDown();
                }
            }));
        }

        assertTrue(ready.await(10, TimeUnit.SECONDS), "All workers should be ready before release");
        start.countDown();
        assertTrue(done.await(30, TimeUnit.SECONDS), "All workers should finish within timeout");

        int successCount = 0;
        for (Future<Boolean> outcome : outcomes) {
            if (Boolean.TRUE.equals(outcome.get())) {
                successCount++;
            }
        }

        int failureCount = ATTEMPTS - successCount;

        assertEquals(EXPECTED_SUCCESS, successCount, "Successful reservations should match inventory");
        assertEquals(EXPECTED_FAILURE, failureCount, "Failed reservations should be out-of-stock");

        InventoryItem inventoryItem = inventoryRepository.findAll().stream()
                .filter(item -> "ABC".equals(item.getSku()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing inventory row for ABC"));
        assertEquals(0, inventoryItem.getAvailableQuantity(), "Final inventory should be depleted");

        assertEquals(EXPECTED_SUCCESS, reservationRepository.count(),
            "Only successful reservations should be committed");
    }

    @AfterEach
    void tearDown() {
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}
