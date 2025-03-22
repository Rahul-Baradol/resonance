package com.resonance.resonancebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@Slf4j
public class Health {

    @GetMapping("/health")
    public ResponseEntity<Void> ping() {
        log.debug("Ping!");
        return ResponseEntity.noContent().build();
    }

}
