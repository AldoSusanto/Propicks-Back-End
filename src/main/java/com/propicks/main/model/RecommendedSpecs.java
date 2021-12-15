package com.propicks.main.model;

import com.propicks.main.entity.GraphicCardsEntity;
import com.propicks.main.entity.ProcessorEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedSpecs {

    private ProcessorEntity minProcessor;
    private ProcessorEntity recProcessor;
    private Integer minRam;
    private Integer recRam;
    private GraphicCardsEntity minGraphicsCard;
    private GraphicCardsEntity recGraphicsCard;
    private Integer storage;
    private Boolean isMinimum;

}
