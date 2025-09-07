package com.istad.docuhub.feature.adminDetail;

import com.istad.docuhub.domain.StudentDetail;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.enums.STATUS;
import com.istad.docuhub.feature.studentDetail.StudentDetailRepository;
import com.istad.docuhub.feature.user.UserRepository;
import com.istad.docuhub.feature.user.dto.UserCreateDto;
import com.istad.docuhub.slugGeneration.SlugUtil;
import jakarta.ws.rs.NotFoundException;
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
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final Keycloak keycloak;
    private final UserRepository userRepository;
    private final StudentDetailRepository studentDetailRepository;

    @Override
    public void createStudent(UserCreateDto userCreateDto) {
        createUserWithRole(userCreateDto, "STUDENT");
    }

    @Override
    public void createAdviser(UserCreateDto userCreateDto) {
        createUserWithRole(userCreateDto, "ADVISER");
    }

    // 🔁 reusable method
    @Override
    public void createUserWithRole(UserCreateDto userCreateDto, String roleName) {
        if (!userCreateDto.password().equals(userCreateDto.confirmedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Passwords do not match");
        }

        // Build Keycloak user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(userCreateDto.username());
        user.setEmail(userCreateDto.email());
        user.setFirstName(userCreateDto.firstname());
        user.setLastName(userCreateDto.lastname());

        // Credentials
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(userCreateDto.password());
        user.setCredentials(List.of(cred));

        user.setEmailVerified(false);
        user.setEnabled(true);

        try (Response response = keycloak.realm("docuapi").users().create(user)) {
            AtomicReference<String> userUuid = new AtomicReference<>("");

            if (response.getStatus() == HttpStatus.CREATED.value()) {
                // Fetch created user
                List<UserRepresentation> foundUsers = keycloak.realm("docuapi")
                        .users()
                        .search(user.getUsername(), true);

                foundUsers.stream()
                        .filter(u -> !u.isEmailVerified())
                        .findFirst()
                        .ifPresent(u -> userUuid.set(u.getId()));

                if (userUuid.get().isEmpty()) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                            "Cannot fetch created user with role: " + roleName);
                }

                // ✅ Assign role
                UserResource userResource = keycloak.realm("docuapi").users().get(userUuid.get());
                RoleRepresentation role = keycloak.realm("docuapi").roles().get(roleName).toRepresentation();
                userResource.roles().realmLevel().add(Collections.singletonList(role));

                // Generate unique ID for DB
                Integer id;
                int retries = 0;
                do {
                    if (retries++ > 50) {
                        throw new RuntimeException("Unable to generate unique ID after 50 attempts");
                    }
                    id = new Random().nextInt(1_000_000);
                } while (userRepository.existsByIdAndIsDeletedFalse(id));

                String fullName = userCreateDto.firstname() + " " + userCreateDto.lastname();

                // Save to DB with flags
                User saveUser = User.builder()
                        .id(id)
                        .uuid(userUuid.get())
                        .fullName(fullName)
                        .isUser(false)
                        .isAdmin(false)
                        .isAdvisor("ADVISER".equals(roleName))
                        .isStudent("STUDENT".equals(roleName))
                        .isDeleted(false)
                        .createDate(LocalDate.now())
                        .updateDate(LocalDate.now())
                        .slug(SlugUtil.toSlug(fullName, userCreateDto.username()))
                        .build();

                userRepository.save(saveUser);

                log.info("{} created with ID: {} and UUID: {}", roleName, saveUser.getId(), saveUser.getUuid());
                return;
            }
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, roleName + " creation failed");
    }

    @Override
    public void promoteAsStudent(String studentUuidOrUsername) {
        RealmResource realmResource = keycloak.realm("docuapi");

        UserResource userResource = null;
        UserRepresentation userRepresentation = null;

        try {
            // 🔎 Try by UUID
            userResource = realmResource.users().get(studentUuidOrUsername);
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            // 🔎 Try by username/email exact match
            List<UserRepresentation> foundUsers = realmResource.users().search(studentUuidOrUsername, true);
            userRepresentation = foundUsers.stream()
                    .filter(u -> studentUuidOrUsername.equalsIgnoreCase(u.getUsername()) ||
                            studentUuidOrUsername.equalsIgnoreCase(u.getEmail()))
                    .findFirst()
                    .orElse(null);

            if (userRepresentation != null) {
                userResource = realmResource.users().get(userRepresentation.getId());
            }
        }

        if (userRepresentation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
        }

        // ✅ Local DB check
        Optional<User> userOpt = userRepository
                .getUserByUuidAndIsStudentIsFalseAndIsDeletedIsFalse(userRepresentation.getId());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User not found in local DB or already a student");
        }

        if (!Boolean.TRUE.equals(userRepresentation.isEnabled())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is disabled in Keycloak");
        }

        // ✅ Ensure role exists
        RoleRepresentation roleRepresentation;
        try {
            roleRepresentation = realmResource.roles().get("STUDENT").toRepresentation();
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "STUDENT role not found in Keycloak");
        }

        // ✅ Assign STUDENT role if missing
        boolean hasStudentRole = userResource.roles().realmLevel().listAll().stream()
                .anyMatch(r -> "STUDENT".equals(r.getName()));
        if (!hasStudentRole) {
            userResource.roles().realmLevel().add(Collections.singletonList(roleRepresentation));
        }

        // ✅ Update local DB
        User user = userOpt.get();
        user.setIsStudent(true);
        userRepository.save(user);


        // update student detail when apporve
        StudentDetail stdt = studentDetailRepository.findByUser_Uuid(studentUuidOrUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found in Keycloak"));
        stdt.setIsStudent(true);
        stdt.setStatus(STATUS.APPROVED);
        studentDetailRepository.save(stdt);

    }
}
