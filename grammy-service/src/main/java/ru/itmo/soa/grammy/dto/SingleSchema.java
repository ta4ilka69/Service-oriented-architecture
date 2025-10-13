package ru.itmo.soa.grammy.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.validation.constraints.NotBlank;

@JacksonXmlRootElement(localName = "singleSchema")
public class SingleSchema {
    @NotBlank
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}


