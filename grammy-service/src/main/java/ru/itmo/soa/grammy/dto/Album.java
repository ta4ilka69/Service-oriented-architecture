package ru.itmo.soa.grammy.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "album")
public class Album {
    private String name;
    private Long tracks;

    public Album() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getTracks() { return tracks; }
    public void setTracks(Long tracks) { this.tracks = tracks; }
}


