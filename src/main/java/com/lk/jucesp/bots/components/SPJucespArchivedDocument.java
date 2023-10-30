package com.lk.jucesp.bots.components;

import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.UnexpectedPage;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlHiddenInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlRadioButtonInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTableBody;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import com.lk.captcha.CaptchaSolver;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import static java.lang.Math.min;

public class SPJucespArchivedDocument extends SPJucespTemplate {

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
    //"https://www.jucesponline.sp.gov.br/Pesquisa.aspx?IDProduto=12"

    @Override
    protected List<DocumentMetadata> getDocuments(Page pageResult) throws IOException, InterruptedException {
        List<DocumentMetadata> results = new ArrayList<>();

        if (pageResult instanceof HtmlPage htmlPage
            && htmlPage.getElementById("ctl00_cphContent_txtEmail") == null) {

            boolean flag = true;
            while (flag) {
                fillAllDocs(selectDocumentType(htmlPage), results);
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
        HtmlForm submitForm = okSubmitButton.getEnclosingForm();
        Iterator iteratorTable = documentsTable.getChildElements().iterator();
        HtmlTableBody tableBody = (HtmlTableBody) iteratorTable.next();
        Iterator rowIterator = tableBody.getChildElements().iterator();
        rowIterator.next();
        while (rowIterator.hasNext()) {
            HtmlTableRow row = (HtmlTableRow) rowIterator.next();
            HtmlTableCell radioCell = row.getCell(0);
            Iterator<DomElement> radioCellIterator = radioCell.getChildElements().iterator();
            DomElement element = radioCellIterator.next();
            if(element instanceof HtmlRadioButtonInput radioButtonInput && radioButtonInput.getAttribute("id").equals("ctl00_cphContent_frmPreVisualiza_rblTipoDocumento_3")){
                radioButtonInput.setChecked(true);
                HtmlPage documentPage = webClient.getPage(submitForm.getWebRequest(okSubmitButton));
                return documentPage;
            }
        }
        return htmlPage;
    }
}
