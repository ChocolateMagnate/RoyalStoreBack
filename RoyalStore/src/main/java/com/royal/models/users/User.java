package com.royal.models.users;

import com.royal.models.products.ElectronicProduct;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@Setter
@Document(collection = "users")
public class User implements UserDetails {
    private String username;
    private String password;
    private String email;
    private byte[] photo;
    private ArrayList<SimpleGrantedAuthority> roles;
    private ArrayList<ElectronicProduct> cart;

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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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
