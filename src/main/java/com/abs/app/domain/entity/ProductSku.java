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
@Table(name = "product_skus")
public class ProductSku {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_id")
    private Long id;

    @Column(name = "sku_code", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
    private String skuCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "color", columnDefinition = "VARCHAR(50)")
    private String color;

    @Column(name = "size", columnDefinition = "VARCHAR(50)")
    private String size;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "selling_price")
    private Integer sellingPrice;
}
