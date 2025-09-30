package ru.itmo.soa.grammy.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "participantSchema")
public class ParticipantSchema {
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}


