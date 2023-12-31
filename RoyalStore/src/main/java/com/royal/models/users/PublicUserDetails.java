package com.royal.models.users;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PublicUserDetails {
    private String id;
    private String token;
    private String email;
    private String username;
    private byte[] profilePicture;
    private ArrayList<String> roles;
}
