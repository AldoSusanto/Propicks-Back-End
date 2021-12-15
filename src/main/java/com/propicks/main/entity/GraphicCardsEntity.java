package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "graphic_cards")
public class GraphicCardsEntity extends BaseEntity{

    @Id
    private Integer id;
    private String name;
    private Integer graphicCardRank;
    private Double benchmark;
    private Integer ntileRank;

}
