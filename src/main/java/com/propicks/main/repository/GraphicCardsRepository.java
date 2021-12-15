package com.propicks.main.repository;

import com.propicks.main.entity.GraphicCardsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GraphicCardsRepository extends JpaRepository<GraphicCardsEntity, Integer> {

    List<GraphicCardsEntity> findAllByOrderByGraphicCardRankAsc();

    List<GraphicCardsEntity> findByGraphicCardRankLessThanEqualOrderByGraphicCardRankAsc(Integer graphicCardRank);

    List<GraphicCardsEntity> findByNtileRankLessThanEqualOrderByGraphicCardRankAsc(Integer ntileRank);
}
