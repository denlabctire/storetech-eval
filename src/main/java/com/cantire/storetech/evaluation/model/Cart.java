package com.cantire.storetech.evaluation.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.cantire.storetech.evaluation.dto.CartSaveRequest;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Cart aggregate root representing a shopping cart with products, pricing, and tax information.
 */
@Entity
@Data
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter(AccessLevel.NONE)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "cart_product",
            joinColumns = @JoinColumn(name = "cart_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )

    private Set<Product> products = new LinkedHashSet<>();

    @Setter(AccessLevel.NONE)
    @ElementCollection
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Long, Integer> productQuantities = new LinkedHashMap<>();

    private String region;

    private String currencyCode;

    private BigDecimal subtotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private CartType cartType;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Transient
    private List<TaxInfo> applicableTaxes = new LinkedList<>();

    /**
     * Factory method to create a new Cart instance.
     */
    public static Cart create(CartSaveRequest cartSaveRequest, List<TaxInfo> taxesForRegion) {
        Cart cart = new Cart();
        cart.setRegion(cartSaveRequest.getRegion());
        cart.setCurrencyCode(cartSaveRequest.getCurrencyCode());
        cart.setSubtotal(cart.calculateSubtotal());
        cart.setCreatedAt(ZonedDateTime.now());
        cart.setApplicableTaxes(taxesForRegion);
        return cart;
    }

    public void addProduct(Product product, int quantity) {
        products.add(product);
        productQuantities.put(product.getId(), quantity);
        this.setSubtotal(calculateSubtotal());
    }

    public void removeProduct(Long productId) {
        products.removeIf(p -> p.getId().equals(productId));
        productQuantities.remove(productId);
    }

    /**
     * Helper method to calculate subtotal from products in cart.
     *
     * @return Calculated subtotal
     */
    public BigDecimal calculateSubtotal() {
        return this.getProducts().stream()
                .map(product -> {
                    int quantity = this.getProductQuantityOrZero(product.getId());
                    Optional<BigDecimal> price = Product.findCurrentPrice(product, this.getCurrencyCode());
                    return price.map(p -> p.multiply(new BigDecimal(quantity)))
                            .orElse(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getProductQuantityOrZero(Long productId) {
        return productQuantities.getOrDefault(productId, 0);
    }

    public void emptyCart() {
        this.getProducts().clear();
        this.getProductQuantities().clear();
        this.setSubtotal(BigDecimal.ZERO);
    }
}
