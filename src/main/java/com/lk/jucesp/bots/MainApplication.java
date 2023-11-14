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
    }
}
