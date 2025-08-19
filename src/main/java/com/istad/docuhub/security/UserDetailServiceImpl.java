package com.istad.docuhub.security;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final Keycloak keycloak;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UsersResource usersResource = keycloak.realm("docuhub").users();
        List<UserRepresentation> userRepresentations = usersResource.search(username,true);
        if (userRepresentations.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        UserRepresentation userRepresentation = userRepresentations.get(0);

        return new org.springframework.security.core.userdetails.User(
                userRepresentation.getUsername(),
                "",
                mapRolesToAuthorities(userRepresentation.getRealmRoles())
        );
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<String> roles) {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
