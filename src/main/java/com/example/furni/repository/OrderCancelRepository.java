package com.example.furni.repository;

import com.example.furni.entity.OrderCancel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderCancelRepository extends JpaRepository<OrderCancel, Integer> {
    @Query("SELECT o.reason, COUNT(o) FROM OrderCancel o GROUP BY o.reason")
    List<Object[]> countByReason();
    Page<OrderCancel> findAll(Pageable pageable);
}
