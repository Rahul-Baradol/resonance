package com.resonance.resonancebackend.repository;

import com.resonance.resonancebackend.model.Playlist;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, ObjectId> {
    List<Playlist> findAll();


}
