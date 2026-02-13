package com.servicebooking.repository;

import com.servicebooking.entity.User;
import com.servicebooking.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByMobileNumber(String mobileNumber);

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

}
