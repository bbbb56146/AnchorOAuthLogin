package anchor.oauthlogin.api.service;

import anchor.oauthlogin.api.repository.user.UserRepository;
import anchor.oauthlogin.api.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User getUser(String userId) {
        return userRepository.findByUserId(userId);
    }
}
