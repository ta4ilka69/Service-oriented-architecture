package ru.itmo.soa.music.model;

import java.io.Serializable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import ru.itmo.soa.music.xml.FloatXAdapter;
import ru.itmo.soa.music.xml.Int64YAdapter;

@XmlRootElement(name = "coordinates")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"x", "y"})
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(FloatXAdapter.class)
    private Double x;

    @XmlElement(required = true)
    @XmlJavaTypeAdapter(Int64YAdapter.class)
    private Long y;

    public Coordinates() {
    }

    public Coordinates(Double x, Long y) {
        this.x = x;
        this.y = y;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Long getY() {
        return y;
    }

    public void setY(Long y) {
        this.y = y;
    }
}


