package guru.sfg.brewery.domain.security;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Document(collection = "user")
public class User implements Serializable{

    private static final Long serialVersionUId = 1l;

    @Id
    private ObjectId id;
    private UUID uuid;
    private String username;
    private String password;

    @Builder.Default
    private Boolean accountNonExpired = false;

    @Builder.Default
    private Boolean accountNonLocked = false;

    @Builder.Default
    private Boolean credentialNonExpired = false;

    @Builder.Default
    private Boolean enabled = false;

    @Builder.Default
    private Boolean useGoogle2f = false;
    private String google2faSecret;

    @Transient
    private Boolean google2faRequired = true;
}
