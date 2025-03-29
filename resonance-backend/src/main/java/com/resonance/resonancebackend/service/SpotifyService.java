package com.resonance.resonancebackend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resonance.resonancebackend.dto.Song;
import com.resonance.resonancebackend.dto.UpdateStatus;
import com.resonance.resonancebackend.model.LikedSong;
import com.resonance.resonancebackend.model.Playlist;
import com.resonance.resonancebackend.service.client.SpotifyClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SpotifyService {

    private final SpotifyClient spotifyClient;

    private final PlaylistService playlistService;

    private final LikedSongsService likedSongsService;

    private final ObjectMapper objectMapper;

    private static final String MY_PLAYLISTS = "https://api.spotify.com/v1/me/playlists";

    private static final String LIKED_SONGS = "https://api.spotify.com/v1/me/tracks?limit=%s&offset=%s";

    public SpotifyService(SpotifyClient spotifyClient, PlaylistService playlistService, LikedSongsService likedSongsService, ObjectMapper objectMapper) {
        this.spotifyClient = spotifyClient;
        this.playlistService = playlistService;
        this.likedSongsService = likedSongsService;
        this.objectMapper = objectMapper;
    }

    public UpdateStatus updateLikedSongs(int limit, int offset) {
        try {
            String formatedURL = String.format(LIKED_SONGS, limit, offset);
            ResponseEntity<String> response = spotifyClient.talkTo(formatedURL);

            JsonNode jsonNodeSongs = objectMapper.readTree(response.getBody());

            List<JsonNode> songs = objectMapper.readValue(jsonNodeSongs.get("items").toString(), new TypeReference<>() {
            });

            List<LikedSong> likedSongs = new ArrayList<>();

            for (JsonNode jsonNodeSong : songs) {
                Song song = new Song();
                String songName = String.valueOf(jsonNodeSong.get("track").get("name")).replaceAll("\\\\\"", "\"");
                List<String> artists = new ArrayList<>();

                List<JsonNode> jsonNodeArtists = objectMapper.readValue(jsonNodeSong.get("track").get("album").get("artists").toString(), new TypeReference<>() {
                });
                for (JsonNode artist : jsonNodeArtists) {
                    String artistName = artist.get("name").toString().replaceAll("\\\\\"", "\"");
                    artists.add(artistName.substring(1, artistName.length() - 1));
                }

                song.setSongName(songName.substring(1, songName.length() - 1));
                song.setArtists(artists);

                LikedSong likedSong = new LikedSong(song);
                likedSongs.add(likedSong);
            }

            likedSongsService.saveLikedSongs(likedSongs);
            return UpdateStatus.UPDATED;
        } catch (Exception exception) {
            log.debug(exception.getMessage());
            return UpdateStatus.NOT_UPDATED;
        }
    }

    public UpdateStatus updatePlaylists() {
        try {
            ResponseEntity<String> response = spotifyClient.talkTo(MY_PLAYLISTS);

            JsonNode jsonNodePlaylists = objectMapper.readTree(response.getBody());
            List<JsonNode> items = objectMapper.readValue(jsonNodePlaylists.get("items").toString(), new TypeReference<>() {
            });

            List<Playlist> playlists = items.stream().map(element -> {
                Playlist playlist = new Playlist();

                String playlistId = String.valueOf(element.get("id"));
                String playlistName = String.valueOf(element.get("name")).replaceAll("\\\\\"", "\"");

                playlist.setPlaylistId(playlistId.substring(1, playlistId.length() - 1));
                playlist.setPlaylistName(playlistName.substring(1, playlistName.length() - 1));
                return playlist;
            }).toList();

            for (int i = 0; i < playlists.size(); i++) {
                Playlist playlist = playlists.get(i);
                String playlistId = playlist.getPlaylistId();

                String URL = "https://api.spotify.com/v1/playlists/" + playlistId + "/tracks";
                ResponseEntity<String> resp = spotifyClient.talkTo(URL);

                JsonNode jsonNodeSongs = objectMapper.readTree(resp.getBody());

                List<JsonNode> songs = objectMapper.readValue(jsonNodeSongs.get("items").toString(), new TypeReference<>() {
                });

                List<Song> playlistSongs = new ArrayList<>();

                for (JsonNode jsonNodeSong : songs) {
                    Song song = new Song();
                    String songName = String.valueOf(jsonNodeSong.get("track").get("name")).replaceAll("\\\\\"", "\"");
                    List<String> artists = new ArrayList<>();

                    List<JsonNode> jsonNodeArtists = objectMapper.readValue(jsonNodeSong.get("track").get("album").get("artists").toString(), new TypeReference<>() {
                    });
                    for (JsonNode artist : jsonNodeArtists) {
                        String artistName = artist.get("name").toString().replaceAll("\\\\\"", "\"");
                        artists.add(artistName.substring(1, artistName.length() - 1));
                    }

                    song.setSongName(songName.substring(1, songName.length() - 1));
                    song.setArtists(artists);
                    playlistSongs.add(song);
                }

                playlists.get(i).setSongs(playlistSongs);
            }

            playlistService.savePlaylists(playlists);
            return UpdateStatus.UPDATED;
        } catch (Exception exception) {
            log.debug(exception.getMessage());
            return UpdateStatus.NOT_UPDATED;
        }
    }


}
