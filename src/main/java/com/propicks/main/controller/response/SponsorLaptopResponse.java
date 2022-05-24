package com.propicks.main.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SponsorLaptopResponse {

    private String sponsorId;
    private LaptopResponse laptop;
    private String sponsorName;
    private String buttonMessage;
    private String linkTo;
    private Boolean isValid;

}
