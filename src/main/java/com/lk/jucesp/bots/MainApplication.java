package com.lk.jucesp.bots;

import com.lk.jucesp.bots.components.SPJucespBot;
import com.lk.jucesp.bots.components.SPJucespBuilderImpl;
import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;

import java.util.logging.Logger;

public class MainApplication {

    public static final Logger LOGGER = Logger.getLogger(MainApplication.class.getName());

    public static void main(String[] args) throws CannotGetJucespFileException {
        LOGGER.info("Running");
        if (args.length > 0) {
            String socialReason = args[0];
            SPJucespBot spJucespBot = new SPJucespBot(new SPJucespBuilderImpl());
            spJucespBot.getArchivedDocuments(socialReason);
        }
    }
}
