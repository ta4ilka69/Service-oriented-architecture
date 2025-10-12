package ru.itmo.soa.music.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Int64TracksAdapter extends XmlAdapter<String, Long> {
    @Override
    public Long unmarshal(String v) {
        if (v == null) return null;
        if (!v.matches("^-?\\d+$")) {
            throw new IllegalArgumentException("Field 'bestAlbum.tracks' must be integer (int64)");
        }
        try {
            return Long.parseLong(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field 'bestAlbum.tracks' must be integer (int64)");
        }
    }

    @Override
    public String marshal(Long v) {
        return v == null ? null : v.toString();
    }
}


