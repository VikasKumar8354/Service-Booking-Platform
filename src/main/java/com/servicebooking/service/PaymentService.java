package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Booking;
import com.servicebooking.entity.Payment;
import com.servicebooking.enums.PaymentMethod;
import com.servicebooking.enums.PaymentStatus;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.BookingRepository;
import com.servicebooking.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Transactional
    public ApiResponse<Payment> recordPayment(Map<String, Object> request) {
        Long bookingId = Long.valueOf(request.get("bookingId").toString());
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(booking.getAmount());
        payment.setMethod(PaymentMethod.valueOf(request.get("method").toString()));
        payment.setStatus(PaymentStatus.COMPLETED);

        payment = paymentRepository.save(payment);
        return ApiResponse.success("Payment recorded successfully", payment);
    }

    @Transactional
    public ApiResponse<Payment> markComplete(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));

        payment.setStatus(PaymentStatus.COMPLETED);
        payment = paymentRepository.save(payment);

        return ApiResponse.success("Payment marked as complete", payment);
    }

    public ApiResponse<List<Payment>> getPaymentHistory(int page, int size) {
        Page<Payment> paymentPage = paymentRepository.findAll(PageRequest.of(page, size));
        return ApiResponse.success("Payment history fetched", paymentPage.getContent());
    }
}
