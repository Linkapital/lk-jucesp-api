package com.lk.jucesp.bots.components;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;

import java.io.IOException;

public class SPJucespSimplifiedCertification extends SPJucespTemplate {

    private static SPJucespTemplate instance;

    private SPJucespSimplifiedCertification() {
    }

    static SPJucespTemplate getInstance() {
        if (instance == null)
            instance = new SPJucespSimplifiedCertification();

        return instance;
    }

    @Override
    protected String getUrlHomePage() {
        return "https://www.jucesponline.sp.gov.br/Pesquisa.aspx?IDProduto=4";
    }

    @Override
    protected Page getDocumentPage(HtmlAnchor documentLink) throws IOException {
        String nireCode = documentLink.getFirstChild().getTextContent();
        String newUrl = "https://www.jucesponline.sp.gov.br/login.aspx?ReturnUrl=%2fRestricted%2fGeraDocumento.aspx%3fnire%3d"
                        + nireCode + "%26tipoDocumento%3d4&nire=" + nireCode + "&tipoDocumento=4";

        return webClient.getPage(newUrl);
    }

    @Override
    public Page getDocument(String nireCode) throws IOException {
        String newUrl = "https://www.jucesponline.sp.gov.br/login.aspx?ReturnUrl=%2fRestricted%2fGeraDocumento.aspx%3fnire%3d"
                + nireCode + "%26tipoDocumento%3d4&nire=" + nireCode + "&tipoDocumento=4";

        return webClient.getPage(newUrl);
    }
}
