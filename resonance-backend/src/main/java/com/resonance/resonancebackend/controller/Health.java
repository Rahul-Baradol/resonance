package com.resonance.resonancebackend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Health {

    @GetMapping("/health")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.noContent().build();
    }

}
