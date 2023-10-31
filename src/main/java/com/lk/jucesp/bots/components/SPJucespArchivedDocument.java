package com.lk.jucesp.bots.components;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.html.*;
import com.lk.captcha.CaptchaSolver;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import static java.lang.Math.min;
import static java.lang.String.format;

public class SPJucespArchivedDocument extends SPJucespTemplate {

    private static SPJucespTemplate instance;
    private static final int capFail = 100;

    private SPJucespArchivedDocument() {
    }

    static SPJucespTemplate getInstance() {
        if (instance == null)
            instance = new SPJucespArchivedDocument();

        return instance;
    }

    @Override
    protected String getUrlHomePage() {
        return "https://www.jucesponline.sp.gov.br";
    }
    //"https://www.jucesponline.sp.gov.br/Pesquisa.aspx?IDProduto=12"

    @Override
    protected List<DocumentMetadata> getDocuments(Page pageResult) throws IOException, InterruptedException {
        List<DocumentMetadata> results = new ArrayList<>();

        if (pageResult instanceof HtmlPage htmlPage
            && htmlPage.getElementById("ctl00_cphContent_txtEmail") == null) {

            boolean flag = true;
            while (flag) {
                HtmlPage documentType = selectDocumentType(htmlPage);
                HtmlPage loginPage = doLogin(documentType);
                fillAllDocs(loginPage, results);
                HtmlAnchor next = (HtmlAnchor) htmlPage.getElementById(
                        "ctl00_cphContent_GdvArquivamento_pgrGridView_btrNext_lbtText");
                if (next != null) {
                    HtmlHiddenInput hiddenInput = (HtmlHiddenInput) htmlPage.getElementById(
                            "__EVENTTARGET");
                    HtmlForm submitForm = hiddenInput.getEnclosingForm();
                    hiddenInput.setAttribute("value",
                            "ctl00$cphContent$GdvArquivamento$pgrGridView$btrNext$lbtText");
                    htmlPage = webClient.getPage(submitForm.getWebRequest(hiddenInput));
                } else {
                    flag = false;
                }
            }

            if (results.isEmpty())
                results = null;
        }

        return results;
    }

    private void fillAllDocs(HtmlPage htmlPage, List<DocumentMetadata> results) throws IOException {
        HtmlTable documentsTable = (HtmlTable) htmlPage.getElementById(
                "ctl00_cphContent_GdvArquivamento_gdvContent");
        HtmlSubmitInput continueSubmitButton = (HtmlSubmitInput) htmlPage.getElementById(
                "ctl00_cphContent_btnContinuar");
        HtmlForm submitForm = continueSubmitButton.getEnclosingForm();
        Iterator iteratorTable = documentsTable.getChildElements().iterator();
        HtmlTableBody tableBody = (HtmlTableBody) iteratorTable.next();
        Iterator rowIterator = tableBody.getChildElements().iterator();
        rowIterator.next();

        while (rowIterator.hasNext()) {
            HtmlTableRow row = (HtmlTableRow) rowIterator.next();
            HtmlTableCell radioCell = row.getCell(0);
            HtmlTableCell dateCell = row.getCell(1);
            HtmlTableCell documentCell = row.getCell(4);
            Iterator<DomElement> radioCellIterator = radioCell.getChildElements().iterator();
            DomElement element = radioCellIterator.next();
            Iterator<DomElement> documentCellIterator = documentCell.getChildElements().iterator();
            HtmlSpan descriptionSpan = (HtmlSpan) documentCellIterator.next();

            if (element instanceof HtmlRadioButtonInput radioButtonInput) {
                radioButtonInput.setChecked(true);
                UnexpectedPage documentPage = webClient.getPage(submitForm.getWebRequest(continueSubmitButton));
                String description = descriptionSpan.getTextContent();
                results.add(DocumentMetadata.builder()
                        .data(documentPage.getInputStream())
                        .date(dateCell.getTextContent())
                        .description(ObjectUtils.isEmpty(description)
                                     ? null
                                     : description.substring(0, min(50, description.length())))
                        .build());
            }
        }
    }

    private HtmlPage selectDocumentType(HtmlPage htmlPage) throws IOException, InterruptedException {
        var r = new Random();
        Thread.sleep(3000L + r.nextInt(2000));
        HtmlTable documentsTable = (HtmlTable) htmlPage.getElementById(
                "ctl00_cphContent_frmPreVisualiza_rblTipoDocumento");
        HtmlSubmitInput okSubmitButton = (HtmlSubmitInput) htmlPage.getElementById(
                "ctl00_cphContent_frmPreVisualiza_btnEmitir");

        Iterator iteratorTable = documentsTable.getChildElements().iterator();
        HtmlTableBody tableBody = (HtmlTableBody) iteratorTable.next();
        Iterator rowIterator = tableBody.getChildElements().iterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            HtmlTableRow row = (HtmlTableRow) rowIterator.next();
            HtmlTableCell radioCell = row.getCell(0);
            Iterator<DomElement> radioCellIterator = radioCell.getChildElements().iterator();
            DomElement element = radioCellIterator.next();
            if(element instanceof HtmlRadioButtonInput radioButtonInput &&
                    radioButtonInput.getAttribute("id")
                            .equals("ctl00_cphContent_frmPreVisualiza_rblTipoDocumento_3")){
                radioButtonInput.setChecked(true);
                HtmlPage tempPage = okSubmitButton.click();
                return tempPage;
            }
        }
        return htmlPage;
    }

    private HtmlPage doLogin(HtmlPage htmlPage) throws IOException, InterruptedException {
        SPJucespCredentials credentials = SPJucespCredentialsGenerator.getInstance().getCredentials();
        String cpf = credentials.getCpf();
        String password = credentials.getPassword();
        logger.info(format("%s Credenciales de jucesp usadas ---- %s", cpf, password));
        boolean flag = true;
        var r = new Random();
        String captcha;
        var failCount = 0;
        HtmlTextInput cpfInput;
        HtmlPasswordInput passwordInput;
        HtmlTextInput captchaInput;
        HtmlSubmitInput enterSubmitButton;
        HtmlForm captcha1Form2;
        HtmlPage pageResult = htmlPage;
        while (flag && failCount < capFail) {
            captcha = getCaptcha(pageResult);
            if (captcha != null) {
                cpfInput = (HtmlTextInput) pageResult.getElementById("ctl00_cphContent_txtEmail");
                cpfInput.setText(cpf);
                passwordInput = (HtmlPasswordInput) pageResult.getElementById("ctl00_cphContent_txtSenha");
                passwordInput.setText(password);
                captchaInput = pageResult.getFirstByXPath("//input[@name='ctl00$cphContent$CaptchaControl1']");
                enterSubmitButton = (HtmlSubmitInput) pageResult.getElementById("ctl00_cphContent_btEntrar");
                captchaInput.setText(captcha);
                captcha1Form2 = enterSubmitButton.getEnclosingForm();
                pageResult = webClient.getPage(captcha1Form2.getWebRequest(enterSubmitButton));
                HtmlAnchor next = (HtmlAnchor) pageResult.getElementById(
                        "ctl00_cphContent_GdvArquivamento_pgrGridView_btrNext_lbtText");
                if (Objects.isNull(next) || next != null) {
                    flag = false;
                } else {
                    logger.info("Other error for the captcha in the login page: "+(failCount + 1));
                    failCount++;
                    Thread.sleep(3000L + r.nextInt(2000));
              }
            }
        }
        return pageResult;
    }
}
