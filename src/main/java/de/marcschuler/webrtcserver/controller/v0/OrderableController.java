package de.marcschuler.webrtcserver.controller.v0;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.UUID;

public interface OrderableController {

    @PutMapping("{id}/order/{newOrder}")
    void order(@PathVariable UUID id, @PathVariable int newOrder);

}
