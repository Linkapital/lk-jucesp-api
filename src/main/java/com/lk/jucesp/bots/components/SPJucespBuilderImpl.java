package com.lk.jucesp.bots.components;

import com.lk.captcha.CaptchaSolver;
import org.springframework.stereotype.Component;

@Component
public class SPJucespBuilderImpl implements SPJucespBuilder {

    private final CaptchaSolver captchaSolver;

    public SPJucespBuilderImpl(CaptchaSolver captchaSolver) {
        this.captchaSolver = captchaSolver;
    }

    @Override
    public SPJucespTemplate createJucespRegistration() {
        return SPJucespRegistration.getInstance(captchaSolver);
    }

    @Override
    public SPJucespTemplate createJucespSimplifiedCertification() {
        return SPJucespSimplifiedCertification.getInstance(captchaSolver);
    }

    @Override
    public SPJucespTemplate createJucespArchivedDocument() {
        return SPJucespArchivedDocument.getInstance(captchaSolver);
    }

}
