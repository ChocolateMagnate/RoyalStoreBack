package com.royal.users.domain.details;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class AuthenticatedUserDetails {
    private String email;
    private String password;
    private boolean rememberMe;
    private byte[] profilePicture;
    private ArrayList<String> roles;
}
