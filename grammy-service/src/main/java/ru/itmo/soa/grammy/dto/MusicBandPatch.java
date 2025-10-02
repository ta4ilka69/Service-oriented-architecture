package ru.itmo.soa.grammy.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "musicBand")
public class MusicBandPatch {
    private Integer numberOfParticipants;
    private Integer albumsCount;

    public MusicBandPatch() {}

    public Integer getNumberOfParticipants() { return numberOfParticipants; }
    public void setNumberOfParticipants(Integer numberOfParticipants) { this.numberOfParticipants = numberOfParticipants; }
    public Integer getAlbumsCount() { return albumsCount; }
    public void setAlbumsCount(Integer albumsCount) { this.albumsCount = albumsCount; }
}


