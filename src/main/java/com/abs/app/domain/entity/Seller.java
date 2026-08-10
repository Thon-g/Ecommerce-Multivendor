package com.abs.app.domain.entity;

import com.abs.app.domain.entity.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name = "sellers")
public class Seller {
    @Id
    @Column(name = "seller_id", nullable = false, unique = true)
    private String sellerId;

    @Column(name = "seller_name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String sellerName;

    @Column(name = "phone", columnDefinition = "VARCHAR(20)")
    private String phone;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "VARCHAR(100)")
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Embedded
    private BusinessDetails businessDetails = new BusinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pickup_address_id")
    private Address pickupAddress = new Address();

    @Column(name = "gstin", columnDefinition = "VARCHAR(20)")
    private String GSTIN;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role = new Role();

    @Column(name = "is_email_verified", nullable = false)
    private boolean isEmailVerified = false;

    @Column(name = "user_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus userStatus = AccountStatus.PENDING_VERIFICATION;
}
