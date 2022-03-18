package guru.sfg.brewery.web.controllers.api;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BeerRestControllerIT extends BaseIT {

    @Test
    void findBeers() throws Exception {
        this.mockMvc.perform(get("/api/v1/beer"))
                .andExpect(status().isOk());
    }

    @Test
    void findById() throws Exception {
        this.mockMvc.perform(get("/api/v1/beer/406e921e-3ac1-44cd-80cd-67623a96150f"))
                .andExpect(status().isOk());
    }

    @Test
    void findByUpc() throws Exception {
        this.mockMvc.perform(get("/api/v1/beerUpc/86312234200036"))
                .andExpect(status().isOk());
    }
}
