package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "laptops")
public class LaptopEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    private String title;
    private String name;

    private BigDecimal price;
    private String brand;
    private String laptopType;
    private String processor;
    private Integer ram;
    private String graphics;
    private String storageType;
    private Integer storageOne;
    private Integer storageTwo;

    private String display;

    @Column(name = "isTouch")
    private Boolean isTouchscreen;

    @Column(name = "weight")
    private Double weightGrams;
    private Double size;
    private String description;
    private String imageLink;
    private String link;

    private String sponsorId;

}
