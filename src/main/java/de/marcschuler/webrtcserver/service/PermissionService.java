package de.marcschuler.webrtcserver.service;

import de.marcschuler.webrtcserver.data.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    public List<String> permissionsForUser(User user){
        return List.of(); //TODO
    }


    public boolean hasPermission(User user, String permission){
        return false; //TODO
    }
}
