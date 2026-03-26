package com.cantire.storetech.evaluation.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cantire.storetech.evaluation.model.Cart;
import com.cantire.storetech.evaluation.model.CartType;
import com.cantire.storetech.evaluation.repo.CartRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartPurgingServiceImpl implements CartPurgingService {

    private final CartRepository cartRepository;

    @Value("${jobs.purge.cutoff-default}")
    private int cutoffDefault;

    @Value("${jobs.purge.cutoff-reward-eligible}")
    private int cutoffRewardEligible;

    @Transactional
    @Override
    public void purgeCarts() {

        var furhterCutoffTime = cutoffRewardEligible > cutoffDefault ? cutoffRewardEligible : cutoffDefault;

        ZonedDateTime startZdt = ZonedDateTime.now().minusHours(furhterCutoffTime);
        ZonedDateTime endZdt = ZonedDateTime.now(); 

        List<Cart> cartsToDelete = new ArrayList<>();

        List<Cart> carts =cartRepository.findAllBetweenCutoffTimes(startZdt, endZdt);
                carts.forEach(cart -> {
                    if (cart.getCartType() != null && CartType.REWARD_ELIGIBLE.equals(cart.getCartType())) {
                        if (cart.getUpdatedAt().isBefore(ZonedDateTime.now().minusHours(cutoffRewardEligible))) {
                            cartsToDelete.add(cart);
                        } else {
                            log.info("Skipping cart {} with cart_type REWARD_ELIGIBLE updated " +
                            "at {} as it is within the {} hour cutoff", cart.getId(), 
                            cart.getUpdatedAt(), cutoffRewardEligible);
                        }
                    } else {
                        if(cart.getUpdatedAt().isBefore(ZonedDateTime.now().minusHours(cutoffDefault))) {
                            cartsToDelete.add(cart);
                        } else {
                            log.info("Skipping cart {} with cart_type {} updated at {} as it is within the {} hour cutoff", 
                            cart.getId(), cart.getCartType(), cart.getUpdatedAt(), cutoffDefault);
                        }
                    }
                });    
        cartRepository.deleteAll(cartsToDelete);
    }
}
