package com.lk.jucesp.bots;

import com.lk.captcha.CaptchaSolver;
import com.lk.jucesp.bots.components.*;
import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;
import com.lk.jucesp.bots.util.DetectText;
import com.lk.jucesp.bots.util.ImageTools;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

import java.io.IOException;
import java.util.logging.Logger;

public class MainApplication {

    public static final Logger LOGGER = Logger.getLogger(MainApplication.class.getName());

    public static void main(String[] args) throws CannotGetJucespFileException{
        LOGGER.info("Running");
        String socialReason = args[0] ;
        SPJucespBot spJucespBot = new SPJucespBot(new SPJucespBuilderImpl());
        spJucespBot.getArchivedDocuments(socialReason);
    }
}
