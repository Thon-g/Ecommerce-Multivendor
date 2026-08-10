package com.abs.app.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "categories")
public class Category {
    @Id
    @Column(name = "categories_id", nullable = false, unique = true)
    private String id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String name;

    @NotNull
    @Column(name = "category_id", nullable = false, unique = true, columnDefinition = "VARCHAR(50)")
    private String categoryId;

    @ManyToOne
    @JoinColumn(name = "parent_category_id")
    private Category parentCategory;

    @Column(name = "level")
    private Integer level;
}
