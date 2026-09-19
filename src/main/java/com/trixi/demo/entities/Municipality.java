package com.trixi.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="municipalities")
public class Municipality
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;

    @Column(name = "region_id", nullable = false, unique = true)
    private String regionId;
    private String name;
    @OneToMany(mappedBy = "municipality", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("municipality")
    private List<MunicipalPart> locations = new ArrayList<>();

    public void addMunicipalPart(MunicipalPart part)
    {
        locations.add(part);
        part.setMunicipality(this);
    }
    public String getRegionId()
    {
        return regionId;
    }

    public void setRegionId(String regionId)
    {
        this.regionId = regionId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<MunicipalPart> getLocations()
    {
        return locations;
    }

    public void setLocations(List<MunicipalPart> locations)
    {
        this.locations = locations;
    }
}
