package com.propicks.main.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Insights {

    private String title;
    private String description;
    private String icon;
    private String type;
    private Integer priority;
}
