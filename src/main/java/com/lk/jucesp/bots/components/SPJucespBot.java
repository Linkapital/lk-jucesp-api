package com.lk.jucesp.bots.components;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class SPJucespBot implements JucespBot {

    private final SPJucespBuilder jucespBuilder;

    public SPJucespBot(SPJucespBuilder jucespBuilder) {
        this.jucespBuilder = jucespBuilder;
    }

    @Override
    public InputStream getRegistrationForm(String nire) throws CannotGetJucespFileException, IOException {
        Page page = jucespBuilder.createJucespRegistration().getDocument(nire);
        if (page instanceof UnexpectedPage) {
            throw new CannotGetJucespFileException("Not a valid page for registration");
        } else {
            try {
                return page.getUrl().openStream();
            } catch (Exception e) {
                throw new CannotGetJucespFileException(e.getMessage());
            }
        }
    }

    @Override
    public InputStream getSimplifiedCertification(String nire) throws CannotGetJucespFileException, IOException {
        Page page = jucespBuilder.createJucespSimplifiedCertification().getDocument(nire);
        if (page instanceof UnexpectedPage) {
            throw new CannotGetJucespFileException("Not a valid page for certification");
        } else {
            try {
                return page.getUrl().openStream();
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
