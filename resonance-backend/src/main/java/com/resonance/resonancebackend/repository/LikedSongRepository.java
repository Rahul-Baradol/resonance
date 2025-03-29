package com.resonance.resonancebackend.repository;

import com.resonance.resonancebackend.model.LikedSong;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedSongRepository extends MongoRepository<LikedSong, String> {
}