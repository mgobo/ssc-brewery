package guru.sfg.brewery.domain.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "authority_user")
public class AuthorityUser implements Serializable {

    private static final long serialVersionUID = 1l;
    private UUID uuid;

    @DBRef
    private User user;

    @DBRef
    private Authority authority;

}
