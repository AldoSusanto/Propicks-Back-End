package com.propicks.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreSheet {

    private String laptopId;
    private Double totalScore;

    private Double priceScore;
    private Double processorScore;
    private Double processorMaxScore;
    private Double ramScore;
    private Double ramMaxScore;
    private Double graphicsScore;
    private Double graphicsMaxScore;
    private Double primaryTotalScore;

    private Double weightScore;
    private Double weightMaxScore;
    private Double sizeScore;
    private Double sizeMaxScore;
    private Double touchscreenScore;
    private Double touchscreenMaxScore;
    private Double brandScore;
    private Double brandMaxScore;
    private Double secondaryTotalScore;


}
