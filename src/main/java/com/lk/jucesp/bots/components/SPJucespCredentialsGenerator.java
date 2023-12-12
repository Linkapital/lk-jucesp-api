package com.lk.jucesp.bots.components;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SPJucespCredentialsGenerator {

    private static SPJucespCredentialsGenerator INSTANCE;
    private final List<SPJucespCredentials> credentials;
    private int lastPosition;

    private SPJucespCredentialsGenerator() {
        this.credentials = new ArrayList<>();
        this.fillCredentials();
    }

    static SPJucespCredentialsGenerator getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SPJucespCredentialsGenerator();

        return INSTANCE;
    }

    private void fillCredentials() {
        this.credentials.add(new SPJucespCredentials("24246324809", "Cubano1404"));
        this.credentials.add(new SPJucespCredentials("24302834846","Cubana1301"));
        this.credentials.add(new SPJucespCredentials("80151524963","Mn1234567*"));
        this.credentials.add(new SPJucespCredentials("29674360700","TIKUNolan24"));
    }

    public SPJucespCredentials getCredentials() {
        Random random = new Random();
        int value = random.nextInt(credentials.size());

        while (lastPosition == value)
            value = random.nextInt(credentials.size());

        lastPosition = value;

        return credentials.get(lastPosition);
    }

}
