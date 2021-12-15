package com.propicks.main.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SoftwareResponse  {

    private Integer id;
    private String name;
    private String minimumProcessor;
    private Integer minimumRam;
    private String minimumGraphics;
    private String recommendedProcessor;
    private Integer recommendedRam;
    private String recommendedGraphics;
    private Integer storageSize;

}
