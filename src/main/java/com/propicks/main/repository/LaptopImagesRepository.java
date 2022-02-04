package com.propicks.main.repository;

import com.propicks.main.entity.LaptopImagesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaptopImagesRepository extends JpaRepository<LaptopImagesEntity, String> {

    List<LaptopImagesEntity> findByNameIn(List<String> name);
}
