package com.royal.models.users;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;

@Getter
@Setter
public class PublicUserDetails {
    private String id;
    private String token;
    private String email;
    private String username;
    private byte[] profilePicture;
    private ArrayList<SimpleGrantedAuthority> roles;
}
