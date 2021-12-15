package com.propicks.main.repository;

import com.propicks.main.entity.SoftwareEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SoftwareRepository extends JpaRepository<SoftwareEntity, Integer> {

    public List<SoftwareEntity> findByNameIn(List<String> names);
}
