package guru.sfg.brewery.web.controllers.api.customer;

import guru.sfg.brewery.web.controllers.BaseIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class CustomerControllerIT extends BaseIT {

    @ParameterizedTest(name = "#{index} with [{arguments}]")
    @MethodSource(value = "getStreamAllUsers")
    void testListCustomerAUTH(String user, String pwd) throws Exception {
        this.mockMvc.perform(get("/customers")
                .with(httpBasic(user,pwd)))
                .andExpect(status().isOk());
    }

    @Test
    void testListCustomerNOAUTH() throws Exception {
        this.mockMvc.perform(get("/customers")
                        .with(httpBasic("user","password")))
                .andExpect(status().isForbidden());
    }

    @Test
    void testListCustomerNOTLOGGEDIN() throws Exception {
        this.mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("Add Customers")
    @Nested
    class AddCustomers {
        @Rollback
        @Test
        void processCreationForm() throws Exception {
            mockMvc.perform(post("/customers/new").with(csrf())
                    .param("customerName", "Foo Customer")
                    .with(httpBasic("spring", "guru")))
                    .andExpect(status().is3xxRedirection());
        }

        @Rollback
        @ParameterizedTest(name = "#{index} with [{arguments}]")
        @MethodSource("guru.sfg.brewery.web.controllers.api.beer.BeerControllerIT#getStreamNotAdmin")
        void processCreationFormNOAUTH(String user, String pwd) throws Exception {
            mockMvc.perform(post("/customers/new").with(csrf())
                            .param("customerName", "Foo Customer")
                            .with(httpBasic(user, pwd)))
                    .andExpect(status().isForbidden());
        }

        @Rollback
        @Test
        void processCreationFormNOAUTH() throws Exception {
            mockMvc.perform(post("/customers/new").with(csrf())
                            .param("customerName", "Foo Customer")
                            .with(httpBasic("spring", "guru")))
                    .andExpect(status().isUnauthorized());
        }
    }
}
