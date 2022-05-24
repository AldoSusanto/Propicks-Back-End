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
@Table(name = "sponsor_laptops")
public class SponsorLaptopEntity {

    @Id
    private String sponsorId;
    private String sponsorName;
    private String buttonMessage;
    private String linkTo;
    private Boolean isValid;

}
