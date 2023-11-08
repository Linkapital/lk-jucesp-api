package com.lk.jucesp.bots.components;

import com.lk.captcha.CaptchaSolver;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
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
