package com.abs.app.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(20)")
    private String name;

    @Column(name = "locality", nullable = false, columnDefinition = "VARCHAR(20)")
    private String locality;

    @Column(name = "address", nullable = false, columnDefinition = "VARCHAR(20)")
    private String address;

    @Column(name = "city", nullable = false, columnDefinition = "VARCHAR(20)")
    private String city;

    @Column(name = "state", nullable = false, columnDefinition = "VARCHAR(20)")
    private String state;

    @Column(name = "pin_code", nullable = false, columnDefinition = "VARCHAR(20)")
    private String pinCode;

    @Column(name = "phone", nullable = false, columnDefinition = "VARCHAR(20)")
    private String phone;
}
