package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.enums.BookingStatus;
import com.servicebooking.repository.BookingRepository;
import com.servicebooking.repository.PaymentRepository;
import com.servicebooking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public ApiResponse<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("pendingBookings", bookingRepository.countByStatus(BookingStatus.PENDING));
        stats.put("completedBookings", bookingRepository.countByStatus(BookingStatus.COMPLETED));

        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = LocalDateTime.now();
        BigDecimal monthlyRevenue = paymentRepository.calculateRevenueBetweenDates(monthStart, monthEnd);
        stats.put("monthlyRevenue", monthlyRevenue != null ? monthlyRevenue : BigDecimal.ZERO);

        return ApiResponse.success("Dashboard stats fetched", stats);
    }

    public ApiResponse<Map<String, Object>> getMonthlyReport() {
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime monthEnd = LocalDateTime.now();

        Map<String, Object> report = new HashMap<>();
        report.put("totalBookings", bookingRepository.findBookingsBetweenDates(monthStart, monthEnd).size());
        report.put("revenue", paymentRepository.calculateRevenueBetweenDates(monthStart, monthEnd));
        report.put("period", monthStart.getMonth() + " " + monthStart.getYear());

        return ApiResponse.success("Monthly report generated", report);
    }
}
