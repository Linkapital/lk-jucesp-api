package com.lk.jucesp.bots.components;

import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface JucespBot {

    InputStream getRegistrationForm(String socialReason) throws CannotGetJucespFileException, IOException;

    InputStream getSimplifiedCertification(String socialReason) throws CannotGetJucespFileException, IOException;

    List<DocumentMetadata> getArchivedDocuments(String socialReason) throws CannotGetJucespFileException;

}
