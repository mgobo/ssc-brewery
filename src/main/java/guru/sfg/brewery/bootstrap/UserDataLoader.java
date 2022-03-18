package guru.sfg.brewery.bootstrap;

import guru.sfg.brewery.domain.security.Authority;
import guru.sfg.brewery.domain.security.AuthorityUser;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.AuthorityRepository;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class UserDataLoader implements CommandLineRunner {

    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    private int validateUser(String useranme){
        return Optional.ofNullable(this.userRepository.findByUsername(useranme)).isPresent() ? 1 : 0;
    }

    private Authority findAuthority(String role){
        Optional<List<Authority>> values = Optional.ofNullable(this.mongoTemplate.find(new Query(Criteria.where("role").is(role)),Authority.class));
        if(values.isPresent() && !values.get().isEmpty()){
            return values.get().get(0);
        }
        return null;
    }

    private void loadSecurityData(){
        Authority role = this.findAuthority("ROLE_ADMIN");
        if(role == null) {
            role = Authority.builder().role("ROLE_ADMIN").uuid(UUID.randomUUID()).build();
            role = mongoTemplate.save(role);
        }
        User admin = User.builder().username("admin")
                .id(new ObjectId())
                .uuid(UUID.randomUUID())
                .password(encoder.encode("admin"))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .enabled(true)
                .build();
        admin = mongoTemplate.save(admin);
        AuthorityUser authorityAdmin = AuthorityUser.builder().authority(role).user(admin).uuid(UUID.randomUUID()).build();
        mongoTemplate.save(authorityAdmin);

        role = this.findAuthority("ROLE_USER");
        if(role == null) {
            role = Authority.builder().role("ROLE_USER").uuid(UUID.randomUUID()).build();
            role = mongoTemplate.save(role);
        }
        User user = User.builder().username("user")
                .id(new ObjectId())
                .uuid(UUID.randomUUID())
                .password(encoder.encode("password"))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .enabled(true)
                .build();
        user = mongoTemplate.save(user);
        AuthorityUser authorityUser = AuthorityUser.builder().authority(role).user(user).uuid(UUID.randomUUID()).build();
        mongoTemplate.save(authorityUser);

        role = this.findAuthority("ROLE_CUSTOMER");
        if(role == null) {
            role = Authority.builder().role("ROLE_CUSTOMER").uuid(UUID.randomUUID()).build();
            role = mongoTemplate.save(role);
        }
        User scott = User.builder().username("scott")
                .id(new ObjectId())
                .uuid(UUID.randomUUID())
                .password(encoder.encode("tiger"))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .enabled(true)
                .build();
        scott = mongoTemplate.save(scott);
        AuthorityUser authorityScott = AuthorityUser.builder().authority(role).user(scott).uuid(UUID.randomUUID()).build();
        mongoTemplate.save(authorityScott);

        role = this.findAuthority("ROLE_ADMIN");
        if(role == null) {
            role = Authority.builder().role("ROLE_ADMIN").uuid(UUID.randomUUID()).build();
            role = mongoTemplate.save(role);
        }
        User spring = User.builder().username("spring")
                .id(new ObjectId())
                .uuid(UUID.randomUUID())
                .password(encoder.encode("guru"))
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialNonExpired(true)
                .enabled(true)
                .build();
        spring = mongoTemplate.save(spring);
        AuthorityUser authoritySpring = AuthorityUser.builder().authority(role).user(spring).uuid(UUID.randomUUID()).build();
        mongoTemplate.save(authoritySpring);
        log.debug("Users loaded: "+this.userRepository.count());
    }


    @Override
    public void run(String... args) throws Exception {
        mongoTemplate.getCollection("authority_user").drop();
        mongoTemplate.getCollection("authority").drop();
        mongoTemplate.getCollection("user").drop();
        if(this.userRepository.count() == 0) {
            this.loadSecurityData();
        }
    }
}