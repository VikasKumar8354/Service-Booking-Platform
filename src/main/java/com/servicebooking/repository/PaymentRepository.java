package com.servicebooking.repository;

import com.servicebooking.entity.Payment;
import com.servicebooking.entity.Booking;
import com.servicebooking.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBooking(Booking booking);
    Optional<Payment> findByBookingId(Long bookingId);
    Page<Payment> findByStatus(PaymentStatus status, Pageable pageable);
    
    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'COMPLETED' AND p.createdAt BETWEEN :startDate AND :endDate")
    BigDecimal calculateRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
}
