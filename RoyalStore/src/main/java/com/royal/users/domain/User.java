package com.royal.users.domain;


import com.royal.orders.domain.Order;
import com.royal.users.domain.details.PublicUserDetails;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String email;
    private String username;
    private String password;
    private byte[] photo;
    private ArrayList<String> roles = new ArrayList<>();
    private ArrayList<String> cart = new ArrayList<>();
    private ArrayList<String> liked = new ArrayList<>();
    @DBRef
    private Set<Order> orders = new HashSet<>();

    public PublicUserDetails getPublicDetails() {
        var details = new PublicUserDetails();
        details.setEmail(email);
        details.setRoles(roles);
        details.setUsername(username);
        details.setProfilePicture(photo);
        return details;
    }

    public void setPublicDetails(@NotNull PublicUserDetails details) {
        email = details.getEmail();
        roles = details.getRoles();
        username = details.getUsername();
        photo = details.getProfilePicture();
    }

    public String toString() {
        return username + " aka " + email + ": " + roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var authorities = new ArrayList<SimpleGrantedAuthority>(roles.size());
        for (String role : roles) authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
