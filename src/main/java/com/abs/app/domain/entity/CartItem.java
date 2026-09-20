package com.abs.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Đổi từ AUTO sang IDENTITY
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "sku_id")
    private ProductSku sku;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "mrp_price", nullable = false)
    private Integer mrpPrice;

    @Column(name = "selling_price", nullable = false)
    private Integer sellingPrice;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(50)")
    private String userId;
}
