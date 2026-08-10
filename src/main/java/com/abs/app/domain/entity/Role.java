package com.abs.app.domain.entity;

import com.abs.app.domain.entity.enums.RoleUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name", unique = true)
    @Enumerated(EnumType.STRING)
    private RoleUser roleName = RoleUser.CUSTOMER;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;
}
