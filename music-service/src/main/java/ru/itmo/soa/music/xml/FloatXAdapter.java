package ru.itmo.soa.music.xml;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;

public class FloatXAdapter extends XmlAdapter<String, Double> {
    @Override
    public Double unmarshal(String v) {
        if (v == null)
            return null;
        if (!v.matches("^-?\\d+([\\.,]\\d+)?$")) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }
        String normalized = v.replace(',', '.');
        BigDecimal bd;
        try {
            bd = new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }

        int scale = Math.max(0, bd.stripTrailingZeros().scale());
        if (scale > 5) {
            throw new IllegalArgumentException("Field 'coordinates.x' must have at most 5 digits after decimal");
        }

        if (bd.compareTo(BigDecimal.valueOf(-975)) <= 0) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be greater than -975");
        }
        try {
            return bd.doubleValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Field 'coordinates.x' must be number (float)");
        }
    }

    @Override
    public String marshal(Double v) {
        return v == null ? null : v.toString();
    }
}
