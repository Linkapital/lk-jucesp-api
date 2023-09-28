package com.lk.jucesp.bots.components;

import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;

import java.io.InputStream;
import java.util.List;

public interface JucespBot {

    InputStream getRegistrationForm(String socialReason) throws CannotGetJucespFileException;

    InputStream getSimplifiedCertification(String socialReason) throws CannotGetJucespFileException;

    List<DocumentMetadata> getArchivedDocuments(String socialReason) throws CannotGetJucespFileException;

}
