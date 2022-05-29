package com.propicks.main.repository;

import com.propicks.main.entity.LaptopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface LaptopRepository extends JpaRepository<LaptopEntity, String> {

    @Query(value = "SELECT * FROM laptops lp WHERE ?1 < lp.price " +
            " AND lp.price < ?2 " +
            " AND lp.processor IN ?3 " +
            " AND lp.ram >= ?4 " +
            " AND lp.graphics IN ?5" +
            " AND lp.is_valid = 1", nativeQuery = true)
    public List<LaptopEntity> findSuitableLaptops(BigDecimal min, BigDecimal max, List<String> processorNamesList, Integer ram, List<String> graphicCardsNamesList);

    @Query(value = "SELECT * FROM laptops lp WHERE ?1 < lp.price " +
            " AND lp.price < ?2 " +
            " AND lp.is_valid = 1", nativeQuery = true)
    public List<LaptopEntity> findAnyLaptopsInPriceRange(BigDecimal min, BigDecimal max);

    public Optional<LaptopEntity> findBySponsorId(String sponsorId);

    @Query(value = "SELECT lp.* FROM sponsor_laptops sp INNER JOIN laptops lp " +
            "ON sp.sponsor_id = lp.sponsor_id " +
            "            WHERE ?1 < lp.price " +
            "            AND lp.price < ?2 " +
            "            AND lp.processor IN ?3" +
            "            AND lp.ram >= ?4 " +
            "            AND lp.graphics IN ?5" +
            "            AND lp.is_valid = 1" +
            "            AND sp.is_valid = 1", nativeQuery = true)
    public List<LaptopEntity> findSuitableSponsorLaptops(BigDecimal min, BigDecimal max, List<String> processorNamesList, Integer ram, List<String> graphicCardsNamesList);

}
