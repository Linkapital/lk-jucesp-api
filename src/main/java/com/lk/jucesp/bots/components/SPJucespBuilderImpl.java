package com.lk.jucesp.bots.components;

import org.springframework.stereotype.Component;

@Component
public class SPJucespBuilderImpl implements SPJucespBuilder {

    @Override
    public SPJucespTemplate createJucespRegistration() {
        return SPJucespRegistration.getInstance();
    }

    @Override
    public SPJucespTemplate createJucespSimplifiedCertification() {
        return SPJucespSimplifiedCertification.getInstance();
    }

    @Override
    public SPJucespTemplate createJucespArchivedDocument() {
        return SPJucespArchivedDocument.getInstance();
    }

}
