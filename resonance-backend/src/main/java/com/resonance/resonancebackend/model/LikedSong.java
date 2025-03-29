package com.resonance.resonancebackend.model;

import com.resonance.resonancebackend.dto.Song;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Data
@Document(collection = "likedSongs")
public class LikedSong {

    @Id
    private String id;

    private Song song;

    public LikedSong(Song song) {
        this.id = generateSongId(song);
        this.song = song;
    }

    private String generateSongId(Song song) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = song.getSongName() + "-" + String.join(",", song.getArtists());
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

}
