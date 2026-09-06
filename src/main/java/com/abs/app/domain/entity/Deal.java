package com.abs.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "deals")
public class Deal {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Đổi từ AUTO sang IDENTITY
    @Column(name = "deal_id")
    private Long id;

    @Column(name = "discount")
    private Integer discount;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private HomeCategory category;
}
