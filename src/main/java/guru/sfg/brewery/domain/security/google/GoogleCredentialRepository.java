package guru.sfg.brewery.domain.security.google;

import com.warrenstrange.googleauth.ICredentialRepository;
import guru.sfg.brewery.domain.security.User;
import guru.sfg.brewery.repositories.security.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class GoogleCredentialRepository implements ICredentialRepository {

    private final UserRepository userRepository;

    @Override
    public String getSecretKey(String username) {
        User user = this.userRepository.findByUsername(username).orElseThrow();
        return user.getGoogle2faSecret();
    }

    @Override
    public void saveUserCredentials(String username, String secretKey, int validationCode, List<Integer> scratchCodes) {
        User user = this.userRepository.findByUsername(username).orElseThrow();
        user.setGoogle2faSecret(secretKey);
        user.setUseGoogle2f(true);
        userRepository.save(user);
    }
}
