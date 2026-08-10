package com.abs.app.domain.entity;

import com.abs.app.domain.entity.enums.HomeCategorySection;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "home_categories")
public class HomeCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "home_category_id")
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String name;

    @Column(name = "image", columnDefinition = "VARCHAR(255)")
    private String image;

    @Column(name = "category_id", columnDefinition = "VARCHAR(50)")
    private String categoryId;

    @Column(name = "section", nullable = false)
    @Enumerated(EnumType.STRING)
    private HomeCategorySection section;
}
