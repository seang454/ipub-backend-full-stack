package com.istad.docuhub.feature.adminDetail;

import com.istad.docuhub.domain.AdviserDetail;
import com.istad.docuhub.domain.StudentDetail;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.enums.STATUS;
import com.istad.docuhub.feature.adviserDetail.AdviserDetailRepository;
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
    private final AdviserDetailRepository adviserDetailRepository;

    @Override
    public void createStudent(UserCreateDto userCreateDto) {
        createUserWithRole(userCreateDto, "STUDENT");
    }

    @Override
    public void createAdviser(UserCreateDto userCreateDto) {
        createUserWithRole(userCreateDto, "ADVISER");
    }

    // 🔁 reusable method
    private void createUserWithRole(UserCreateDto userCreateDto, String roleName) {
        // Validate passwords
        if (!userCreateDto.password().equals(userCreateDto.confirmedPassword())) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Passwords do not match");
        }

        if (userCreateDto.password().length() < 8) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Password must be at least 8 characters");
        }

        // Sanitize username
        String safeUsername = userCreateDto.username()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", ".")
                .replaceAll("[^a-z0-9._-]", "");

        if (safeUsername.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid username after sanitization");
        }

        // Build Keycloak user
        UserRepresentation user = new UserRepresentation();
        user.setUsername(safeUsername);
        user.setEmail(userCreateDto.email());
        user.setFirstName(userCreateDto.firstname());
        user.setLastName(userCreateDto.lastname());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(userCreateDto.password());
        user.setCredentials(List.of(cred));

        user.setEmailVerified(false);
        user.setEnabled(true);

        try (Response response = keycloak.realm("docuapi").users().create(user)) {
            String respBody = response.readEntity(String.class);
            log.info("Keycloak response status: {}", response.getStatus());
            log.info("Keycloak response body: {}", respBody);

            if (response.getStatus() != HttpStatus.CREATED.value()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        roleName + " creation failed: " + respBody);
            }

            // Fetch created user
            AtomicReference<String> userUuid = new AtomicReference<>("");
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

            // Assign role
            UserResource userResource = keycloak.realm("docuapi").users().get(userUuid.get());
            RoleRepresentation role = keycloak.realm("docuapi").roles().get(roleName).toRepresentation();
            userResource.roles().realmLevel().add(Collections.singletonList(role));

            // Generate unique DB ID
            Integer id;
            int retries = 0;
            do {
                if (retries++ > 50) throw new RuntimeException("Unable to generate unique ID after 50 attempts");
                id = new Random().nextInt(1_000_000);
            } while (userRepository.existsByIdAndIsDeletedFalse(id));

            String fullName = userCreateDto.firstname() + " " + userCreateDto.lastname();

            // Save User
            User saveUser = User.builder()
                    .id(id)
                    .uuid(userUuid.get())
                    .fullName(fullName)
                    .isUser(true)
                    .isAdmin(false)
                    .isAdvisor("ADVISER".equals(roleName))
                    .isStudent("STUDENT".equals(roleName))
                    .isDeleted(false)
                    .isActive(true)
                    .createDate(LocalDate.now())
                    .updateDate(LocalDate.now())
                    .slug(SlugUtil.toSlug(fullName, safeUsername))
                    .build();
            userRepository.save(saveUser);

            // Student Detail
            if (saveUser.getIsStudent()) {
                StudentDetail studentDetail = new StudentDetail();
                Integer idStudent;
                int retriesStudent = 0;
                do {
                    if (retriesStudent++ > 50)
                        throw new RuntimeException("Unable to generate unique ID after 50 attempts");
                    idStudent = new Random().nextInt(1_000_000);
                } while (studentDetailRepository.existsById(idStudent));
                studentDetail.setId(idStudent);
                studentDetail.setUuid(UUID.randomUUID().toString());
                studentDetail.setStatus(STATUS.APPROVED);
                studentDetail.setIsStudent(true);
                studentDetail.setMajor("Information Technology");
                studentDetail.setUniversity("Institute of Science Technology and Advanced Development");
                studentDetail.setUser(saveUser);
                studentDetail.setStudentCardUrl("https://placehold.co/600x400.png");
                studentDetailRepository.save(studentDetail);
            }

            // Adviser Detail
            if (saveUser.getIsAdvisor()) {
                AdviserDetail adviserDetail = new AdviserDetail();
                Integer idAdviser;
                int retriesAdviser = 0;
                do {
                    if (retriesAdviser++ > 50)
                        throw new RuntimeException("Unable to generate unique ID after 50 attempts");
                    idAdviser = new Random().nextInt(1_000_000);
                } while (adviserDetailRepository.existsById(idAdviser));
                adviserDetail.setId(idAdviser);
                adviserDetail.setOffice("ISTAD");
                adviserDetail.setExperienceYears(2);
                adviserDetail.setIsDeleted(false);
                adviserDetail.setLinkedinUrl("https://www.linkedin.com/in/istad/");
                adviserDetail.setSocialLinks("https://www.facebook.com/istad.co");
                adviserDetail.setStatus("APPROVED");
                adviserDetail.setUuid(UUID.randomUUID().toString());
                adviserDetail.setUser(saveUser);
                adviserDetailRepository.save(adviserDetail);
            }

            log.info("{} created with ID: {} and UUID: {}", roleName, saveUser.getId(), saveUser.getUuid());

        } catch (Exception e) {
            log.error("Error creating user in Keycloak: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, roleName + " creation failed: " + e.getMessage());
        }
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
