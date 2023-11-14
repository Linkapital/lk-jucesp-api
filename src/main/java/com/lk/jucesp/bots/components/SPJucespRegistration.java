package com.lk.jucesp.bots.components;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;

public class SPJucespRegistration extends SPJucespTemplate {

    protected static SPJucespTemplate INSTANCE;
    private String nire;

    private SPJucespRegistration() {
        this.nire = "";
    }

    static SPJucespTemplate getInstance() {
        if (INSTANCE == null)
            INSTANCE = new SPJucespRegistration();

        return INSTANCE;
    }

    @Override
    protected String getUrlHomePage() {
        return "https://www.jucesponline.sp.gov.br/Pesquisa.aspx?IDProduto=2";
    }

    @Override
    protected Page getDocumentPage(@NonNull HtmlAnchor documentLink) throws IOException {
        String nireCode = documentLink.getFirstChild().getTextContent();
        String newUrl = "https://www.jucesponline.sp.gov.br/login.aspx?ReturnUrl=%2fRestricted%2fGeraDocumento.aspx%3fnire%3d"
                        + nireCode + "%26tipoDocumento%3d2&nire=" + nireCode + "&tipoDocumento=2";

        return webClient.getPage(newUrl);
    }

    @Override
    public Page getDocument(String nireCode) throws IOException {
        String newUrl = "https://www.jucesponline.sp.gov.br/login.aspx?ReturnUrl=%2fRestricted%2fGeraDocumento.aspx%3fnire%3d"
                + nireCode + "%26tipoDocumento%3d2&nire=" + nireCode + "&tipoDocumento=2";

        return webClient.getPage(newUrl);
    }

}
