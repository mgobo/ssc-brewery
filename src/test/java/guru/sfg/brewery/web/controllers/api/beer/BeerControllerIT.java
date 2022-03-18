package guru.sfg.brewery.web.controllers.api.beer;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BeerControllerIT extends BaseIT {

    @DisplayName("Init new Form")
    @Nested
    class InitNewForm {

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void initCreationForm(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/new").with(httpBasic(user, pwd)))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/createBeer"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void initCreationFormNotAuth() throws Exception {
            mockMvc.perform(get("/beers/new"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @DisplayName("Init Find Beer Form")
    @Nested
    class FindForm {

        @Test
        void findBeers() throws Exception {
            mockMvc.perform(get("/beers/find")
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void findBeersTaskWithHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/find").with(httpBasic(user, pwd))
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void findBeersWithHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(get("/beers/find").with(httpBasic(user, pwd))
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isOk())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }

        @Test
        void findBeersWithAnonymous() throws Exception {
            mockMvc.perform(get("/beers/find").with(anonymous())
                            .accept(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(status().isUnauthorized())
                    .andExpect(view().name("beers/findBeers"))
                    .andExpect(model().attributeExists("beer"));
        }
    }

    @DisplayName("Delete tests")
    @Nested
    class DeletingForm {

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void deleteHttpBasic(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/97df8c39-98c4-4ae8-b663-453e8e19c311")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().is2xxSuccessful());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void deleteHttpBasicUserRole(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/97df8c39-98c4-4ae8-b663-453e8e19c311")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void deleteHttpBasicCustomerRole(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/97df8c39-98c4-4ae8-b663-453e8e19c311")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void deleteBeerBadCreeds(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/97df8c39-98c4-4ae8-b663-453e8e19c311")
                            .header("Api-Key", user).header("Api-secret", pwd+"XXXX"))
                    .andExpect(status().isUnauthorized());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void deleteBeerCreedsUrl(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/97df8c39-98c4-4ae8-b663-453e8e19c311")
                            .param("apiKey", user).header("apiSecret", pwd))
                    .andExpect(status().is2xxSuccessful());
        }

        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamAllUsers")
        void deleteBeer(String user, String pwd) throws Exception {
            mockMvc.perform(delete("/api/v1/beer/97df8c39-98c4-4ae8-b663-453e8e19c311")
                            .header("Api-Key", user).header("Api-secret", pwd))
                    .andExpect(status().is2xxSuccessful());
        }
    }
}
