package ru.itmo.soa.music.dto;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "musicBands")
@XmlAccessorType(XmlAccessType.FIELD)
public class MusicBandList {

    @XmlElement(name = "musicBandAllSchema")
    private List<MusicBandAllSchema> items = new ArrayList<>();

    public MusicBandList() {}

    public List<MusicBandAllSchema> getItems() {
        return items;
    }

    public void setItems(List<MusicBandAllSchema> items) {
        this.items = items;
    }
}


