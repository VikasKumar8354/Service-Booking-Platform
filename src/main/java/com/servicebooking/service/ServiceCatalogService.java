package com.servicebooking.service;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.ServiceCategory;
import com.servicebooking.entity.ServiceItem;
import com.servicebooking.exception.BadRequestException;
import com.servicebooking.exception.ResourceNotFoundException;
import com.servicebooking.repository.ServiceCategoryRepository;
import com.servicebooking.repository.ServiceItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ServiceCatalogService {

    @Autowired
    private ServiceCategoryRepository categoryRepository;

    @Autowired
    private ServiceItemRepository serviceItemRepository;

    @Transactional
    public ApiResponse<ServiceCategory> addCategory(Map<String, String> request) {
        if (categoryRepository.existsByName(request.get("name"))) {
            throw new BadRequestException("Category already exists");
        }

        ServiceCategory category = new ServiceCategory();
        category.setName(request.get("name"));
        category.setDescription(request.get("description"));
        category.setIcon(request.getOrDefault("icon", "default-icon"));

        category = categoryRepository.save(category);
        return ApiResponse.success("Category added successfully", category);
    }

    public ApiResponse<List<ServiceCategory>> getAllCategories() {
        List<ServiceCategory> categories = categoryRepository.findAll();
        return ApiResponse.success("Categories fetched successfully", categories);
    }

    @Transactional
    public ApiResponse<ServiceCategory> updateCategory(Long id, Map<String, String> updates) {
        ServiceCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (updates.containsKey("name")) category.setName(updates.get("name"));
        if (updates.containsKey("description")) category.setDescription(updates.get("description"));
        if (updates.containsKey("icon")) category.setIcon(updates.get("icon"));

        category = categoryRepository.save(category);
        return ApiResponse.success("Category updated successfully", category);
    }

    @Transactional
    public ApiResponse<String> deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
        return ApiResponse.success("Category deleted successfully");
    }

    @Transactional
    public ApiResponse<ServiceItem> addService(Map<String, Object> request) {
        Long categoryId = Long.valueOf(request.get("categoryId").toString());
        ServiceCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        ServiceItem service = new ServiceItem();
        service.setCategory(category);
        service.setName(request.get("name").toString());
        service.setDescription(request.get("description").toString());
        service.setBasePrice(new BigDecimal(request.get("basePrice").toString()));

        service = serviceItemRepository.save(service);
        return ApiResponse.success("Service added successfully", service);
    }

    public ApiResponse<List<ServiceItem>> searchServices(String keyword, int page, int size) {
        Page<ServiceItem> servicePage = serviceItemRepository.searchByName(keyword, PageRequest.of(page, size));
        return ApiResponse.success("Services found", servicePage.getContent());
    }

    @Transactional
    public ApiResponse<ServiceItem> updateService(Long id, Map<String, Object> updates) {
        ServiceItem service = serviceItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service not found"));

        if (updates.containsKey("name")) service.setName(updates.get("name").toString());
        if (updates.containsKey("description")) service.setDescription(updates.get("description").toString());
        if (updates.containsKey("basePrice")) service.setBasePrice(new BigDecimal(updates.get("basePrice").toString()));

        service = serviceItemRepository.save(service);
        return ApiResponse.success("Service updated successfully", service);
    }

    @Transactional
    public ApiResponse<String> deleteService(Long id) {
        if (!serviceItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Service not found");
        }
        serviceItemRepository.deleteById(id);
        return ApiResponse.success("Service deleted successfully");
    }
}
