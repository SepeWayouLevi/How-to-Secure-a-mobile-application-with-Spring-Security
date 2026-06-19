package org.example.accessbasededonnees.repository;

import org.example.accessbasededonnees.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Meta;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Map;
import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, String> {

    Optional<AppUser> findByEmail(String email);


    @Meta(comment = "checking user's status account")
    @Query(value = "select * from isUserAccountDisableorExpiredOrLocked(:email)", nativeQuery = true)
    Map<String, Boolean> checkIfUserAccountIsDisabledOrExpiredOrLocked(@Param("email") String email);
}
