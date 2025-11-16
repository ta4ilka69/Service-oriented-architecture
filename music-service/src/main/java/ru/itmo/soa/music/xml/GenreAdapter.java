package ru.itmo.soa.music.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import ru.itmo.soa.music.model.Genre;

public class GenreAdapter extends XmlAdapter<String, Genre> {
    private static final String ALLOWED = "[PSYCHEDELIC_CLOUD_RAP, SOUL, POP]";

    @Override
    public Genre unmarshal(String v) {
        if (v == null) return null;
        try {
            String normalized = v.trim().toUpperCase();
            return Genre.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Field 'genre' must be one of " + ALLOWED);
        }
    }

    @Override
    public String marshal(Genre v) {
        return v == null ? null : v.name();
    }
}


