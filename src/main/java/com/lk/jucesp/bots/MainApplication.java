package com.lk.jucesp.bots;

import com.lk.jucesp.bots.components.DocumentMetadata;
import com.lk.jucesp.bots.components.SPJucespBot;
import com.lk.jucesp.bots.components.SPJucespBuilderImpl;
import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

public class MainApplication {

    public static final Logger LOGGER = Logger.getLogger(MainApplication.class.getName());

    public static void main(String[] args) throws CannotGetJucespFileException, IOException {
        LOGGER.info("Running");
        if (args.length > 0) {
            String socialReason = args[0];
            SPJucespBot spJucespBot = new SPJucespBot(new SPJucespBuilderImpl());
            LOGGER.info("Getting documents by Social Reason");
            List<DocumentMetadata> find1 = spJucespBot.getArchivedDocuments(socialReason);
            LOGGER.info("End for getting documents by Social Reason");
            LOGGER.info("Getting company registration form by Nire Code");
            InputStream find2 = spJucespBot.getRegistrationForm(spJucespBot.getNire());
            LOGGER.info("End of getting company registration form by Nire Code");
            LOGGER.info("Getting company Simplified Certification by Nire Code");
            InputStream find3 = spJucespBot.getSimplifiedCertification(spJucespBot.getNire());
            LOGGER.info("End of getting company Simplified Certification by Nire Code");
        }
    }
}
