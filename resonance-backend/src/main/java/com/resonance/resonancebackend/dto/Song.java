package com.resonance.resonancebackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class Song {

    private String songName;

    private List<String> artists;

}
