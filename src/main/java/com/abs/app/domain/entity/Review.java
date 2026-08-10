package com.abs.app.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @Column(name = "review_text", nullable = false, columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "rating", nullable = false)
    private Double rating;

    @ElementCollection
    @CollectionTable(name = "review_product_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url", columnDefinition = "VARCHAR(255)")
    private List<String> productImages;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();
}
