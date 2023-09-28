package com.lk.jucesp.bots.components;

import lombok.Builder;
import lombok.Getter;

import java.io.InputStream;

@Builder
@Getter
public class DocumentMetadata {

    private String date;
    private String description;
    private InputStream data;

}
