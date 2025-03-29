package com.resonance.resonancebackend.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.resonance.resonancebackend.dto.Song;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "playlists")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Playlist {

    @Id
    @Field("playlist_id")
    private String playlistId;

    @Field("playlist_name")
    private String playlistName;

    private List<Song> songs;
}
