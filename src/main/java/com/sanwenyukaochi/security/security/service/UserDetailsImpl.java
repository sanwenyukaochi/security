package com.sanwenyukaochi.security.security.service;

import java.util.*;
import java.util.stream.Collectors;

import com.sanwenyukaochi.security.entity.Tenant;
import com.sanwenyukaochi.security.entity.User;
import com.sanwenyukaochi.security.service.UserPermissionService;
import com.sanwenyukaochi.security.service.UserPermissionCacheService;
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
    
    @JsonIgnore
    private String password;
    
    private String email;

    private String phone;
    
    private Boolean status;
    
    private Boolean accountNonExpired;
    
    private Boolean accountNonLocked;
    
    private Boolean credentialsNonExpired;

    private Collection<? extends GrantedAuthority> authorities;

    public static UserDetailsImpl build(User user, UserPermissionService userPermissionService, UserPermissionCacheService cacheService) {
        List<String> authorityCodes = Optional.ofNullable(cacheService)
                .map(service -> service.getUserAuthorities(user.getId()))
                .orElseGet(() -> userPermissionService.getUserAuthorities(user.getId()));
        
        List<GrantedAuthority> grantedAuthorities = Optional.ofNullable(authorityCodes)
                .orElseGet(Collections::emptyList).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

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
                grantedAuthorities);
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