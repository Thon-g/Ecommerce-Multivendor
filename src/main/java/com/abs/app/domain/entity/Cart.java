package com.abs.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "carts")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Đổi từ AUTO sang IDENTITY
    @Column(name = "cart_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CartItem> cartItems = new HashSet<>();

    @Column(name = "total_selling_price")
    private Double totalSellingPrice;

    @Column(name = "total_item")
    private Integer totalItem;

    @Column(name = "total_mrp_price")
    private Integer totalMrpPrice;

    @Column(name = "discount")
    private Integer discount;

    @Column(name = "coupon_code", columnDefinition = "VARCHAR(50)")
    private String couponCode;
}
