package de.marcschuler.onyxserver.controller.v0;

import de.marcschuler.onyxserver.dto.AuthChallenge;
import de.marcschuler.onyxserver.dto.IceServer;
import de.marcschuler.onyxserver.dto.SignedContent;
import de.marcschuler.onyxserver.dto.data.ChannelExtendedDTO;
import de.marcschuler.onyxserver.dto.data.SectionExtendedDTO;
import de.marcschuler.onyxserver.dto.data.UserOnlineDTO;
import de.marcschuler.onyxserver.dto.data.UserSimpleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Quick and dirty helper controller to embed all DTOs
 * we might need for messaging but not (yet) for controller
 * to appear on openapi spec
 */
@RestController
@RequestMapping("/v0/server/")
@RequiredArgsConstructor
public class TodoController {

    @GetMapping("auth")
    public AuthChallenge auth(){
        return null;
    }

    @GetMapping("userReference")
    public UserSimpleDTO userReference(){
        return null;
    }
    @GetMapping("useronline")
    public UserOnlineDTO userOnline(){
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

    @GetMapping("usersimpledto")
    public UserSimpleDTO userSimpleDTO(){
        return null;
    }
    @GetMapping("channelExtendedDTO")
    public ChannelExtendedDTO channelExtendedDTO(){
        return null;
    }
    @GetMapping("sectionextendeddto")
    public SectionExtendedDTO sectionExtendedDTO(){
        return null;
    }
}
