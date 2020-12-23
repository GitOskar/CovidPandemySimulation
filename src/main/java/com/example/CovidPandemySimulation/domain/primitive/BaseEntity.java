package com.example.CovidPandemySimulation.domain.primitive;

import javax.persistence.*;

@MappedSuperclass
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    public boolean isNew() {
        return id == null;
    }

    public Integer getId() {
        return id;
    }
}