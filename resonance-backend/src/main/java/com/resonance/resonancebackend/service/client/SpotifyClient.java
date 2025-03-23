package com.resonance.resonancebackend.service.client;

import com.resonance.resonancebackend.dto.TokenState;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
@Getter
@Slf4j
public class SpotifyClient {

    private String clientId;

    private String clientSecret;

    private String redirectUrl;

    private String accessToken;

    private String refreshToken;

    private final String tokenUrl = "https://accounts.spotify.com/api/token";

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public SpotifyClient(@Value("${spotify.clientid}") String clientId, @Value("${spotify.clientsecret}") String clientSecret, @Value("${spotify.redirecturl}") String redirectUrl) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUrl = redirectUrl;
    }

    private void refreshTokens() {
        try {
            log.debug("Refreshing tokens...");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            String authHeader = Base64.getEncoder().encodeToString((this.clientId + ":" + this.clientSecret).getBytes(StandardCharsets.UTF_8));
            headers.set("Authorization", "Basic " + authHeader);

            String requestBody = UriComponentsBuilder.newInstance()
                    .queryParam("refresh_token", this.refreshToken)
                    .queryParam("grant_type", "refresh_token")
                    .build().toString().substring(1);

            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map<String, String>> response = restTemplate.exchange(this.tokenUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
            });

            if (response.getStatusCode() != HttpStatus.OK) {
                return;
            }

            this.accessToken = response.getBody().get("access_token");
        } catch (Exception exception) {
            log.debug(exception.getMessage());
        }
    }

    private ResponseEntity<String> talk(String URL) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Authorization", "Bearer " + this.accessToken);

            HttpEntity<Void> entity = new HttpEntity<>(httpHeaders);
            return restTemplate.exchange(URL, HttpMethod.GET, entity, String.class);
        } catch (Exception exception) {
            log.debug(exception.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    public ResponseEntity<String> talkTo(String URL) {
        ResponseEntity<String> response = talk(URL);

        if (response.getStatusCode() != HttpStatus.OK) {
            this.refreshTokens();
            response = talk(URL);
        }

        return response;
    }

    public TokenState saveTokens(String code) {
        try {
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

            ResponseEntity<Map<String, String>> response = restTemplate.exchange(this.tokenUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<>() {
            });

            if (response.getStatusCode() != HttpStatus.OK) {
                log.debug("Couldn't save the tokens");
                return TokenState.NOT_SAVED;
            }

            this.accessToken = response.getBody().get("access_token");
            this.refreshToken = response.getBody().get("refresh_token");

            return TokenState.SAVED;
        } catch (Exception exception) {
            log.debug(exception.getMessage());
            return TokenState.UNKNOWN_ERROR;
        }
    }

}
