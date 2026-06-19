package org.example.accessbasededonnees.repository;
import org.example.accessbasededonnees.model.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;


    @Test
    public void findAUser(){
        Optional<AppUser> appUser =  appUserRepository.findByEmail("test@mail.com");
        assertTrue(appUser.isPresent());

        Optional<AppUser> secondUser =  appUserRepository.findByEmail("andrelevi@gmail.com");
        assertTrue(secondUser.isPresent());
    }
}
