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

    public static void main(String[] args) throws CannotGetJucespFileException, IOException {
        LOGGER.info("Running");

        /*DetectText detectTextTool = new DetectText();
        String sourceImage = args[0] ;
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        String detectedText  = detectTextTool.detectTextLabels(rekClient, sourceImage ).trim();
        LOGGER.info("Word: "+detectedText);

        rekClient.close();*/
        ImageTools imgTools = new ImageTools();
        String sourceImage = args[0] ;
        imgTools.saveImage(sourceImage);
    }
}
