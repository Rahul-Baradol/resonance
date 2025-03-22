package com.resonance.resonancebackend.service.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@Getter
public class SpotifyClient {

    @Value("${spotify.clientid}")
    private String clientId;

    @Value("${spotify.clientsecret}")
    private String clientSecret;

    @Value("${spotify.redirecturl}")
    private String redirectUrl;

    private String accessToken;

    private String refreshToken;

    private final String tokenUrl = "https://accounts.spotify.com/api/token";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public boolean saveTokens(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String authHeader = Base64.getEncoder().encodeToString((this.clientId + ":" + this.clientSecret).getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + authHeader);

        String requestBody = UriComponentsBuilder.newInstance()
                .queryParam("code", code)
                .queryParam("redirect_uri", this.redirectUrl)
                .queryParam("grant_type", "authorization_code")
                .build().toString().substring(1);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(this.tokenUrl, HttpMethod.POST, requestEntity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            return false;
        }

        this.accessToken = (String) response.getBody().get("access_token");
        this.refreshToken = (String) response.getBody().get("refresh_token");
        return true;
    }

}
