package com.lk.jucesp.bots.components;


import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import com.gargoylesoftware.htmlunit.html.parser.HTMLParserListener;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;
import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;
import com.lk.jucesp.bots.util.DetectText;
import com.lk.jucesp.bots.util.ImageTools;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SPJucespTemplate {

    public static final Logger logger = Logger.getLogger(SPJucespTemplate.class.getName());
    private static final String jucespUrl = "https://jucesponline.sp.gov.br/";
    private static final int capFail = 100;
    protected final WebClient webClient;
    private final ImageTools imageTools;
    private final String localFile = "captcha.jpg";

    protected SPJucespTemplate() {
        this.webClient = new WebClient();
        this.imageTools = new ImageTools();
    }

    public List<DocumentMetadata> getDocuments(String socialReason) throws CannotGetJucespFileException {
        List<DocumentMetadata> results = new ArrayList<>();
        var failCount = 0;

        try {

            HtmlTable resultTable = null;
            var r = new Random();
            webClient.getOptions().setThrowExceptionOnScriptError(false);
            webClient.addRequestHeader("Sec-Fetch-Dest", "document");
            webClient.addRequestHeader("Sec-Fetch-Mode", "cors");
            webClient.addRequestHeader("Sec-Fetch-Site", "same-origin");
            webClient.addRequestHeader("Sec-Fetch-User", "?1");
            this.setLogConfig(webClient);

            HtmlPage page = webClient.getPage(getUrlHomePage());
            HtmlTextInput textField = (HtmlTextInput) page.getElementById("ctl00_cphContent_frmBuscaSimples_txtPalavraChave");
            HtmlSubmitInput searchButton = (HtmlSubmitInput) page.getElementById("ctl00_cphContent_frmBuscaSimples_btPesquisar");
            textField.setText(socialReason);
            HtmlForm submitForm = searchButton.getEnclosingForm();
            page = webClient.getPage(submitForm.getWebRequest(searchButton));

            boolean flag = true;
            String captcha;
            HtmlTextInput captcha1Input;
            HtmlSubmitInput captcha1SubmitInput;
            HtmlForm captcha1Form;
            while (resultTable == null && failCount < capFail && flag) {
                captcha = getCaptcha(page);
                if (captcha != null) {
                    captcha1Input = page.getFirstByXPath("//input[@name='ctl00$cphContent$gdvResultadoBusca$CaptchaControl1']");
                    captcha1Input.setText(captcha);
                    captcha1SubmitInput = (HtmlSubmitInput) page.getElementById("ctl00_cphContent_gdvResultadoBusca_btEntrar");
                    captcha1Form = captcha1SubmitInput.getEnclosingForm();

                    page = webClient.getPage(captcha1Form.getWebRequest(captcha1SubmitInput));
                    resultTable = (HtmlTable) page.getElementById("ctl00_cphContent_gdvResultadoBusca_gdvContent");

                    if (Objects.isNull(resultTable))
                        //captchaSolver.report(captcha.getId());
                        logger.info("Error with the detected text from captcha: " + (failCount + 1));

                    failCount++;
                    Thread.sleep(3000L + r.nextInt(2000));
                } else {
                    flag = false;
                    resultTable = (HtmlTable) page.getElementById("ctl00_cphContent_gdvResultadoBusca_gdvContent");
                }
            }

            Thread.sleep(3000L + r.nextInt(2000));
            checkCapFail(failCount, socialReason);
            failCount = 0;
            Iterator iteratorTable = resultTable.getChildElements().iterator();
            HtmlTableBody tableBody = (HtmlTableBody) iteratorTable.next();
            Iterator rowIterator = tableBody.getChildElements().iterator();
            rowIterator.next();
            HtmlTableRow row = (HtmlTableRow) rowIterator.next();
            HtmlTableCell tableCell = row.getCell(0);
            Iterator cellIterator = tableCell.getChildElements().iterator();
            HtmlAnchor documentLink = (HtmlAnchor) cellIterator.next();
            String nire = documentLink.getVisibleText();
            if (nire == "") {
                throw new CannotGetJucespFileException("No NIRE found for this social reason");
            } else {
                HtmlPage pageResult = getPageFromNire(nire);
                flag = true;
                while (flag && failCount < capFail) {
                    captcha = getCaptcha(pageResult);
                    if (captcha != null) {
                        /*Nuevo*/
                        captcha1Input = pageResult.getFirstByXPath("//input[@name='ctl00$cphContent$frmPreVisualiza$CaptchaControl1']");
                        captcha1Input.setText(captcha);
                        captcha1SubmitInput = (HtmlSubmitInput) pageResult.getElementById("ctl00_cphContent_frmPreVisualiza_btEntrar");
                        captcha1Form = captcha1SubmitInput.getEnclosingForm();

                        pageResult = webClient.getPage(captcha1Form.getWebRequest(captcha1SubmitInput));
                        HtmlTable documentsTable = (HtmlTable) pageResult.getElementById(
                                "ctl00_cphContent_frmPreVisualiza_rblTipoDocumento");
                        if (Objects.isNull(documentsTable)) {
                            logger.info("Error with the detected text from captcha second page: " + (failCount + 1));

                            failCount++;
                            Thread.sleep(3000L + r.nextInt(2000));
                        } else {
                            results = getDocuments(pageResult);
                            flag = false;
                        }
                        /*Fin nuevo*/
                    } else {
                        results = getDocuments(pageResult);
                        flag = false;
                    }
                }

                page = webClient.getPage(getUrlHomePage());
                logout(page);
                checkCapFail(failCount, socialReason);
            }
        } catch (Exception e) {
            logger.info(String.format("%s -- error social reason: %s", e.getMessage(), socialReason));
            throw new CannotGetJucespFileException(e.getMessage());
        }

        return results;
    }

    protected abstract String getUrlHomePage();

    protected Page getDocumentPage(HtmlAnchor documentLink) throws IOException {
        return documentLink.click();
    }

    protected HtmlPage getPageFromNire(String nire) throws IOException {
        return webClient.getPage("https://jucesponline.sp.gov.br/Pre_Visualiza.aspx?nire=" + nire);
    }

    protected List<DocumentMetadata> getDocuments(Page pageResult) throws IOException, InterruptedException {
        ArrayList<DocumentMetadata> results = new ArrayList<DocumentMetadata>();
        if (pageResult instanceof UnexpectedPage unexpected)
            results.add(DocumentMetadata
                    .builder()
                    .data(unexpected.getInputStream())
                    .build());

        return results;
    }

    protected String getCaptcha(HtmlPage page) throws IOException {
        HtmlImage image = page.getFirstByXPath("//img[contains(@src,'Captcha')]");
        if (image == null)
            return null;

        URL url = new URL(String.format("%s%s", jucespUrl, image.getSrcAttribute()));

        return getDetectedText(url);
    }

    private String getDetectedText(URL url) {
        imageTools.saveImage(url);
        imageTools.resizeAndSave(localFile);
        Region region = Region.US_EAST_1;
        RekognitionClient rekClient = RekognitionClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();

        String text = Optional.ofNullable(DetectText.detectTextLabels(rekClient, localFile))
                .map(s -> s.replaceAll("\\s+", ""))
                .orElse("");

        logger.info(String.format("Word: %s", text));
        rekClient.close();

        return text;
    }

    private void checkCapFail(int failCount, String socialReason) throws CannotGetJucespFileException {
        if (failCount >= capFail) {
            String message = String.format("Cannot decode captcha -- social reason: %s", socialReason);
            throw new CannotGetJucespFileException(message);
        }
    }

    private void logout(HtmlPage page) throws IOException {
        HtmlHiddenInput hiddenInput = (HtmlHiddenInput) page.getElementById("__EVENTTARGET");
        HtmlForm submitForm = hiddenInput.getEnclosingForm();
        hiddenInput.setAttribute("value", "ctl00$frmLogin$lbtSair");
        webClient.getPage(submitForm.getWebRequest(hiddenInput));
    }

    private void setLogConfig(WebClient webClient) {
        java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(Level.OFF);
        java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);

        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        webClient.setIncorrectnessListener((arg0, arg1) -> {
            // TODO Auto-generated method stub
        });

        webClient.setCssErrorHandler(new CSSErrorHandler() {
            @Override
            public void warning(CSSParseException exception) throws CSSException {
            }

            @Override
            public void error(CSSParseException exception) throws CSSException {
            }

            @Override
            public void fatalError(CSSParseException exception) throws CSSException {
            }
        });

        webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {
            @Override
            public void scriptException(HtmlPage page, ScriptException scriptException) {
            }

            @Override
            public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
            }

            @Override
            public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
            }

            @Override
            public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
            }

            @Override
            public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
            }
        });

        webClient.setHTMLParserListener(new HTMLParserListener() {
            @Override
            public void error(String message, URL url, String html, int line, int column, String key) {
            }

            @Override
            public void warning(String message, URL url, String html, int line, int column, String key) {
            }
        });
    }

}
