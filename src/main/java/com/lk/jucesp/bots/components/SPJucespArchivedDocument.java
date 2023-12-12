package com.lk.jucesp.bots.components;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class SPJucespArchivedDocument extends SPJucespTemplate {

    private static final int capFail = 100;
    private static SPJucespTemplate instance;

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

    @Override
    protected List<DocumentMetadata> getDocuments(Page pageResult) throws IOException, InterruptedException {
        List<DocumentMetadata> results = new ArrayList<>();

        if (pageResult instanceof HtmlPage htmlPage && htmlPage.getElementById("ctl00_cphContent_txtEmail") == null) {
            boolean flag = true;
            HtmlPage documentType;
            HtmlPage loginPage;
            HtmlHiddenInput hiddenInput;
            HtmlForm submitForm;
            while (flag) {
                documentType = selectDocumentType(htmlPage);
                loginPage = doLogin(documentType);
                fillAllDocs(loginPage, results);
                if (!ObjectUtils.isEmpty(htmlPage.getElementById("ctl00_cphContent_GdvArquivamento_pgrGridView_btrNext_lbtText"))) {
                    hiddenInput = (HtmlHiddenInput) htmlPage.getElementById("__EVENTTARGET");
                    submitForm = hiddenInput.getEnclosingForm();
                    hiddenInput.setAttribute("value", "ctl00$cphContent$GdvArquivamento$pgrGridView$btrNext$lbtText");
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

    private void fillAllDocs(HtmlPage htmlPage, List<DocumentMetadata> results) throws IOException, InterruptedException {
        HtmlTable documentsTable = (HtmlTable) htmlPage.getElementById(
                "ctl00_cphContent_GdvArquivamento_gdvContent");
        HtmlSubmitInput continueSubmitButton = (HtmlSubmitInput) htmlPage.getElementById(
                "ctl00_cphContent_btnContinuar");//ctl00_cphContent_btnContinuar
        HtmlForm submitForm = continueSubmitButton.getEnclosingForm();
        Iterator iteratorTable = documentsTable.getChildElements().iterator();
        HtmlTableBody tableBody = (HtmlTableBody) iteratorTable.next();
        Iterator rowIterator = tableBody.getChildElements().iterator();
        rowIterator.next();

        HtmlTableRow row;
        HtmlTableCell radioCell;
        HtmlTableCell dateCell;
        HtmlTableCell documentCell;
        Iterator<DomElement> radioCellIterator;
        DomElement element;
        Iterator<DomElement> documentCellIterator;
        HtmlSpan descriptionSpan;
        UnexpectedPage documentPage;
        while (rowIterator.hasNext()) {
            row = (HtmlTableRow) rowIterator.next();
            radioCell = row.getCell(0);
            dateCell = row.getCell(1);
            documentCell = row.getCell(4);
            radioCellIterator = radioCell.getChildElements().iterator();
            element = radioCellIterator.next();
            documentCellIterator = documentCell.getChildElements().iterator();
            descriptionSpan = (HtmlSpan) documentCellIterator.next();

            if (element instanceof HtmlRadioButtonInput radioButtonInput) {
                radioButtonInput.setChecked(true);
                documentPage = webClient.getPage(submitForm.getWebRequest(continueSubmitButton));
                results.add(new DocumentMetadata(dateCell.getTextContent(),
                        ObjectUtils.isEmpty(descriptionSpan.getTextContent())
                        ? null
                        : descriptionSpan.getTextContent().substring(0, Math.min(50, descriptionSpan.getTextContent().length())),
                        documentPage.getInputStream()));
            }
        }
    }

    private HtmlPage selectDocumentType(HtmlPage htmlPage) throws IOException, InterruptedException {
        Random r = new Random();
        Thread.sleep(3000L + r.nextInt(2000));
        HtmlTable documentsTable = (HtmlTable) htmlPage.getElementById(
                "ctl00_cphContent_frmPreVisualiza_rblTipoDocumento");
        HtmlSubmitInput okSubmitButton = (HtmlSubmitInput) htmlPage.getElementById(
                "ctl00_cphContent_frmPreVisualiza_btnEmitir");

        Iterator iteratorTable = documentsTable.getChildElements().iterator();
        HtmlTableBody tableBody = (HtmlTableBody) iteratorTable.next();
        Iterator rowIterator = tableBody.getChildElements().iterator();
        rowIterator.next();
        HtmlTableRow row;
        HtmlTableCell radioCell;
        Iterator<DomElement> radioCellIterator;
        DomElement element;
        HtmlPage tempPage;
        while (rowIterator.hasNext()) {
            row = (HtmlTableRow) rowIterator.next();
            radioCell = row.getCell(0);
            radioCellIterator = radioCell.getChildElements().iterator();
            element = radioCellIterator.next();

            if (element instanceof HtmlRadioButtonInput radioButtonInput &&
                "ctl00_cphContent_frmPreVisualiza_rblTipoDocumento_3".equals(radioButtonInput.getAttribute("id"))) {
                radioButtonInput.setChecked(true);
                tempPage = okSubmitButton.click();

                return tempPage;
            }
        }

        return htmlPage;
    }

    private HtmlPage doLogin(HtmlPage htmlPage) throws IOException, InterruptedException {
        SPJucespCredentials credentials = SPJucespCredentialsGenerator.getInstance().getCredentials();
        String cpf = credentials.getCpf();
        String password = credentials.getPassword();
        logger.info(String.format("%s Credenciales de jucesp usadas ---- %s", cpf, password));
        boolean flag = true;
        Random r = new Random();
        String captcha;
        int failCount = 0;
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

                if (pageResult.getElementById("ctl00_cphContent_GdvArquivamento_pgrGridView_btrNext_lbtText") != null) {
                    flag = false;
                } else {
                    logger.info(String.format("Other error for the captcha in the login page: %s", (failCount + 1)));
                    failCount++;
                    Thread.sleep(3000L + r.nextInt(2000));
                }
            }
        }

        return pageResult;
    }
}
