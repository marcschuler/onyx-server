package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.File;
import de.marcschuler.webrtcserver.data.User;
import de.marcschuler.webrtcserver.repository.UserRepository;
import de.marcschuler.webrtcserver.service.websocket.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final WebSocketService webSocketService;

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public List<User> all(){
        return userRepository.findAll();
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void setUserAvatar(User user, File f) {
        user.setAvatar(f);
        userRepository.save(user);
        webSocketService.updateServerTree();
    }
}
