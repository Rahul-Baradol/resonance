package com.resonance.resonancebackend.scheduler;

import com.resonance.resonancebackend.dto.UpdateStatus;
import com.resonance.resonancebackend.service.SpotifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@EnableScheduling
@Slf4j
@Service
public class UpdatePlaylists {

    private final SpotifyService spotifyService;

    public UpdatePlaylists(SpotifyService spotifyService) {
        this.spotifyService = spotifyService;
    }

    @Scheduled(fixedDelayString = "${scheduler.fixedDelayInSeconds}")
    private void updatePlaylists() {
        log.debug("Updating playlists...");
        UpdateStatus updateStatus = spotifyService.updatePlaylists();
        log.debug("Spotify Playlist Update Status: " + updateStatus.name());
    }
}
