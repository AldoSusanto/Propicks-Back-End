package com.propicks.main.controller.response;

import com.propicks.main.model.Insights;
import com.propicks.main.model.LaptopLinks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaptopResponse {

    private String id;
    private String title;
    private String name;
    private BigDecimal price;
    private Boolean isSponsored;
    private String sponsorId;
    private String brand;
    private String laptopType;
    private String processor;
    private Integer ram;
    private String graphics;
    private String storageType;
    private Integer storageOne;
    private Integer storageTwo;
    private String display;
    private Boolean isTouchscreen;
    private Double weightGrams;
    private Double size;
    private String description;
    private String sponsorName;
    private String buttonMessage;
    private String reason;
    private String review;
    private List<String> imageLink;
    private List<LaptopLinks> link;
    private List<Insights> insights;

}
