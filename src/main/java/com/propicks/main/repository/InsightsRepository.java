package com.propicks.main.repository;

import com.propicks.main.entity.InsightsEntity;
import com.propicks.main.model.Insights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsightsRepository extends JpaRepository<InsightsEntity, String> {
}
