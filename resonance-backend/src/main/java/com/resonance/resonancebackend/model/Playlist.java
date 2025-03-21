package com.resonance.resonancebackend.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "playlists")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
public class Playlist {

    @Id
    private ObjectId id;

    @Field("playlist_id")
    private String playlistId;

    @Field("playlist_name")
    private String playlistName;

    private List<String> songs;
}
