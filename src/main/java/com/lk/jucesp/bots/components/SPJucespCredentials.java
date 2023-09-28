package com.lk.jucesp.bots.components;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SPJucespCredentials {

    private String cpf;
    private String password;

}
