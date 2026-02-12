package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Address;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.entity.User;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.AddressRepository;
import com.servicebooking.repository.CustomerProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CustomerProfileRepository customerProfileRepository;

    @Autowired
    private UserService userService;

    @Transactional
    public ApiResponse<Address> addAddress(Map<String, Object> request) {
        User currentUser = userService.getCurrentUser();
        CustomerProfile customer = customerProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        Address address = new Address();
        address.setCustomer(customer);
        address.setAddressLine(request.get("addressLine").toString());
        address.setCity(request.get("city").toString());
        address.setState(request.get("state").toString());
        address.setPincode(request.get("pincode").toString());
        address.setIsDefault(Boolean.parseBoolean(request.getOrDefault("isDefault", "false").toString()));

        if (address.getIsDefault()) {
            List<Address> existingAddresses = addressRepository.findByCustomer(customer);
            existingAddresses.forEach(addr -> {
                addr.setIsDefault(false);
                addressRepository.save(addr);
            });
        }

        address = addressRepository.save(address);
        return ApiResponse.success("Address added successfully", address);
    }

    public ApiResponse<List<Address>> getAllAddresses() {
        User currentUser = userService.getCurrentUser();
        CustomerProfile customer = customerProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Customer profile not found"));

        List<Address> addresses = addressRepository.findByCustomer(customer);
        return ApiResponse.success("Addresses fetched successfully", addresses);
    }

    @Transactional
    public ApiResponse<Address> updateAddress(Long id, Map<String, Object> updates) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        if (updates.containsKey("addressLine")) address.setAddressLine(updates.get("addressLine").toString());
        if (updates.containsKey("city")) address.setCity(updates.get("city").toString());
        if (updates.containsKey("state")) address.setState(updates.get("state").toString());
        if (updates.containsKey("pincode")) address.setPincode(updates.get("pincode").toString());

        address = addressRepository.save(address);
        return ApiResponse.success("Address updated successfully", address);
    }

    @Transactional
    public ApiResponse<String> deleteAddress(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new ResourceNotFoundException("Address not found");
        }
        addressRepository.deleteById(id);
        return ApiResponse.success("Address deleted successfully");
    }
}
