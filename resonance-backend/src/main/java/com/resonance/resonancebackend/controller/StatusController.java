package com.resonance.resonancebackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@Slf4j
public class StatusController {

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> hello() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Resonance..."));
    }

    @GetMapping("/health")
    public ResponseEntity<Void> ping() {
        log.info("Ping!");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/friendzoned")
    public ResponseEntity<Map<String, String>> friendZoned() {
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "you are not supposed to go there, my friend..."));
    }

}
