package com.cantire.storetech.evaluation.job;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.cantire.storetech.evaluation.Application;
import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.CartType;

import jakarta.persistence.EntityManager;

@SpringBootTest(classes = Application.class)
class PurgingJobIntegrationTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void runPurgeRemovesOnlyCartsOlderThanCutoff() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createQuery("delete from Cart c").executeUpdate();

            Cart oldCart = new Cart();
            oldCart.setRegion("ON");
            oldCart.setCurrencyCode("CAD");
            oldCart.setSubtotal(BigDecimal.ZERO);
            oldCart.setCreatedAt(ZonedDateTime.now().minusDays(2));
            entityManager.persist(oldCart);

            Cart recentCart = new Cart();
            recentCart.setRegion("ON");
            recentCart.setCurrencyCode("CAD");
            recentCart.setSubtotal(BigDecimal.ZERO);
            recentCart.setCreatedAt(ZonedDateTime.now().minusHours(1));
            entityManager.persist(recentCart);

            entityManager.flush();
        });

        PurgingJob purgingJob = new PurgingJob(entityManager, transactionManager);

        int purgedCount = purgingJob.runPurge(ZonedDateTime.now().minusDays(1));

        Long remainingCount = transactionTemplate.execute(status ->
                entityManager.createQuery("select count(c) from Cart c", Long.class).getSingleResult());

        assertEquals(1, purgedCount);
        assertEquals(1L, remainingCount);
    }

    @Test
    void runPurgeHandlesCartTypeVariants() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createQuery("delete from Cart c").executeUpdate();

            Cart nullTypeCart = new Cart();
            nullTypeCart.setRegion("ON");
            nullTypeCart.setCurrencyCode("CAD");
            nullTypeCart.setSubtotal(BigDecimal.ZERO);
            nullTypeCart.setCreatedAt(ZonedDateTime.now().minusDays(2));
            nullTypeCart.setCartType(null);
            entityManager.persist(nullTypeCart);

            Cart nonRewardCart = new Cart();
            nonRewardCart.setRegion("ON");
            nonRewardCart.setCurrencyCode("CAD");
            nonRewardCart.setSubtotal(BigDecimal.ZERO);
            nonRewardCart.setCreatedAt(ZonedDateTime.now().minusDays(2));
            nonRewardCart.setCartType(CartType.NON_REWARD_ELIGIBLE);
            entityManager.persist(nonRewardCart);

            Cart rewardEligibleCart = new Cart();
            rewardEligibleCart.setRegion("ON");
            rewardEligibleCart.setCurrencyCode("CAD");
            rewardEligibleCart.setSubtotal(BigDecimal.ZERO);
            rewardEligibleCart.setCreatedAt(ZonedDateTime.now().minusDays(2));
            rewardEligibleCart.setCartType(CartType.REWARD_ELIGIBLE);
            entityManager.persist(rewardEligibleCart);

            entityManager.flush();
        });

        PurgingJob purgingJob = new PurgingJob(entityManager, transactionManager);

        int purgedCount = purgingJob.runPurge(ZonedDateTime.now().minusDays(1));

        Long remainingCount = transactionTemplate.execute(status ->
                entityManager.createQuery("select count(c) from Cart c", Long.class).getSingleResult());

        Long rewardEligibleRemaining = transactionTemplate.execute(status ->
                entityManager.createQuery(
                                "select count(c) from Cart c where c.cartType = :cartType",
                                Long.class)
                        .setParameter("cartType", CartType.REWARD_ELIGIBLE)
                        .getSingleResult());

        assertEquals(2, purgedCount);
        assertEquals(1L, remainingCount);
        assertEquals(1L, rewardEligibleRemaining);
    }

    @Test
    void runPurgeRemovesRewardEligibleCartOlderThanFourteenDays() {
        transactionTemplate.executeWithoutResult(status -> {
            entityManager.createQuery("delete from Cart c").executeUpdate();

            Cart rewardEligibleCart = new Cart();
            rewardEligibleCart.setRegion("ON");
            rewardEligibleCart.setCurrencyCode("CAD");
            rewardEligibleCart.setSubtotal(BigDecimal.ZERO);
            rewardEligibleCart.setCreatedAt(ZonedDateTime.now().minusDays(15));
            rewardEligibleCart.setCartType(CartType.REWARD_ELIGIBLE);
            entityManager.persist(rewardEligibleCart);

            entityManager.flush();
        });

        PurgingJob purgingJob = new PurgingJob(entityManager, transactionManager);

        int purgedCount = purgingJob.runPurge(ZonedDateTime.now().minusDays(1));

        Long remainingCount = transactionTemplate.execute(status ->
                entityManager.createQuery("select count(c) from Cart c", Long.class).getSingleResult());

        assertEquals(1, purgedCount);
        assertEquals(0L, remainingCount);
    }
}