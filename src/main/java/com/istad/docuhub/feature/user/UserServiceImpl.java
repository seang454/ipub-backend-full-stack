package com.istad.docuhub.feature.user;

import com.istad.docuhub.Repository.UserRepository;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.user.dto.AuthResponse;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.feature.user.dto.UserResponse;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
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
                log.info("User created with id " + user.getUsername());
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
                int retries = 0;
                do {
                    if (retries++ > 50) {
                        throw new RuntimeException("Unable to generate unique ID after 50 attempts");
                    }
                    id = new Random().nextInt(Integer.parseInt("1000000"));
                }
                while (userRepository.existsByIdAndIsDeletedFalse(id));
                String fullName = userCreateDto.firstname() + " " + userCreateDto.lastname();
                log.info("User id ",user.getId());

                User saveUser = User.builder()
                        .id(id)
                        .uuid(UserUuid.get())
                        .fullName(fullName)
                        .isUser(true)
                        .isAdmin(false)
                        .isAdvisor(false)
                        .isStudent(false)
                        .isDeleted(false)
                        .createDate(LocalDate.now())
                        .updateDate(LocalDate.now())
                        .build();
                userRepository.save(saveUser);

                return UserResponse.builder()
                        .id(id)
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
                        .build();
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "user Unauthorized");
    }
    @Override
    public AuthResponse login(String username, String password) {
        return null;
    }

    public void verify(String userId) {
        log.info("Verifying user {}", userId);
        UserResource userResource = keycloak.realm("docuapi").users().get(userId);
        userResource.sendVerifyEmail();

    }
}
