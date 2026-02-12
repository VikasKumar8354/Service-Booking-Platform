package com.servicebooking.repository;

import com.servicebooking.entity.Booking;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.entity.ProviderProfile;
import com.servicebooking.enums.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    Page<Booking> findByCustomer(CustomerProfile customer, Pageable pageable);
    Page<Booking> findByCustomerId(Long customerId, Pageable pageable);
    Page<Booking> findByProvider(ProviderProfile provider, Pageable pageable);
    Page<Booking> findByProviderId(Long providerId, Pageable pageable);
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = :status")
    Long countByStatus(@Param("status") BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                           @Param("endDate") LocalDateTime endDate);
}
