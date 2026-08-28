package com.abs.app.domain.entity;

import com.abs.app.domain.entity.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "seller_id", nullable = false, unique = true)
    private String sellerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Embedded
    private BusinessDetails businessDetails = new BusinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pickup_address_id")
    private Address pickupAddress;

    @Column(name = "gstin", columnDefinition = "VARCHAR(20)")
    private String gstin;

    @Column(name = "seller_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus sellerStatus = AccountStatus.PENDING_VERIFICATION;
}
