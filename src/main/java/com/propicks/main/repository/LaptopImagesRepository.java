package com.propicks.main.repository;

import com.propicks.main.entity.LaptopImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopImagesRepository extends JpaRepository<LaptopImagesEntity, String> {

    LaptopImagesEntity findFirstByName(String name);
}
