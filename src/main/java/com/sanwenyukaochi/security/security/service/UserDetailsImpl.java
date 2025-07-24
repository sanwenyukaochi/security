package com.sanwenyukaochi.security.security.service;

import java.util.*;
import java.util.stream.Collectors;

import com.sanwenyukaochi.security.entity.Permission;
import com.sanwenyukaochi.security.entity.Tenant;
import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {

    private Long id;
    private Tenant tenant;
    private String username;
    private String nickName;
    @JsonIgnore
    private String password;
    private String email;
    private String phone;
    private String avatar;
    private Boolean status;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;
    private Collection<? extends GrantedAuthority> authorities;
    private List<Role> roles;
    private List<Permission> permissions;
    private Long createdAt;
    private Long updatedAt;

    public static UserDetailsImpl build(User user, List<String> authorityCodes, List<Role> roles, List<Permission> permissions) {
        List<GrantedAuthority> grantedAuthorities = Optional.ofNullable(authorityCodes)
                .orElseGet(Collections::emptyList).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new UserDetailsImpl(
                user.getId(),
                user.getTenant(),
                user.getUserName(),
                user.getNickName(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getAvatar(),
                user.getStatus(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                grantedAuthorities,
                roles,
                permissions,
                user.getUpdatedAt(),
                user.getCreatedAt()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.status;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserDetailsImpl other)) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}