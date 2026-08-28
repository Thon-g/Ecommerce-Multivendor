package com.abs.app.domain.entity;

import com.abs.app.domain.entity.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

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

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "shop_name", nullable = false, columnDefinition = "VARCHAR(100)")
    private String shopName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pickup_address_id")
    private Address pickupAddress;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.PENDING_VERIFICATION;

    @Embedded
    private BusinessDetails businessDetails = new BusinessDetails();

    @Embedded
    private BankDetails bankDetails = new BankDetails();

    @Column(name = "gstin", columnDefinition = "VARCHAR(20)")
    private String GSTIN;

    @OneToMany(mappedBy = "seller")
    private Set<Product> products = new HashSet<>();

    @OneToMany(mappedBy = "seller")
    private Set<Transaction> transactions = new HashSet<>();

    @OneToOne(mappedBy = "seller", cascade = CascadeType.ALL)
    private SellerReport sellerReport;
}
