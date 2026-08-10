package com.abs.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_item_id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "size", columnDefinition = "VARCHAR(20)")
    private String size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "mrp_price", nullable = false)
    private Integer mrpPrice;

    @Column(name = "selling_price", nullable = false)
    private Integer sellingPrice;

    @Column(name = "user_id", nullable = false, columnDefinition = "VARCHAR(50)")
    private String userId;
}
