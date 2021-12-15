package com.propicks.main.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorResponse {

    private Integer id;
    private String name;
    private Integer processorRank;

}
