package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "laptop_images")
public class LaptopImagesEntity {

    @Id
    private String id;
    private String name;
    private String imageLinkOne;
    private String imageLinkTwo;
    private String imageLinkThree;
    private String imageLinkFour;
    private String imageLinkFive;

}
