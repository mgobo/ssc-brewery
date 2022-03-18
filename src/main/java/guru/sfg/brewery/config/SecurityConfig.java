package guru.sfg.brewery.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.SslSettings;
import guru.sfg.brewery.config.security.RestHeaderAuthFilter;
import guru.sfg.brewery.config.security.RestUrlAuthFilter;
import guru.sfg.brewery.config.security.SfgCustomEncoder;
import guru.sfg.brewery.domain.security.google.Google2faFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.UuidRepresentation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Bean - To NoOpPasswordEncoder (Textplain password)
//    public PasswordEncoder passwordEncoder(){
//        return NoOpPasswordEncoder.getInstance();
//    }

//    @Bean - Sha256 encoder
//    public PasswordEncoder passwordEncoder(){
//        return new StandardPasswordEncoder();
//    }

    private final Google2faFilter google2faFilter;

    @Bean
    public MongoTemplate mongoTemplate(){
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder().applyToSslSettings(ssl->{
            ssl.applySettings(SslSettings.builder().enabled(false).build());
        }).applyToConnectionPoolSettings(pool->{
            pool.maxWaitTime(60l, TimeUnit.SECONDS)
                    .applyConnectionString(new ConnectionString("mongodb://security-nosql:@localhost:27017"))
                    .maxSize(100)
                    .minSize(20)
                    .maxConnectionIdleTime(25l, TimeUnit.SECONDS)
                    .maxConnectionLifeTime(30l, TimeUnit.SECONDS);
        }).uuidRepresentation(UuidRepresentation.STANDARD).build();
        MongoClient mongoClient = MongoClients.create(mongoClientSettings);
        return new MongoTemplate(mongoClient, "security-credentials");
    }

    public RestHeaderAuthFilter restHeaderAuthFilter(AuthenticationManager authenticationManager){
        RestHeaderAuthFilter filter = new RestHeaderAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    public RestUrlAuthFilter restUrlAuthFilter(AuthenticationManager authenticationManager){
        RestUrlAuthFilter filter = new RestUrlAuthFilter(new AntPathRequestMatcher("/api/**"));
        filter.setAuthenticationManager(authenticationManager);
        return filter;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return SfgCustomEncoder.createDelegatingPasswordEncoder();
    }

    protected void configure(HttpSecurity http) throws Exception {
        this.log.debug("Defining criteria for http requests");
        http.addFilterBefore(google2faFilter, SessionManagementFilter.class);
        http.addFilterBefore(restHeaderAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(restUrlAuthFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .csrf().ignoringAntMatchers("/h2-console/**", "/api/**");
        http.authorizeRequests(authorize -> {
            authorize.antMatchers("/",
                    "/webjars/**",
                    "/login",
                    "/resources/**").permitAll()
                    .antMatchers("/h2-console/**").permitAll() //Do not use in production
                    .antMatchers("/beers/find", "/beers/{beerId}").hasAnyRole("ADMIN", "CUSTOMER", "USER")
                    .antMatchers(HttpMethod.GET,"/api/v1/beer/**").hasAnyRole("ADMIN", "CUSTOMER", "USER")
//                    .mvcMatchers(HttpMethod.DELETE,"/api/v1/beer/**").hasAnyRole("ADMIN", "CUSTOMER", "USER")
                    .antMatchers(HttpMethod.GET,"/api/v1/beerUpc/{upc}").hasAnyRole("ADMIN", "CUSTOMER", "USER")
                    .mvcMatchers("/brewery/breweries").hasAnyRole("ADMIN", "CUSTOMER")
                    .mvcMatchers(HttpMethod.GET,"/brewery/api/v1/breweries").hasAnyRole("ADMIN","CUSTOMER");
        })
        .authorizeRequests()
        .anyRequest().authenticated()
        .and()
        .formLogin().and()
        .httpBasic();

        //For h2 console config
        http.headers().frameOptions().sameOrigin();
    }

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        List<User> users = this.mongoTemplate().findAll(User.class);
//        users.forEach(u->{
//            try {
//                auth.inMemoryAuthentication().withUser(u.getUsername())
//                        .password(u.getPassword())
//                        .roles(this.mongoTemplate().findOne(new Query(Criteria.where("userUUID").is(u.getUuid())), Authority.class).getRole());
//            }catch (Exception ex){
//                return;
//            }
//        });
//        auth.inMemoryAuthentication().withUser("admin")
//                .password("{noop}admin")
//                .roles("ADMIN")
//                .and()
//                .withUser("user")
//                .password("{bcrypt}$2a$12$5Jw0g82OvPkhHPsb9tgWheRs7d3o1CsHd6Rhri9WqE6kYdkCzVfbC")
//                .roles("USER")
//                .and()
//                .withUser("spring")
//                .password("{bcrypt}$2y$12$yrpw65uoNnSf34AOrySAD.qPV7HOBmYx2.JciDRNDWRoBdQZ5kMhy")
//                .roles("USER");;
//        auth.inMemoryAuthentication().withUser("tiger")
//                .password("{bcrypt}$2a$12$8t5pc425xGyg0kfD2F2M5O1koAcGEzqqb5ERd9CwlQTFqJulcsAWy")
//                .roles("CUSTOMER");
//    }

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("admin")
//                .roles("ADMIN")
//                .build();
//
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username("user")
//                .password("password")
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(admin,user);
//    }
}
