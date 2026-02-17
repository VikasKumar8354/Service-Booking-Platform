package com.servicebooking.service;

import com.servicebooking.dto.request.BookingCreateRequest;
import com.servicebooking.dto.response.*;
import com.servicebooking.entity.*;
import com.servicebooking.enums.BookingStatus;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.*;

import jakarta.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerProfileRepository customerRepo;

    @Autowired
    private ProviderProfileRepository providerRepo;

    @Autowired
    private ServiceItemRepository serviceRepo;

    @Autowired
    private UserService userService;

    // ================= CREATE BOOKING =================
    @Transactional
    public ApiResponse<BookingResponseDTO> createBooking(BookingCreateRequest request) {

        User user = userService.getCurrentUser();

        CustomerProfile customer = customerRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        ServiceItem service = serviceRepo.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        Booking booking = new Booking();
        booking.setCustomer(customer);
        booking.setService(service);
        booking.setBookingDateTime(request.getBookingDateTime());
        booking.setLocation(request.getLocation());
        booking.setStatus(BookingStatus.PENDING);
        booking.setAmount(service.getBasePrice());
        booking.setCustomerName(user.getName());

        bookingRepository.save(booking);

        return ApiResponse.success("Booking created successfully", mapToDTO(booking));
    }

    // ================= ASSIGN PROVIDER =================
    @Transactional
    public ApiResponse<BookingResponseDTO> assignProvider(Long bookingId, Long providerId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        ProviderProfile provider = providerRepo.findById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

        booking.setProvider(provider);
        booking.setProviderName(provider.getUser().getName());
        booking.setStatus(BookingStatus.ACCEPTED);

        bookingRepository.save(booking);

        return ApiResponse.success("Provider assigned successfully", mapToDTO(booking));
    }

    // ================= CUSTOMER BOOKINGS =================
    public ApiResponse<PageResponse<BookingResponseDTO>> getCustomerBookings(int page, int size) {

        User user = userService.getCurrentUser();

        CustomerProfile customer = customerRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Page<Booking> bookingPage = bookingRepository.findByCustomer(
                customer,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ApiResponse.success("Bookings fetched", mapPage(bookingPage));
    }

    // ================= PROVIDER BOOKINGS =================
    public ApiResponse<PageResponse<BookingResponseDTO>> getProviderBookings(int page, int size) {

        User user = userService.getCurrentUser();

        ProviderProfile provider = providerRepo.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Provider profile not found"));

        Page<Booking> bookingPage = bookingRepository.findByProvider(
                provider,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ApiResponse.success("Bookings fetched", mapPage(bookingPage));
    }

    // ================= UPDATE STATUS =================
    @Transactional
    public ApiResponse<BookingResponseDTO> updateStatus(Long id, BookingStatus status) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(status);
        bookingRepository.save(booking);

        return ApiResponse.success("Status updated", mapToDTO(booking));
    }

    // ================= CANCEL BOOKING =================
    @Transactional
    public ApiResponse<String> cancelBooking(Long id) {

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        return ApiResponse.success("Booking cancelled successfully", null);
    }

    // ================= FILTER BOOKINGS =================
    public ApiResponse<PageResponse<BookingResponseDTO>> filterBookings(
            Map<String, String> filters,
            int page,
            int size) {

        Specification<Booking> spec = (root, query, cb) -> {

            var predicates = new ArrayList<Predicate>();

            if (filters.containsKey("status")) {
                predicates.add(cb.equal(
                        root.get("status"),
                        BookingStatus.valueOf(filters.get("status"))
                ));
            }

            if (filters.containsKey("customerName")) {
                predicates.add(cb.like(
                        cb.lower(root.get("customerName")),
                        "%" + filters.get("customerName").toLowerCase() + "%"
                ));
            }

            if (filters.containsKey("providerName")) {
                predicates.add(cb.like(
                        cb.lower(root.get("providerName")),
                        "%" + filters.get("providerName").toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Booking> result = bookingRepository.findAll(
                spec,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ApiResponse.success("Filtered bookings", mapPage(result));
    }

    // ================= BY CUSTOMER ID =================
    public ApiResponse<PageResponse<BookingResponseDTO>> getBookingsByCustomerId(
            Long customerId, int page, int size) {

        Page<Booking> bookingPage = bookingRepository.findByCustomerId(
                customerId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ApiResponse.success("Customer bookings fetched", mapPage(bookingPage));
    }

    // ================= BY PROVIDER ID =================
    public ApiResponse<PageResponse<BookingResponseDTO>> getBookingsByProviderId(
            Long providerId, int page, int size) {

        Page<Booking> bookingPage = bookingRepository.findByProviderId(
                providerId,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ApiResponse.success("Provider bookings fetched", mapPage(bookingPage));
    }

    // ================= BY STATUS =================
    public ApiResponse<PageResponse<BookingResponseDTO>> getBookingsByStatus(
            BookingStatus status, int page, int size) {

        Page<Booking> bookingPage = bookingRepository.findByStatus(
                status,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        return ApiResponse.success("Bookings by status fetched", mapPage(bookingPage));
    }

    // ================= COUNT BY STATUS =================
    public ApiResponse<Long> countBookingsByStatus(BookingStatus status) {

        Long count = bookingRepository.countByStatus(status);

        return ApiResponse.success("Booking count fetched", count);
    }

    // ================= BETWEEN DATES =================
    public ApiResponse<List<BookingResponseDTO>> getBookingsBetweenDates(
            LocalDateTime start,
            LocalDateTime end) {

        List<Booking> bookings =
                bookingRepository.findBookingsBetweenDates(start, end);

        List<BookingResponseDTO> dtos =
                bookings.stream().map(this::mapToDTO).toList();

        return ApiResponse.success("Bookings between dates fetched", dtos);
    }

    // ================= DTO MAPPING =================
    private BookingResponseDTO mapToDTO(Booking b) {

        return BookingResponseDTO.builder()
                .bookingId(b.getId())
                .customer(CustomerDTO.builder()
                        .id(b.getCustomer().getId())
                        .name(b.getCustomer().getUser().getName())
                        .email(b.getCustomer().getUser().getEmail())
                        .mobile(b.getCustomer().getUser().getMobileNumber())
                        .build())
                .provider(b.getProvider() == null ? null :
                        ProviderDTO.builder()
                                .id(b.getProvider().getId())
                                .name(b.getProvider().getUser().getName())
                                .rating(b.getProvider().getRating())
                                .build())
                .service(ServiceDTO.builder()
                        .id(b.getService().getId())
                        .name(b.getService().getName())
                        .category(b.getService().getCategory().getName())
                        .price(b.getService().getBasePrice())
                        .build())
                .bookingDateTime(b.getBookingDateTime())
                .location(b.getLocation())
                .status(b.getStatus())
                .amount(b.getAmount())
                .createdAt(b.getCreatedAt())
                .build();
    }

    private PageResponse<BookingResponseDTO> mapPage(Page<Booking> page) {

        return new PageResponse<>(
                page.getContent().stream().map(this::mapToDTO).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}