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

    private String documents;
    private String selectedServices;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProviderStatus status = ProviderStatus.PENDING_APPROVAL;

    private BigDecimal totalEarnings = BigDecimal.ZERO;
    private Integer completedJobs = 0;
    private Double rating = 0.0;
}
