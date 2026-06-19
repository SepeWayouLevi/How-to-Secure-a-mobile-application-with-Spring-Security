package org.example.accessbasededonnees.model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@Entity
@Table(name = "region")
public class Region {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_region")
    private Integer idRegion;

    @Column(name = "region_name", nullable = false)
    private String regionName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_country", nullable = false)
    private Country country;


    public Region() {

    }

    public Region(Integer idRegion, String regionName, Country country) {
        this.idRegion = idRegion;
        this.regionName = regionName;
        this.country = country;
    }
}

