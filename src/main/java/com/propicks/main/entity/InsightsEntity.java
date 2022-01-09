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
@Table(name = "insights")
public class InsightsEntity {

    @Id
    private String id;
    private String title;
    private String icon;
    private String description;
    private String type;
}
