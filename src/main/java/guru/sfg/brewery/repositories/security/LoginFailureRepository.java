package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.LoginFailure;
import guru.sfg.brewery.domain.security.LoginSuccess;
import guru.sfg.brewery.domain.security.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Repository
public interface LoginFailureRepository extends MongoRepository<LoginFailure, UUID> {
    List<LoginFailure> findAllByUserAndCreatedDateIsAfter(User user, long timestamp);
}
