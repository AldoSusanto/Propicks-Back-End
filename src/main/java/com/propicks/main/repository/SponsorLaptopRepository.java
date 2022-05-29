package com.propicks.main.repository;

import com.propicks.main.entity.SponsorLaptopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface SponsorLaptopRepository extends JpaRepository<SponsorLaptopEntity, String> {

    @Query(value = "SELECT sp.* FROM sponsor_laptops sp INNER JOIN laptops lp " +
            "ON sp.sponsor_id = lp.sponsor_id " +
            "            WHERE ?1 < lp.price " +
            "            AND lp.price < ?2 " +
            "            AND lp.processor IN ?3" +
            "            AND lp.ram >= ?4 " +
            "            AND lp.graphics IN ?5" +
            "            AND lp.is_valid = 1" +
            "            AND sp.is_valid = 1", nativeQuery = true)
    public List<SponsorLaptopEntity> findSuitableSponsorLaptops(BigDecimal min, BigDecimal max, List<String> processorNamesList, Integer ram, List<String> graphicCardsNamesList);

}
