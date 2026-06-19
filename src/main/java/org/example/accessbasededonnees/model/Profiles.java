package org.example.accessbasededonnees.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name  ="profiles")
public class Profiles implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private int profileId;

    @Column(name = "profile_description")
    private String profileDescription;

    @Column(name = "profile_name")
    private String profileName;

    public Profiles() {

    }


    // Getters & setters
    public int getProfileId() {
        return profileId;
    }

    @Override
    public String getAuthority(){
        return profileName;
    }

    public void setProfileId(int profileId) {
        this.profileId = profileId;
    }

    public String getProfileDescription() {
        return profileDescription;
    }

    public void setProfileDescription(String profileDescription) {
        this.profileDescription = profileDescription;
    }


    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public Profiles(int profileId, String profileDescription, String profileName
    ) {
        this.profileId = profileId;
        this.profileDescription = profileDescription;
        this.profileName = profileName;
    }
}
