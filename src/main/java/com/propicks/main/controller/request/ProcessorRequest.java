package com.propicks.main.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessorRequest {

    private Integer id;
    private String name;
    private Integer processorRank;

}
