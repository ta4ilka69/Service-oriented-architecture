package ru.itmo.soa.music.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import ru.itmo.soa.music.model.Album;
import ru.itmo.soa.music.model.Coordinates;
import ru.itmo.soa.music.model.Genre;

@XmlRootElement(name = "musicBand")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "name",
        "coordinates",
        "numberOfParticipants",
        "albumsCount",
        "description",
        "genre",
        "bestAlbum"
})
public class MusicBandCreateUpdate {
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private Coordinates coordinates;
    @XmlElement(required = true)
    private Integer numberOfParticipants;
    @XmlElement(required = true)
    private Integer albumsCount;
    @XmlElement(required = true)
    private String description;
    @XmlElement(required = true)
    private Genre genre;
    private Album bestAlbum;

    public MusicBandCreateUpdate() {}

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
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public Album getBestAlbum() { return bestAlbum; }
    public void setBestAlbum(Album bestAlbum) { this.bestAlbum = bestAlbum; }
}


