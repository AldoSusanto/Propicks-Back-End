package com.propicks.main.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "processors")
public class ProcessorEntity extends BaseEntity {

    @Id
    private Integer id;
    private String name;
    private Integer processorRank;

}
