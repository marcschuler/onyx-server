package de.marcschuler.webrtcserver.controller.v0;

import de.marcschuler.webrtcserver.data.Permission;
import de.marcschuler.webrtcserver.data.policy.PolicyItem;
import de.marcschuler.webrtcserver.dto.AuthChallenge;
import de.marcschuler.webrtcserver.dto.IceServer;
import de.marcschuler.webrtcserver.dto.SignedContent;
import de.marcschuler.webrtcserver.dto.data.ChannelExtendedDTO;
import de.marcschuler.webrtcserver.dto.data.SectionExtendedDTO;
import de.marcschuler.webrtcserver.dto.data.ServerWriteDTO;
import de.marcschuler.webrtcserver.dto.data.UserSimpleDTO;
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
    public UserSimpleDTO userReference(){
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
    @GetMapping("serverwritabledto")
    public ServerWriteDTO serverWritableDTO(){
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
    @GetMapping("permissionType")
    public Permission.PermissionType permissionType(){
        return null;
    }
    @GetMapping("policyItem")
    public PolicyItem policyItem(){
        return null;
    }
}
