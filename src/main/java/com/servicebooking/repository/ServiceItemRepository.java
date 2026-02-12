package com.servicebooking.repository;

import com.servicebooking.entity.ServiceItem;
import com.servicebooking.entity.ServiceCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findByCategory(ServiceCategory category);
    Page<ServiceItem> findByCategoryId(Long categoryId, Pageable pageable);
    
    @Query("SELECT s FROM ServiceItem s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<ServiceItem> searchByName(@Param("keyword") String keyword, Pageable pageable);
}
