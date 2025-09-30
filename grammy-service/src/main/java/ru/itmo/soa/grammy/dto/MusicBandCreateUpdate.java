package ru.itmo.soa.grammy.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "musicBand")
public class MusicBandCreateUpdate {
    private String name;
    private Coordinates coordinates;
    private Integer numberOfParticipants;
    private Integer albumsCount;
    private String description;
    private String genre;
    @JacksonXmlProperty(localName = "album")
    private Album bestAlbum;

    public static MusicBandCreateUpdate fromAllSchema(MusicBandAllSchema src) {
        MusicBandCreateUpdate dto = new MusicBandCreateUpdate();
        dto.setName(src.getName());
        dto.setCoordinates(src.getCoordinates());
        dto.setNumberOfParticipants(src.getNumberOfParticipants());
        dto.setAlbumsCount(src.getAlbumsCount());
        dto.setDescription(src.getDescription());
        dto.setGenre(src.getGenre());
        dto.setBestAlbum(src.getBestAlbum());
        return dto;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public Integer getNumberOfParticipants() { return numberOfParticipants; }
    public void setNumberOfParticipants(Integer numberOfParticipants) { this.numberOfParticipants = numberOfParticipants; }
    public Integer getAlbumsCount() { return albumsCount; }
    public void setAlbumsCount(Integer albumsCount) { this.albumsCount = albumsCount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    public Album getBestAlbum() { return bestAlbum; }
    public void setBestAlbum(Album bestAlbum) { this.bestAlbum = bestAlbum; }
}


