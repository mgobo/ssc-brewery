package guru.sfg.brewery.web.controllers.api.brewery;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BreweryControllerTest extends BaseIT {

    @Test
    void getAllBreweriesCUSTOMER() throws Exception{
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("scott", "tiger")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getAllBreweriesUSER() throws Exception{
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("user", "password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllBreweriesADMIN() throws Exception{
        mockMvc.perform(get("/brewery/api/v1/breweries")
                        .with(httpBasic("spring", "guru")))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void getAllNOAUTH() throws Exception{
        mockMvc.perform(get("/brewery/api/v1/breweries"))
                .andExpect(status().isUnauthorized());
    }

}
