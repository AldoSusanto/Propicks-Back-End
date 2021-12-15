package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "laptop_links")
public class LaptopLinksEntity {

    @Id
    private String id;
    private String name;
    private String linkOriginOne;
    private String linkOne;
    private String linkOriginTwo;
    private String linkTwo;
    private String linkOriginThree;
    private String linkThree;

}
