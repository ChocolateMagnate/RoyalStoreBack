package com.royal.models.users;

import com.royal.models.Cart;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;

@Getter
@Setter
@Document(collection = "users")
public class User {
    private String username;
    private String password;
    private String email;
    private byte[] photo;
    private ArrayList<String> roles;
    private Cart cart;

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
}
