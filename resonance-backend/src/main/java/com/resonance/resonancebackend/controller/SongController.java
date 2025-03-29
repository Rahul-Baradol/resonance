package com.resonance.resonancebackend.controller;

import com.resonance.resonancebackend.dto.UpdateStatus;
import com.resonance.resonancebackend.model.LikedSong;
import com.resonance.resonancebackend.model.Playlist;
import com.resonance.resonancebackend.service.LikedSongsService;
import com.resonance.resonancebackend.service.PlaylistService;
import com.resonance.resonancebackend.service.SpotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class SongController {

    private final PlaylistService playlistService;

    private final LikedSongsService likedSongsService;

    private final SpotifyService spotifyService;

    @Autowired
    public SongController(PlaylistService playlistService, LikedSongsService likedSongsService, SpotifyService spotifyService) {
        this.playlistService = playlistService;
        this.likedSongsService = likedSongsService;
        this.spotifyService = spotifyService;
    }

    @GetMapping("/spotify/playlists/update")
    public ResponseEntity<?> updateSpotifyPlaylists() {
        UpdateStatus updateStatus = spotifyService.updatePlaylists();

        if (updateStatus == UpdateStatus.UPDATED) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "updated playlists from spotify successfully"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "couldn't update the playlists from spotify"));
    }

    @GetMapping("/spotify/likedSongs/update")
    public ResponseEntity<?> updateSpotifyLikedSongs(@RequestParam int limit, @RequestParam int offset) {
        UpdateStatus updateStatus = spotifyService.updateLikedSongs(limit, offset);

        if (updateStatus == UpdateStatus.UPDATED) {
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "updated liked songs from spotify successfully"));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "couldn't update the liked songs from spotify"));
    }

    @GetMapping("/spotify/playlists")
    public List<Playlist> getAllPlaylists() {
        return playlistService.findAllPlaylists();
    }

    @GetMapping("/spotify/likedSongs")
    public List<LikedSong> getAllLikedSongs() {
        return likedSongsService.getAllLikedSongs();
    }
}
