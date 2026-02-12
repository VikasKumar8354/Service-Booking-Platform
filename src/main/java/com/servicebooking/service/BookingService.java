package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.dto.response.PageResponse;
import com.servicebooking.entity.*;
import com.servicebooking.enums.BookingStatus;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private ProviderProfileRepository providerProfileRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Transactional
    public ApiResponse<Booking> createBooking(Map<String, Object> request) {
        User currentUser = userService.getCurrentUser();
        
        CustomerProfile customer = customerProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Long serviceId = Long.valueOf(request.get("serviceId").toString());
        ServiceItem service = serviceItemRepository.findById(serviceId)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBookingDateTime(LocalDateTime.parse(request.get("bookingDateTime").toString()));
        booking.setLocation(request.get("location").toString());
        booking.setStatus(BookingStatus.PENDING);
        booking.setAmount(service.getBasePrice());
        booking.setCustomerName(currentUser.getName());

        booking = bookingRepository.save(booking);

        notificationService.createNotification(currentUser.getId(), 
            "Booking Created", "Your booking has been created successfully");

        return ApiResponse.success("Booking created successfully", booking);
    }

    @Transactional
    public ApiResponse<Booking> assignProvider(Long bookingId, Long providerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        ProviderProfile provider = providerProfileRepository.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        booking.setProvider(provider);
        booking.setProviderName(provider.getUser().getName());
        booking = bookingRepository.save(booking);

        notificationService.createNotification(provider.getUser().getId(), 
            "New Booking", "You have been assigned a new booking");

        return ApiResponse.success("Provider assigned successfully", booking);
    }

    @Transactional
    public ApiResponse<Booking> updateStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(status);
        booking = bookingRepository.save(booking);

        notificationService.createNotification(booking.getCustomer().getUser().getId(), 
            "Booking Status Updated", "Your booking status is now: " + status);

        if (status == BookingStatus.COMPLETED && booking.getProvider() != null) {
            ProviderProfile provider = booking.getProvider();
            provider.setCompletedJobs(provider.getCompletedJobs() + 1);
            provider.setTotalEarnings(provider.getTotalEarnings().add(booking.getAmount()));
            providerProfileRepository.save(provider);
        }

        return ApiResponse.success("Booking status updated successfully", booking);
    }

    public ApiResponse<PageResponse<Booking>> getCustomerBookings(int page, int size) {
        User currentUser = userService.getCurrentUser();
        CustomerProfile customer = customerProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Booking> bookingPage = bookingRepository.findByCustomer(customer, pageable);

        return createPageResponse(bookingPage);
    }

    public ApiResponse<PageResponse<Booking>> getProviderBookings(int page, int size) {
        User currentUser = userService.getCurrentUser();
        ProviderProfile provider = providerProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Booking> bookingPage = bookingRepository.findByProvider(provider, pageable);

        return createPageResponse(bookingPage);
    }

    public ApiResponse<PageResponse<Booking>> filterBookings(Map<String, String> filters, int page, int size) {
        Specification<Booking> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.containsKey("customerName")) {
                predicates.add(cb.like(cb.lower(root.get("customerName")), 
                    "%" + filters.get("customerName").toLowerCase() + "%"));
            }

            if (filters.containsKey("providerName")) {
                predicates.add(cb.like(cb.lower(root.get("providerName")), 
                    "%" + filters.get("providerName").toLowerCase() + "%"));
            }

            if (filters.containsKey("status")) {
                predicates.add(cb.equal(root.get("status"), BookingStatus.valueOf(filters.get("status"))));
            }

            if (filters.containsKey("fromDate")) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), 
                    LocalDateTime.parse(filters.get("fromDate") + "T00:00:00")));
            }

            if (filters.containsKey("toDate")) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), 
                    LocalDateTime.parse(filters.get("toDate") + "T23:59:59")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (filters.containsKey("sortBy")) {
            Sort.Direction direction = filters.getOrDefault("sortOrder", "DESC")
                .equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, filters.get("sortBy"));
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Booking> bookingPage = bookingRepository.findAll(spec, pageable);

        return createPageResponse(bookingPage);
    }

    private ApiResponse<PageResponse<Booking>> createPageResponse(Page<Booking> bookingPage) {
        PageResponse<Booking> response = new PageResponse<>(
                bookingPage.getContent(),
                bookingPage.getNumber(),
                bookingPage.getSize(),
                bookingPage.getTotalElements(),
                bookingPage.getTotalPages(),
                bookingPage.isLast()
        );
        return ApiResponse.success("Bookings fetched successfully", response);
    }

    public ApiResponse<String> cancelBooking(Long bookingId) {
        updateStatus(bookingId, BookingStatus.CANCELLED);
        return ApiResponse.success("Booking cancelled successfully", null);
    }

}
