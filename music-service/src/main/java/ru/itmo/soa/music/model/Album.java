package ru.itmo.soa.music.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlRootElement(name = "album")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"name", "tracks"})
public class Album {

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private Long tracks; // minimum 1

    public Album() {
    }

    public Album(String name, Long tracks) {
        this.name = name;
        this.tracks = tracks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTracks() {
        return tracks;
    }

    public void setTracks(Long tracks) {
        this.tracks = tracks;
    }
}


