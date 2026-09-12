package com.abs.app.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class BusinessDetails {
    @Column(name = "business_name", columnDefinition = "VARCHAR(100)")
    private String businessName;

    @Column(name = "business_email", columnDefinition = "VARCHAR(100)")
    private String businessEmail;

    @Column(name = "business_phone", columnDefinition = "VARCHAR(20)")
    private String businessPhone;

    @Column(name = "business_address", columnDefinition = "VARCHAR(255)")
    private String businessAddress;

    @Column(name = "logo", columnDefinition = "VARCHAR(255)")
    private String logo;

    @Column(name = "banner", columnDefinition = "VARCHAR(255)")
    private String banner;
}
