package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.ServiceCategory;
import com.servicebooking.entity.ServiceItem;
import com.servicebooking.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Service Catalog", description = "Service and category management")
@SecurityRequirement(name = "bearerAuth")
public class ServiceCatalogController {

    @Autowired
    private ServiceCatalogService catalogService;

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add service category")
    public ResponseEntity<ApiResponse<ServiceCategory>> addCategory(@RequestBody Map<String, String> request) {
        return ResponseEntity.ok(catalogService.addCategory(request));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories")
    public ResponseEntity<ApiResponse<List<ServiceCategory>>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update category")
    public ResponseEntity<ApiResponse<ServiceCategory>> updateCategory(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        return ResponseEntity.ok(catalogService.updateCategory(id, updates));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete category")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.deleteCategory(id));
    }

    @PostMapping("/items")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add service item")
    public ResponseEntity<ApiResponse<ServiceItem>> addService(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(catalogService.addService(request));
    }

    @GetMapping("/search")
    @Operation(summary = "Search services")
    public ResponseEntity<ApiResponse<List<ServiceItem>>> searchServices(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(catalogService.searchServices(keyword, page, size));
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update service item")
    public ResponseEntity<ApiResponse<ServiceItem>> updateService(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(catalogService.updateService(id, updates));
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete service item")
    public ResponseEntity<ApiResponse<String>> deleteService(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.deleteService(id));
    }
}
