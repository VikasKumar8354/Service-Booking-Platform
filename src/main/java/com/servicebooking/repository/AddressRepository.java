package com.servicebooking.repository;

import com.servicebooking.entity.Address;
import com.servicebooking.entity.CustomerProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomer(CustomerProfile customer);
    List<Address> findByCustomerId(Long customerId);
}
