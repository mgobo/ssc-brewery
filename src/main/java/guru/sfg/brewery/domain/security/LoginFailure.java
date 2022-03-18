package guru.sfg.brewery.domain.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "login_failure")
public class LoginFailure implements Serializable {

    public static final long serialVersionUID = 1L;

    private UUID id;
    private String username;
    private User user;
    private String sourceIp;
    private long createdDate;
    private long lastModified;
    private String date;

}
