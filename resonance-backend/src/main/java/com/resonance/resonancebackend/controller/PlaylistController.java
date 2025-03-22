package com.resonance.resonancebackend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.resonance.resonancebackend.model.Playlist;
import com.resonance.resonancebackend.service.PlaylistService;
import com.resonance.resonancebackend.service.SpotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PlaylistController {

    private final PlaylistService playlistService;

    private final SpotifyService spotifyService;

    @Autowired
    public PlaylistController(PlaylistService playlistService, SpotifyService spotifyService) {
        this.playlistService = playlistService;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/spotify/playlists")
    public ResponseEntity<?> getAllSpotifyPlaylists() throws JsonProcessingException {
        return spotifyService.updatePlaylists();
    }

    @GetMapping("/playlists")
    public List<Playlist> getAllPlaylists() {
        return playlistService.findAllPlaylists();
    }
}
