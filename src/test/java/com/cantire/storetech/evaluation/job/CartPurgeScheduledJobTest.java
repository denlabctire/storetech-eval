package com.cantire.storetech.evaluation.job;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.CartType;
import com.cantire.storetech.evaluation.repo.CartRepository;

@SpringBootTest
public class CartPurgeScheduledJobTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartPurgeScheduledJob cartPurgeScheduledJob;

    @BeforeEach 
    void setup() {
        cartRepository.deleteAll();
    }

    @Test
    void runPurgeHandlesCartTypeVariants() {
        
            Cart nullTypeCart = new Cart();
            nullTypeCart.setRegion("ON");
            nullTypeCart.setCurrencyCode("CAD");
            nullTypeCart.setSubtotal(BigDecimal.ZERO);
            nullTypeCart.setCreatedAt(ZonedDateTime.now().minusDays(2)); // delete
            nullTypeCart.setUpdatedAt(nullTypeCart.getCreatedAt());
            nullTypeCart.setCartType(null);

            Cart nonRewardCart = new Cart();
            nonRewardCart.setRegion("ON");
            nonRewardCart.setCurrencyCode("CAD");
            nonRewardCart.setSubtotal(BigDecimal.ZERO);
            nonRewardCart.setCreatedAt(ZonedDateTime.now().minusDays(2)); // No delete
            nonRewardCart.setUpdatedAt(ZonedDateTime.now().minusHours(22));
            nonRewardCart.setCartType(CartType.NON_REWARD_ELIGIBLE);

            Cart rewardEligibleCart = new Cart();
            rewardEligibleCart.setRegion("ON");
            rewardEligibleCart.setCurrencyCode("CAD");
            rewardEligibleCart.setSubtotal(BigDecimal.ZERO);
            rewardEligibleCart.setCreatedAt(ZonedDateTime.now().minusDays(15));
            rewardEligibleCart.setUpdatedAt(ZonedDateTime.now().minusDays(13));
rewardEligibleCart.setCartType(CartType.REWARD_ELIGIBLE); // no delete

            cartRepository.saveAll(List.of(nullTypeCart, nonRewardCart, rewardEligibleCart));

            cartPurgeScheduledJob.runPurge();

            List<Cart> allCarts = cartRepository.findAll();

            assertEquals(2, allCarts.size());

            int countRewardEligible = (int) allCarts.stream()
                    .filter(cart -> CartType.REWARD_ELIGIBLE.equals(cart.getCartType()))
                    .count();
            assertEquals(1, countRewardEligible);
    }
}
