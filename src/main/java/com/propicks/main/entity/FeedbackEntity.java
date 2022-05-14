package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "feedback")
public class FeedbackEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Integer score;
    private String message;
    private String jsonPicks;
    private String priceRange;
    private String pricePref;
    private String activities;
    private String graphicsSoftware;
    private String gamingSoftware;
    private String videoSoftware;
    private String graphicsThreedSoftware;
    private String size;
    private String weight;
    private Boolean isTouch;
    private String brandList;

}
