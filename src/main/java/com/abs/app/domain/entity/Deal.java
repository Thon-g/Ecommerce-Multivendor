package com.abs.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "deals")
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Đổi từ AUTO sang IDENTITY
    @Column(name = "deal_id")
    private Long id;

    @Column(name = "discount")
    private Integer discount;

    @OneToOne
    @JoinColumn(name = "category_id", nullable = false)
    private HomeCategory category;
}
