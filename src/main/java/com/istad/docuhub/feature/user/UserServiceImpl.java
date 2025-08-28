package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.dto.*;
import com.istad.docuhub.feature.user.mapper.UseMapper;
import com.istad.docuhub.feature.user.mapper.UserMapperManual;
import com.istad.docuhub.slugGeneration.SlugUtil;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final UseMapper useMapper;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    @Override
    public UserResponse register(UserCreateDto userCreateDto) {
//        log.info("Registering user in service {}", userCreateDto);
        if (!userCreateDto.password().equals(userCreateDto.confirmedPassword())){
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Passwords do not match");
        }
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userCreateDto.username());
        user.setEmail(userCreateDto.email());
        user.setFirstName(userCreateDto.firstname());
        user.setLastName(userCreateDto.lastname());

        // setPassword
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(userCreateDto.password());
        user.setCredentials(List.of(cred));

        user.setEmailVerified(false);
        user.setEnabled(true);

        try (Response response = keycloak.realm("docuapi").users().create(user)){

            //example : http://localhost:9090/admin/realms/docuapi/users/414d407a-65a2-4814-b595-3759b64fc12b
            AtomicReference<String> UserUuid = new AtomicReference<>("");
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                //verify email
                List<UserRepresentation> userRepresentations = keycloak.realm("docuapi")
                        .users()
                        .search(user.getUsername(),true);
                userRepresentations.stream()
                        .filter(userRepresentation -> userRepresentation.isEmailVerified().equals(false))
                        .findFirst()
                        .ifPresent(userRepresentation -> {
                            UserUuid.set(userRepresentation.getId());
                            verify(userRepresentation.getId());

                        });
                Integer id;

                //assign role
                UserResource userResource = keycloak.realm("docuapi").users().get(UserUuid.get());
                RoleRepresentation userRole = keycloak.realm("docuapi").roles().get("USER").toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(userRole));

                int retries = 0;
                do {
                    if (retries++ > 50) {
                        throw new RuntimeException("Unable to generate unique ID after 50 attempts");
                    }
                    id = new Random().nextInt(Integer.parseInt("1000000"));
                } while (userRepository.existsByIdAndIsDeletedFalse(id));
                String fullName = userCreateDto.firstname() + " " + userCreateDto.lastname();
                log.info("User id ",user.getId());

                User saveUser = User.builder()
                        //   .id(id)
                        .uuid(UserUuid.get())
                        .fullName(fullName)
                        .isUser(true)
                        .isAdmin(false)
                        .isAdvisor(false)
                        .isStudent(false)
                        .isDeleted(false)
                        .createDate(LocalDate.now())
                        .updateDate(LocalDate.now())
                        .slug(SlugUtil.toSlug(fullName,userCreateDto.username()))
                        .build();
                userRepository.save(saveUser);

                return UserResponse.builder()
                        .uuid(UserUuid.get())
                        .userName(userCreateDto.username())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .fullName(fullName)
                        .isUser(saveUser.getIsUser())
                        .isAdmin(saveUser.getIsAdmin())
                        .isAdvisor(saveUser.getIsAdvisor())
                        .isStudent(saveUser.getIsStudent())
                        .createDate(saveUser.getCreateDate())
                        .updateDate(saveUser.getUpdateDate())
                        .slug(saveUser.getSlug())
                        .build();
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user Unauthorized");
    }
    @Override
    public AuthResponse login(String username, String password) {
        return null;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        RealmResource realmResource = keycloak.realm("docuapi");
        List<UserRepresentation> userRepresentations = realmResource.users().list().stream().filter(UserRepresentation::isEnabled).toList();
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserRepresentation userRepresentation : userRepresentations) {
            Optional<User> singleUser = users.stream().filter(user -> user.getUuid().equals(userRepresentation.getId())).findFirst();
            if (singleUser.isPresent()) {
                User user = singleUser.get();
                userResponses.add(
                        UserMapperManual.mapUserToUserResponse(user,userRepresentation)
                );
            }
        }
        return userResponses;
    }
    @Override
    public UserResponse getSingleUser(String uuid) {
        UserResponse userResponse = getAllUsers().stream().filter(user -> uuid.equals(user.uuid()) ).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return userResponse;
    }
    @Override
    public List<UserResponse> searchUserByUsername(String username) {
        RealmResource realmResource = keycloak.realm("docuapi");
        List<UserRepresentation> userRepresentations = realmResource.users().list().stream().filter(UserRepresentation::isEnabled).toList();
        List<User> users = userRepository.findBySlugContainingAndIsDeletedFalse(username);
        List<UserResponse> userResponses = new ArrayList<>();
        for (UserRepresentation userRepresentation : userRepresentations) {
            Optional<User> singleUser = users.stream().filter(user -> user.getUuid().equals(userRepresentation.getId())).findFirst();
            if (singleUser.isPresent()) {
                userResponses.add(
                        UserMapperManual.mapUserToUserResponse(singleUser.get(),userRepresentation)
                );
            }
        }
        return userResponses;
    }

    @Override
    public void deleteUser(String userId) {
        log.info("User id {} ",userId);
        // userId should be String, not Integer
        // Get the Keycloak user
        RealmResource realmResource = keycloak.realm("docuapi");
        UserResource userResource = realmResource.users().get(userId);
        UserRepresentation userRep = userResource.toRepresentation();
        if (userRep != null && userRep.isEnabled()) {
            // Soft delete in local DB
            User dbUser = userRepository.findByUuidAndIsDeletedFalse(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found or already disabled"));
            dbUser.setIsDeleted(true);
            log.info("dbUser {} ",dbUser);
            userRepository.save(dbUser);
            // Disable user in Keycloak
            userRep.setEnabled(false);
            userResource.update(userRep);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found or already disabled");
        }
    }

    @Override
    public void updateUser(String userUuid, UpdateUserDto updateUserDto) {
        RealmResource realmResource = keycloak.realm("docuapi");
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found or already disabled"));
        UserResource userResource = realmResource.users().get(user.getUuid());
        UserRepresentation userRep = userResource.toRepresentation();
        if (userRep == null || !userRep.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled");
        }
        useMapper.updateUser(user,updateUserDto);
        user.setSlug(SlugUtil.toSlug(updateUserDto.fullName(),updateUserDto.userName()));
        user.setUpdateDate(LocalDate.now());
        User user1= userRepository.save(user);

        if (updateUserDto.firstName() != null) userRep.setFirstName(updateUserDto.firstName());
        if (updateUserDto.lastName() != null) userRep.setLastName(updateUserDto.lastName());
        if (updateUserDto.email() != null) userRep.setEmail(updateUserDto.email());
        if (updateUserDto.userName() != null && !updateUserDto.userName().isBlank()) {
            userRep.setUsername(updateUserDto.userName()); // Only if allowed by Keycloak
        }
        log.info("updateUser {} ",userRep);
        userResource.update(userRep);

        UserResource updatedUserResource = realmResource.users().get(user.getUuid());
        UserRepresentation updatedUserRepresentation = updatedUserResource.toRepresentation();
        if (updatedUserRepresentation.isEnabled() && !updatedUserRepresentation.isEmailVerified()) {
            verify(user.getUuid());
        }
    }

    @Override
    public UpdateUserImageDto updateImageUrl(String imageUrl, String userUuid) {
        RealmResource realmResource = keycloak.realm("docuapi");
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"User not found or already disabled"));
        UserResource userResource = realmResource.users().get(user.getUuid());
        UserRepresentation userRep = userResource.toRepresentation();
        if (userRep == null || !userRep.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled");
        }
        if (imageUrl == null || imageUrl.isBlank() || imageUrl.isEmpty()) {
            user.setImageUrl(null);
        }else {
            user.setImageUrl(imageUrl);
        }
        User user1= userRepository.save(user);
        return UpdateUserImageDto.builder()
                .imageUrl(user1.getImageUrl())
                .build();
    }
    public void verify(String userId) {
        log.info("Verifying user {}", userId);
        UserResource userResource = keycloak.realm("docuapi").users().get(userId);
        userResource.sendVerifyEmail();

    }

    public Map<String, Object> getValidTokens(OAuth2AuthorizedClient client, OidcUser oidcUser) {
        Map<String, Object> tokens = new HashMap<>();

        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest.withClientRegistrationId("keycloak")
                .principal(oidcUser.getName())
                .build();

        OAuth2AuthorizedClient updatedClient = authorizedClientManager.authorize(authorizeRequest);

        // no need
        if (updatedClient != null) {
            OAuth2AccessToken accessToken = updatedClient.getAccessToken();
            tokens.put("accessToken", accessToken.getTokenValue());
            tokens.put("refreshToken", updatedClient.getRefreshToken() != null
                    ? updatedClient.getRefreshToken().getTokenValue()
                    : null);
            tokens.put("claims", oidcUser.getClaims());
        }

        return tokens;
    }
}
