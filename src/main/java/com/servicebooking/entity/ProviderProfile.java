package com.servicebooking.entity;

import com.servicebooking.enums.ProviderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "provider_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ✅ BLOB STORAGE
    @Lob
    @Column(name = "document_data", columnDefinition = "LONGBLOB")
    private byte[] documentData;

    // ✅ store mime type
    private String documentType;

    // ✅ store original file name
    private String documentName;

    private String selectedServices;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderStatus status = ProviderStatus.PENDING_APPROVAL;

    @Column(nullable = false)
    private BigDecimal totalEarnings = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer completedJobs = 0;

    @Column(nullable = false)
    private Double rating = 0.0;
}