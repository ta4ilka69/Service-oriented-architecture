package ru.itmo.soa.music.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class FloatXAdapter extends XmlAdapter<String, Double> {
    @Override
    public Double unmarshal(String v) {
        if (v == null) return null;
        if (!v.matches("^-?\\d+(\\.\\d+)?$")) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }
    }

    @Override
    public String marshal(Double v) {
        return v == null ? null : v.toString();
    }
}


