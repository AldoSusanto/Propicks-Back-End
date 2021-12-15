package com.propicks.main.controller.request.userpicks;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPicks {

    private String priceRange;
    private String pricePref;
    private List<String> activities;
    private List<String> operatingSystem;
    private FilmPicks film;
    private ImageGraphicsPicks imageGraphics;
    private GamingPicks gaming;
    private VideoEditingPicks videoEditing;
    private ThreeDGraphicsPicks threeDGraphics;


    // Secondary Factors
    private String size;
    private List<String> weight;
    private String touchScreen;
    private List<String> brand;

}
