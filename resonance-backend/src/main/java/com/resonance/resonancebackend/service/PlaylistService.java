package com.resonance.resonancebackend.service;

import com.resonance.resonancebackend.model.Playlist;
import com.resonance.resonancebackend.repository.PlaylistRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public List<Playlist> findAllPlaylists() {
        return playlistRepository.findAll();
    }

    public void savePlaylist(Playlist playlist) {
        playlistRepository.save(playlist);
    }
}

