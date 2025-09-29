package ru.itmo.soa.music.xml;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class LocalDateAdapter extends XmlAdapter<String, LocalDate> {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    @Override
    public LocalDate unmarshal(String v) throws Exception {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(v, ISO_DATE);
        } catch (DateTimeParseException e) {
            throw e;
        }
    }

    @Override
    public String marshal(LocalDate v) {
        return v == null ? null : v.format(ISO_DATE);
    }
}


