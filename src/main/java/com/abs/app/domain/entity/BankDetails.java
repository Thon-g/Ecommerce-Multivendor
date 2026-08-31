package com.abs.app.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BankDetails {
    @Column(name = "account_name", columnDefinition = "VARCHAR(100)")
    private String accountName;

    @Column(name = "account_holder_name", columnDefinition = "VARCHAR(100)")
    private String accountHolderName;

    @Column(name = "ifsc_code", columnDefinition = "VARCHAR(20)")
    private String ifscCode;
}
