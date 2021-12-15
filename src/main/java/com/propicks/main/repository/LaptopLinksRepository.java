package com.propicks.main.repository;

import com.propicks.main.entity.LaptopLinksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopLinksRepository extends JpaRepository<LaptopLinksEntity, String> {
}
