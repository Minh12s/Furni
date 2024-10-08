package com.example.furni.repository;

import com.example.furni.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Integer> {
    boolean existsByMaterialName(String materialName);
    boolean existsByMaterialNameAndIdNot(String materialName, int id);
}