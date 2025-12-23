package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.dto.IceServer;
import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.dto.data.PublicUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v0/server/")
@RequiredArgsConstructor
public class TodoController {

    @GetMapping("auth")
    public AuthChallenge auth(){
        return null;
    }

    @GetMapping("userReference")
    public PublicUserDTO userReference(){
        return null;
    }

    @GetMapping("iceServer")
    public IceServer iceServer(){
        return null;
    }
    @GetMapping("signedContent")
    public SignedContent signedContent(){
        return null;
    }
}
