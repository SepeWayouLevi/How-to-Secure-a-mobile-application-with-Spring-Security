package org.example.accessbasededonnees.repository;

import org.example.accessbasededonnees.model.AppUser;
import org.example.accessbasededonnees.model.Profiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface AppUserProfileRepository extends JpaRepository<AppUser, String> {
    @Query("SELECT a.profiles FROM AppUser a WHERE a.email = :email")
    Optional<Profiles> findProfilesByEmail(@Param("email") String email);
}
