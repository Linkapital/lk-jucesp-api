package com.lk.jucesp.bots.components;

import com.lk.jucesp.bots.exceptions.CannotGetJucespFileException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface JucespBot {

    /**
     * Obtenha o documento de registro de uma empresa de acordo com o seu código nire
     *
     * @param nireCode {@link String} o código nire da empresa
     * @return {@link InputStream} os dados recebidos
     * @throws CannotGetJucespFileException o documento não foi encontrado
     * @throws IOException                  erro ao processar documento
     */
    InputStream getRegistrationForm(String nireCode) throws CannotGetJucespFileException, IOException;

    /**
     * Obter a certificação simplificada de uma empresa de acordo com o seu código nire
     *
     * @param nireCode {@link String} o código nire da empresa
     * @return {@link InputStream} os dados recebidos
     * @throws CannotGetJucespFileException o documento não foi encontrado
     * @throws IOException                  erro ao processar documento
     */
    InputStream getSimplifiedCertification(String nireCode) throws CannotGetJucespFileException, IOException;

    /**
     * Obter uma lista de documentos arquivados de uma empresa de acordo com sua razão social
     *
     * @param SocialReason {@link String} o social reason
     * @return {@link List}<{@link DocumentMetadata}> uma lista de documentos arquivados
     * @throws CannotGetJucespFileException o documento não foi encontrado
     */
    List<DocumentMetadata> getArchivedDocuments(String SocialReason) throws CannotGetJucespFileException;

}
