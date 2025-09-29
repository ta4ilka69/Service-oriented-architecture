package ru.itmo.soa.music.dto;

import java.time.LocalDate;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.itmo.soa.music.model.Album;
import ru.itmo.soa.music.model.Coordinates;
import ru.itmo.soa.music.model.Genre;
import ru.itmo.soa.music.xml.LocalDateAdapter;

@XmlRootElement(name = "musicBandAllSchema")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "id",
        "name",
        "coordinates",
        "creationDate",
        "numberOfParticipants",
        "albumsCount",
        "description",
        "genre",
        "bestAlbum"
})
public class MusicBandAllSchema {

    @XmlElement(required = true)
    private Integer id;

    @XmlElement(required = true)
    private String name;

    @XmlElement(required = true)
    private Coordinates coordinates;

    @XmlJavaTypeAdapter(LocalDateAdapter.class)
    private LocalDate creationDate;

    @XmlElement(required = true)
    private Integer numberOfParticipants;

    @XmlElement(required = true)
    private Integer albumsCount;

    @XmlElement(required = true)
    private String description;

    @XmlElement(required = true)
    private Genre genre;

    private Album bestAlbum;

    public MusicBandAllSchema() {}

    // getters and setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }
    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }
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


