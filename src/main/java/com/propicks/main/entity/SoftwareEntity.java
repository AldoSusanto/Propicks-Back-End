package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "softwares")
public class SoftwareEntity extends BaseEntity {

    @Id
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
