package guru.sfg.brewery.web.controllers.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
class CorsIT {

    @WithUserDetails("spring")
    @Test
        
}
