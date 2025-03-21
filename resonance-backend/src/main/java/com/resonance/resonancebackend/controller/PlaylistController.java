package com.resonance.resonancebackend.controller;

import com.resonance.resonancebackend.model.Playlist;
import com.resonance.resonancebackend.service.PlaylistService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class PlaylistController {

    private final PlaylistService playlistService;

    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/playlists")
    public List<Playlist> getAllPlaylists() {
        List<Playlist> playlist = playlistService.findAllPlaylists();
        System.out.println(playlist);
        return playlist;
    }

    @PostMapping("/playlist")
    public ResponseEntity<Void> addPlaylist(@RequestBody Playlist playlist) {
        playlist.setId(new ObjectId());
        playlistService.savePlaylist(playlist);
        return ResponseEntity.noContent().build();
    }
}
