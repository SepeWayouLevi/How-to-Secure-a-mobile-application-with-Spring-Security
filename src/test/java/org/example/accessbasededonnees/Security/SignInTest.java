package org.example.accessbasededonnees.Security;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SignInTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void signInTest() throws Exception{

        ResultActions resultActions = mockMvc.perform(post("http://localhost:8080/api/authentication/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"test@mail.com\", \"password\": \"test\"}"))
                .andExpect(status().isOk());
    }

    /**
     * This test will work if the activeTokenRedis is empty.
     * */
    @Test
    public void verifyPresenceOfTokenId() throws Exception {
        signInTest();
        ScanOptions options = ScanOptions.scanOptions().match("*").count(2).build();
        long numberOfTokenIds = options.getCount();
        assertEquals( 2L, numberOfTokenIds);
    }
}
