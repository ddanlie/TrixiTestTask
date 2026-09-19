package com.trixi.demo.repository;

import com.trixi.demo.entities.MunicipalPart;
import com.trixi.demo.entities.Municipality;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
@Repository
public class XMLRepository
{
    private MunicipalityRepository municipalityRepository;
    private MunicipalPartRepository municipalPartRepository;
    @Autowired
    public XMLRepository(
            MunicipalityRepository municipalityRepository,
            MunicipalPartRepository municipalPartRepository
    )
    {
        this.municipalityRepository = municipalityRepository;
        this.municipalPartRepository = municipalPartRepository;
    }

    @Transactional
    public void parseAndSave(ZipInputStream input) throws Exception
    {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true);
        factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        XMLStreamReader reader = factory.createXMLStreamReader(input);

        List<Municipality> obceBatch = new ArrayList<>();
        List<MunicipalPart> castiBatch = new ArrayList<>();

        while (reader.hasNext()) {
            int event = reader.next();

            if (event == XMLStreamConstants.START_ELEMENT)
            {
                String localName = reader.getLocalName();

                if ("Obce".equals(localName))
                {
                    while (reader.hasNext())
                    {
                        event = reader.next();
                        if(event == XMLStreamConstants.START_ELEMENT)
                        {
                            localName = reader.getLocalName();
                            if("Obec".equals(localName))
                            {
                                obceBatch.add(parseObec(reader));
                                if (obceBatch.size() >= 100)
                                {
                                    municipalityRepository.saveAll(obceBatch);
                                    obceBatch.clear();
                                }
                            }
                        }
                        else if (event == XMLStreamConstants.END_ELEMENT && "Obce".equals(reader.getLocalName()))
                        {
                            break;
                        }
                    }
                }
                else if ("CastObce".equals(localName))
                {
                    castiBatch.add(parseCastObce(reader));
                    if (castiBatch.size() >= 100)
                    {
                        municipalPartRepository.saveAll(castiBatch);
                        castiBatch.clear();
                    }
                }
            }
            else if (event == XMLStreamConstants.END_ELEMENT && "CastiObci".equals(reader.getLocalName()))
            {
                break;
            }
        }

        // Save remaining items
        if (!obceBatch.isEmpty()) { municipalityRepository.saveAll(obceBatch); };
        if (!castiBatch.isEmpty()) { municipalPartRepository.saveAll(castiBatch); };

        reader.close();
    }


    private Municipality parseObec(XMLStreamReader reader) throws Exception {
        Municipality municipality = new Municipality();
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                String name = reader.getLocalName();
                if ("Kod".equals(name) && "obi".equals(reader.getPrefix())) {
                    municipality.setRegionId(reader.getElementText());
                }
                else if ("Nazev".equals(name) && "obi".equals(reader.getPrefix())) {
                    municipality.setName(reader.getElementText());
                }
            } else if (event == XMLStreamConstants.END_ELEMENT && "Obec".equals(reader.getLocalName())) {
                break;
            }
        }
        return municipality;
    }

    private MunicipalPart parseCastObce(XMLStreamReader reader) throws Exception {
        MunicipalPart part = new MunicipalPart();
        String parentKod = null;

        while (reader.hasNext())
        {
            int event = reader.next();
            if(event == XMLStreamConstants.START_ELEMENT)
            {
                String name = reader.getLocalName();
                if("Kod".equals(name))
                {
                    part.setPartId(reader.getElementText());
                } else if("Nazev".equals(name))
                {
                    part.setName(reader.getElementText());
                } else if("Obec".equals(name))
                {
                    parentKod = parseParentKod(reader);
                    part.setRegionId(parentKod);
                }
            } else if(event == XMLStreamConstants.END_ELEMENT && "CastObce".equals(reader.getLocalName()))
            {
                break;
            }
        }

        return part;
    }

    private String parseParentKod(XMLStreamReader reader) throws Exception {
        while (reader.hasNext()) {
            int event = reader.next();
            if (event == XMLStreamConstants.START_ELEMENT && "Kod".equals(reader.getLocalName())) {
                return reader.getElementText();
            } else if (event == XMLStreamConstants.END_ELEMENT && "Obec".equals(reader.getLocalName())) {
                break;
            }
        }
        return null;
    }

    @Transactional(readOnly = true)
    public Page<Municipality> getMunicipalitiesPage(int page, int size)
    {
        Page<Municipality> resultPage = this.municipalityRepository.findAll(PageRequest.of(page, size));

        // Triggers lazy loading
        resultPage.forEach(m -> m.getLocations().size());

        return resultPage;
    }
}
