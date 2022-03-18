package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.LoginSuccess;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginSuccessRepository extends MongoRepository<LoginSuccess, UUID> { }
