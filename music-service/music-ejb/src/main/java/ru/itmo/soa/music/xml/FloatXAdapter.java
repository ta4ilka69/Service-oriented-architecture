package ru.itmo.soa.music.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class FloatXAdapter extends XmlAdapter<String, Double> {
    @Override
    public Double unmarshal(String v) {
        if (v == null)
            return null;
        if (!v.matches("^-?\\d+([\\.,]\\d+)?$")) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }
        int sepIndex = Math.max(v.indexOf('.'), v.indexOf(','));
        if (sepIndex >= 0) {
            int fractionalDigits = v.length() - sepIndex - 1;
            if (fractionalDigits > 5) {
                throw new IllegalArgumentException("Field 'coordinates.x' must have at most 5 digits after decimal");
            }
        }
        try {
            String normalized = v.replace(',', '.');
            return Double.parseDouble(normalized);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }
    }

    @Override
    public String marshal(Double v) {
        return v == null ? null : v.toString();
    }
}


