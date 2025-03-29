package com.resonance.resonancebackend.service;

import com.resonance.resonancebackend.model.LikedSong;
import com.resonance.resonancebackend.repository.LikedSongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikedSongsService {

    private final LikedSongRepository likedSongRepository;

    @Autowired
    public LikedSongsService(LikedSongRepository likedSongRepository) {
        this.likedSongRepository = likedSongRepository;
    }

    public List<LikedSong> getAllLikedSongs() {
        return likedSongRepository.findAll();
    }

    public void save(LikedSong likedSong) {
        likedSongRepository.save(likedSong);
    }

    public void saveLikedSongs(List<LikedSong> likedSongs) {
        likedSongs.forEach(this::save);
    }
}