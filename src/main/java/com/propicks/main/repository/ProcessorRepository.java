package com.propicks.main.repository;

import com.propicks.main.entity.ProcessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessorRepository extends JpaRepository<ProcessorEntity, Integer> {

    public List<ProcessorEntity> findAllByOrderByProcessorRankAsc();

    public List<ProcessorEntity> findByProcessorRankLessThanEqualOrderByProcessorRankAsc(Integer processorRank);
}
