package com.servicebooking.service;

import com.servicebooking.dto.request.AddressRequestDTO;
import com.servicebooking.dto.response.AddressResponseDTO;
import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.Address;
import com.servicebooking.entity.CustomerProfile;
import com.servicebooking.entity.User;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.AddressRepository;
import com.servicebooking.repository.CustomerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final UserService userService;

    // ✅ ADD ADDRESS
    @Transactional
    public ApiResponse<AddressResponseDTO> addAddress(AddressRequestDTO request) {

        User currentUser = userService.getCurrentUser();

        CustomerProfile customer = customerProfileRepository
                .findByUser(currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer profile not found"));

        Address address = new Address();
        address.setCustomer(customer);
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultAddresses(customer);
            address.setIsDefault(true);
        } else {
            address.setIsDefault(false);
        }

        Address saved = addressRepository.save(address);

        return ApiResponse.success(
                "Address added successfully",
                mapToDTO(saved)
        );
    }

    // ✅ GET ALL ADDRESSES
    @Transactional(readOnly = true)
    public ApiResponse<List<AddressResponseDTO>> getAllAddresses() {

        User currentUser = userService.getCurrentUser();

        CustomerProfile customer = customerProfileRepository
                .findByUser(currentUser)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer profile not found"));

        List<AddressResponseDTO> list = addressRepository
                .findByCustomer(customer)
                .stream()
                .map(this::mapToDTO)
                .toList();

        return ApiResponse.success("Addresses fetched successfully", list);
    }

    // ✅ UPDATE ADDRESS
    @Transactional
    public ApiResponse<AddressResponseDTO> updateAddress(Long id,
                                                         AddressRequestDTO request) {

        User currentUser = userService.getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address not found"));

        if (!Objects.equals(
                address.getCustomer().getUser().getId(),
                currentUser.getId())) {
            throw new RuntimeException("Not allowed to update this address");
        }

        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPincode(request.getPincode());

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearDefaultAddresses(address.getCustomer());
            address.setIsDefault(true);
        }

        Address saved = addressRepository.save(address);

        return ApiResponse.success(
                "Address updated successfully",
                mapToDTO(saved)
        );
    }

    // ✅ DELETE ADDRESS
    @Transactional
    public ApiResponse<String> deleteAddress(Long id) {

        User currentUser = userService.getCurrentUser();

        Address address = addressRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Address not found"));

        if (!Objects.equals(
                address.getCustomer().getUser().getId(),
                currentUser.getId())) {
            throw new RuntimeException("Not allowed to delete this address");
        }

        addressRepository.delete(address);

        return ApiResponse.success("Address deleted successfully");
    }

    // ✅ DTO MAPPING METHOD (inside service)
    private AddressResponseDTO mapToDTO(Address address) {
        return AddressResponseDTO.builder()
                .id(address.getId())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .isDefault(address.getIsDefault())
                .build();
    }

    // ✅ CLEAR DEFAULT
    private void clearDefaultAddresses(CustomerProfile customer) {
        List<Address> existing = addressRepository.findByCustomer(customer);
        existing.forEach(a -> a.setIsDefault(false));
        addressRepository.saveAll(existing);
    }
}