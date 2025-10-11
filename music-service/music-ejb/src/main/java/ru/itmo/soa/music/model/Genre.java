package ru.itmo.soa.music.model;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;

@XmlType(name = "genre")
@XmlEnum
public enum Genre {
    @XmlEnumValue("PSYCHEDELIC_CLOUD_RAP")
    PSYCHEDELIC_CLOUD_RAP,
    @XmlEnumValue("SOUL")
    SOUL,
    @XmlEnumValue("POP")
    POP;
}


