package com.sanwenyukaochi.security.security.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.sanwenyukaochi.security.entity.Tenant;
import com.sanwenyukaochi.security.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
    
    private Long id;

    private Tenant tenant;

    private String username;
    
    @JsonIgnore
    private String password;
    
    private String email;

    private String phone;
    
    private Boolean status;
    
    private Boolean accountNonExpired;
    
    private Boolean accountNonLocked;
    
    private Boolean credentialsNonExpired;

    private Collection<? extends GrantedAuthority> authorities;


//    public UserDetailsImpl(Long id,SysTenant tenant, String username, String password, String email, String phone,
//                           Boolean status, Boolean accountNonExpired, Boolean accountNonLocked, Boolean credentialsNonExpired,
//                           Collection<? extends GrantedAuthority> authorities) {
//        this.id = id;
//        this.username = username;
//        this.email = email;
//        this.phone = phone;
//        this.tenant = tenant;
//        this.password = password;
//        this.authorities = authorities;
//        this.accountNonExpired = accountNonExpired;
//        this.accountNonLocked = accountNonLocked;
//        this.credentialsNonExpired = credentialsNonExpired;
//        this.status = status;
//    }
    
    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = null;
                /*user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName().name()))
                .collect(Collectors.toList());*/

        return new UserDetailsImpl(
                user.getId(),
                user.getTenant(),
                user.getUserName(),
                user.getPassword(),
                user.getEmail(),
                user.getPhone(),
                user.getStatus(),
                user.getAccountNonExpired(),
                user.getAccountNonLocked(),
                user.getCredentialsNonExpired(),
                authorities);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired != null && this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked != null && this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired != null && this.credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.status != null && this.status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

}