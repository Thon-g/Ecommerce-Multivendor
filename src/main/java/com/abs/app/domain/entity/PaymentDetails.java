package com.abs.app.domain.entity;

import com.abs.app.domain.entity.enums.PaymentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
@Embeddable
public class PaymentDetails {
    @Column(name = "payment_id", columnDefinition = "VARCHAR(100)")
    private String paymentId;

    @Column(name = "payment_link_id", columnDefinition = "VARCHAR(100)")
    private String paymentLinkId;

    @Column(name = "payment_link_reference_id", columnDefinition = "VARCHAR(100)")
    private String paymentLinkReferenceId;

    @Column(name = "payment_link_status", columnDefinition = "VARCHAR(50)")
    private String paymentLinkStatus;

    @Column(name = "payment_id_zwsp", columnDefinition = "VARCHAR(100)")
    private String paymentIdZWSP;

    @Column(name = "payment_detail_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
}
