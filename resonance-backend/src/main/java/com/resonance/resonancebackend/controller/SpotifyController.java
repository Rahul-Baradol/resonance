package com.resonance.resonancebackend.controller;

import com.resonance.resonancebackend.dto.TokenState;
import com.resonance.resonancebackend.service.client.SpotifyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/spotify/")
public class SpotifyController {

    private final SpotifyClient spotifyClient;

    private final String applicationPassword;

    private final String applicationUrl;

    @Autowired
    public SpotifyController(SpotifyClient spotifyClient, @Value("${application.password}") String applicationPassword, @Value("${application.url}") String applicationUrl) {
        this.spotifyClient = spotifyClient;
        this.applicationPassword = applicationPassword;
        this.applicationUrl = applicationUrl;
    }

    private static String generateRandomString(int length) {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = ThreadLocalRandom.current().nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    @GetMapping("/login")
    public RedirectView login(@RequestParam(name = "password", required = false) String password) {
        if (Objects.isNull(password)) {
            return new RedirectView(applicationUrl + "/friendzoned");
        }

        if (!password.equals(applicationPassword)) {
            return new RedirectView(applicationUrl + "/friendzoned");
        }

        String state = generateRandomString(16);
        String scope = "user-read-private user-read-email";

        String url = "https://accounts.spotify.com/authorize?" +
                "response_type=code" +
                "&client_id=" + URLEncoder.encode(spotifyClient.getClientId(), StandardCharsets.UTF_8) +
                "&scope=" + URLEncoder.encode(scope, StandardCharsets.UTF_8) +
                "&redirect_uri=" + URLEncoder.encode(spotifyClient.getRedirectUrl(), StandardCharsets.UTF_8) +
                "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8);

        return new RedirectView(url);
    }

    @GetMapping("/callback")
    public ResponseEntity<Map> authCallback(@RequestParam(name = "code", required = false) String code, @RequestParam(name = "state", required = false) String state) {
        if (state == null) {
            String errorRedirectUrl = UriComponentsBuilder.fromUriString("/#")
                    .queryParam("error", "state_mismatch")
                    .toUriString();
            return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(errorRedirectUrl)).build();
        }

        TokenState tokenState = spotifyClient.saveTokens(code);
        if (tokenState == TokenState.SAVED) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "tokens saved successfully"));
        }

        if (tokenState == TokenState.NOT_SAVED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "couldn't save the tokens"));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "unknown error while trying to save tokens"));
    }

}
