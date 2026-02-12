package com.servicebooking.entity;

import com.servicebooking.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    @ManyToOne
    @JoinColumn(name = "provider_id")
    private ProviderProfile provider;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceItem service;

    @Column(nullable = false)
    private LocalDateTime bookingDateTime;

    @Column(nullable = false)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.PENDING;

    private BigDecimal amount;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private String customerName;
    private String providerName;
}
