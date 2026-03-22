package com.cantire.storetech.evaluation.job;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.CartType;

import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PurgingJob {

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public PurgingJob(EntityManager entityManager, PlatformTransactionManager transactionManager) {
        log.info("Constructing PurgingJob");
        this.entityManager = entityManager;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public int runPurge(ZonedDateTime cutoffTime) {
        List<Cart> carts = entityManager
                .createQuery("select c from Cart c", Cart.class)
                .getResultList();

        AtomicInteger purgedCount = new AtomicInteger(0);

        transactionTemplate.executeWithoutResult(status -> {
            for (Cart cart : carts) {
                Cart managedCart;
                if (cart.getCreatedAt() != null && cart.getCreatedAt().isBefore(cutoffTime)) {

                    // FPOE-1234: Added new functionality to change the cuttoff time to 14 days for carts with cart_type of REWARD_ELIGIBLE carts      
                    if (cart.getCartType() == CartType.REWARD_ELIGIBLE) {
                        if (cart.getCreatedAt().isAfter(cutoffTime.minusDays(13))) {
                            log.info("Skipping cart {} with cart_type REWARD_ELIGIBLE created at {} as it is within the 14 day cutoff", cart.getId(), cart.getCreatedAt());
                            continue;
                        } else {
                            log.info("Purging cart {} with cart_type REWARD_ELIGIBLE created at {} as it is older than the 14 day cutoff", cart.getId(), cart.getCreatedAt());
                            managedCart = entityManager.contains(cart) ? cart : entityManager.merge(cart);
                            entityManager.remove(managedCart);
                            purgedCount.incrementAndGet();
                        }
                    } else {
                        managedCart = entityManager.contains(cart) ? cart : entityManager.merge(cart);
                        entityManager.remove(managedCart);
                        purgedCount.incrementAndGet();
                    }

                }
            }
        });

        return purgedCount.get();
    }
}
