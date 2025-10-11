package ru.itmo.soa.music.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class Int32AlbumsCountAdapter extends XmlAdapter<String, Integer> {
    @Override
    public Integer unmarshal(String v) {
        if (v == null) return null;
        if (!v.matches("^-?\\d+$")) {
            throw new IllegalArgumentException("Field 'albumsCount' must be integer (int32)");
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field 'albumsCount' must be integer (int32)");
        }
    }

    @Override
    public String marshal(Integer v) {
        return v == null ? null : v.toString();
    }
}


