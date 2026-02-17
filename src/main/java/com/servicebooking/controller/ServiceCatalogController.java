package com.servicebooking.controller;

import com.servicebooking.dto.response.ApiResponse;
import com.servicebooking.entity.ServiceCategory;
import com.servicebooking.entity.ServiceItem;
import com.servicebooking.service.ServiceCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@Tag(name = "Service Catalog", description = "Service and category management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ServiceCatalogController {

    @Autowired
    private ServiceCatalogService catalogService;

    // ================= CATEGORY APIs =================

    @PostMapping("/categories")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add category (ADMIN)",
            description = "Create a new service category. Only ADMIN allowed."
    )
    public ResponseEntity<ApiResponse<ServiceCategory>> addCategory(
            @RequestBody Map<String, String> request) {
        return ResponseEntity.ok(catalogService.addCategory(request));
    }

    @GetMapping("/categories")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all categories",
            description = "Fetch all categories. Accessible to any logged-in user."
    )
    public ResponseEntity<ApiResponse<List<ServiceCategory>>> getAllCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get category by ID",
            description = "Fetch a single category using its ID."
    )
    public ResponseEntity<ApiResponse<ServiceCategory>> getCategoryById(
            @PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getCategoryById(id));
    }

    @PutMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update category (ADMIN)",
            description = "Update category details. Only ADMIN allowed."
    )
    public ResponseEntity<ApiResponse<ServiceCategory>> updateCategory(
            @PathVariable Long id,
            @RequestBody Map<String, String> updates) {
        return ResponseEntity.ok(catalogService.updateCategory(id, updates));
    }

    @DeleteMapping("/categories/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete category (ADMIN)",
            description = "Delete category by ID. Only ADMIN allowed."
    )
    public ResponseEntity<ApiResponse<String>> deleteCategory(
            @PathVariable Long id) {
        return ResponseEntity.ok(catalogService.deleteCategory(id));
    }

    // ================= SERVICE APIs =================

    @PostMapping("/items")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Add service item (ADMIN)",
            description = "Create a service under a category. Only ADMIN allowed."
    )
    public ResponseEntity<ApiResponse<ServiceItem>> addService(
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(catalogService.addService(request));
    }

    @GetMapping("/items/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get service by ID",
            description = "Fetch a single service item by ID."
    )
    public ResponseEntity<ApiResponse<ServiceItem>> getServiceById(
            @PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getServiceById(id));
    }


    @GetMapping("/items")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get all services (Paginated)",
            description = "Fetch all services with pagination support."
    )
    public ResponseEntity<ApiResponse<Page<ServiceItem>>> getAllServices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(catalogService.getAllServices(page, size));
    }


    @GetMapping("/items/category/{categoryId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Get services by category (Paginated)",
            description = "Fetch services by category ID with pagination."
    )
    public ResponseEntity<ApiResponse<Page<ServiceItem>>> getServicesByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                catalogService.getServicesByCategory(categoryId, page, size)
        );
    }

    @GetMapping("/search")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Search services (Paginated)",
            description = "Search services by keyword with pagination."
    )
    public ResponseEntity<ApiResponse<List<ServiceItem>>> searchServices(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                catalogService.searchServices(keyword, page, size)
        );
    }

    @PutMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Update service (ADMIN)",
            description = "Update service details. Only ADMIN allowed."
    )
    public ResponseEntity<ApiResponse<ServiceItem>> updateService(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(catalogService.updateService(id, updates));
    }

    @DeleteMapping("/items/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Delete service (ADMIN)",
            description = "Delete service by ID. Only ADMIN allowed."
    )
    public ResponseEntity<ApiResponse<String>> deleteService(
            @PathVariable Long id) {
        return ResponseEntity.ok(catalogService.deleteService(id));
    }
}