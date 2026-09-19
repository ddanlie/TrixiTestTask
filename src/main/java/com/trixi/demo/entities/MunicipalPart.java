package com.trixi.demo.entities;

import jakarta.persistence.*;

@Entity
@Table(name="municipal_parts")
public class MunicipalPart
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pk;
    private String partId;
    private String name;
    @Column(name = "region_id")
    private String regionId;

    public String getRegionId()
    {
        return regionId;
    }

    public void setRegionId(String regionId)
    {
        this.regionId = regionId;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="region_id", referencedColumnName = "region_id", insertable = false, updatable = false)
    private Municipality municipality;

    public String getPartId()
    {
        return partId;
    }

    public void setPartId(String partId)
    {
        this.partId = partId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Municipality getMunicipality()
    {
        return municipality;
    }

    public void setMunicipality(Municipality municipality)
    {
        this.municipality = municipality;
    }
}
