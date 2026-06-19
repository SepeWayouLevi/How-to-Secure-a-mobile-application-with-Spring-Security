package org.example.accessbasededonnees.repository;
import org.example.accessbasededonnees.model.Forms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;


public interface FormsRepository extends JpaRepository<Forms, Long> {

    @Query(value = "select * from get_forms_by_email(:email)", nativeQuery = true)
    List<Forms> findFormsByEmail(@Param("email") String email);


    @Query(value = "select * from get_user_requests_with_marking(:email)", nativeQuery = true)
    List<Forms> formsWithMarking(@Param("email") String email);


    @Query(value = "select * from get_user_requests_for_pricing(:email)", nativeQuery = true)
    List<Forms> formsForPricing(@Param("email") String email);

    @Query(
            value = "select * from get_user_requests_for_purchase(:email, :condition)",
            nativeQuery = true
    )
    List<Forms> findUserRequestsForPurchase(
            @Param("email") String email,
            @Param("condition") String condition
    );

    @Query(value = "select * from get_requests_by_country(:email)", nativeQuery = true)
    List<Forms> findFormsByCountry(@Param("email") String email);

}
