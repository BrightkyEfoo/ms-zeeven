package com.cs.ge.entites;

import com.cs.ge.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Document("UTILISATEURS")
public class Utilisateur extends Profile implements UserDetails {
    @JsonProperty(access = WRITE_ONLY)
    private String password;
    @JsonProperty(access = WRITE_ONLY)
    private Boolean enabled = false;
    @JsonProperty(access = WRITE_ONLY)
    private Role role;
    private UUID serviceKey;
    private UUID serviceId;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    @Override
    public String getUsername() {
        String username = null;
        if (this.email != null) {
            username = this.email;
        }
        if (this.phone != null) {
            username = this.phone;
        }
        return username;
    }

    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

}
