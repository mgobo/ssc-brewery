package guru.sfg.brewery.repositories.security;

import guru.sfg.brewery.domain.security.Authority;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface AuthorityRepository extends MongoRepository<Authority, UUID> {
}
