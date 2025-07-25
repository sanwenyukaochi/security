package com.sanwenyukaochi.security.controller.test;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WSTestController {
    @MessageMapping("/trade")
    @PreAuthorize("hasAuthority('video:video:view')")
    @SendToUser("/queue/position-updates")
    public Map<String, Object> executeTrade(String trade, Principal principal, Authentication authentication) {
        return Map.of("trade", trade, "principal", principal, "authentication", authentication);
    }
}
