package com.lk.jucesp.bots.components;


import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class SPJucespBot implements JucespBot {

    private final SPJucespBuilder jucespBuilder;

    public SPJucespBot(SPJucespBuilder jucespBuilder) {
        this.jucespBuilder = jucespBuilder;
    }

    @Override
    public InputStream getRegistrationForm(String socialReason) throws CannotGetJucespFileException {
        List<DocumentMetadata> metadataList = jucespBuilder.createJucespRegistration().getDocuments(socialReason);
        if (metadataList.isEmpty()) {
            throw new CannotGetJucespFileException("Empty list result");
        } else {
            try {
                return metadataList.get(0).getData();
            } catch (Exception e) {
                throw new CannotGetJucespFileException(e.getMessage());
            }
        }
    }

    @Override
    public InputStream getSimplifiedCertification(String socialReason) throws CannotGetJucespFileException {
        List<DocumentMetadata> metadataList = jucespBuilder.createJucespSimplifiedCertification().getDocuments(socialReason);
        if (metadataList.isEmpty()) {
            throw new CannotGetJucespFileException("Empty list result");
        } else {
            try {
                return metadataList.get(0).getData();
            } catch (Exception e) {
                throw new CannotGetJucespFileException(e.getMessage());
            }
        }
    }

    @Override
    public List<DocumentMetadata> getArchivedDocuments(String socialReason) throws CannotGetJucespFileException {
        try {
            return jucespBuilder.createJucespArchivedDocument().getDocuments(socialReason);
        } catch (Exception e) {
            throw new CannotGetJucespFileException(e.getMessage());
        }
    }

}
