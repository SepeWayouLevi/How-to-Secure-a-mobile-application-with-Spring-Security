package org.example.accessbasededonnees.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(schema = "public",
        name = "country")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id_country")
    private Integer idCountry;

    @Column(name = "country_name", nullable = false)
    @Basic(optional = false)
    private String countryName;

    public Country() {
    }

    public Country(Integer idCountry, String countryName
    ) {
        this.idCountry = idCountry;
        this.countryName = countryName;
    }
}



