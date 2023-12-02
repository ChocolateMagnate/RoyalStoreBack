package com.royal.models.users;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginUserCredentials {
    private String email;
    private String password;
    private boolean rememberMe;
}
