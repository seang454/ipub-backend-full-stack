package com.istad.docuhub.feature.user;

import com.istad.docuhub.domain.AdviserDetail;
import com.istad.docuhub.domain.StudentDetail;
import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.adviserDetail.AdviserDetailRepository;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import com.istad.docuhub.feature.studentDetail.StudentDetailRepository;
import com.istad.docuhub.feature.studentDetail.dto.StudentResponse;
import com.istad.docuhub.feature.user.dto.*;
import com.istad.docuhub.feature.user.mapper.UseMapper;
import com.istad.docuhub.feature.user.mapper.UserMapperManual;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private final OAuth2AuthorizedClientManager authorizedClientManager;
    private final StudentDetailRepository studentDetailRepository;
    private final AdviserDetailRepository adviserDetailRepository;

    @Override
    public UserResponse register(UserCreateDto userCreateDto) {
        log.info("Registering user in service {}", userCreateDto);
        if (!userCreateDto.password().equals(userCreateDto.confirmedPassword())) {
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
        log.info("Registering user in service {}", user);

        try (Response response = keycloak.realm("docuapi").users().create(user)) {
            log.info("Created user {}", response.getStatus());
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                //example : http://localhost:9090/admin/realms/docuapi/users/414d407a-65a2-4814-b595-3759b64fc12b
                AtomicReference<String> UserUuid = new AtomicReference<>("");
                if (response.getStatus() == HttpStatus.CREATED.value()) {
                    //verify email
                    List<UserRepresentation> userRepresentations = keycloak.realm("docuapi")
                            .users()
                            .search(user.getUsername(), true);
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
                    log.info("User id ", user.getId());

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
                            .slug(SlugUtil.toSlug(fullName, userCreateDto.username()))
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
            } else {

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Something went wrong or user already exited");
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can not register user");
    }

    @Override
    public Page<UserResponse> getAllUsersByPage(int page, int size) {
        // 1️⃣ Fetch non-deleted users from DB with pagination
        Page<User> usersPage = userRepository.findByIsDeletedFalse(PageRequest.of(page, size));
        List<User> users = usersPage.getContent();

        // 2️⃣ Fetch all Keycloak users (enabled only)
        RealmResource realmResource = keycloak.realm("docuapi");
        List<UserRepresentation> userRepresentations = realmResource.users().list()
                .stream()
                .filter(UserRepresentation::isEnabled)
                .toList();

        // 3️⃣ Map only users that exist in Keycloak
        List<UserResponse> userResponses = users.stream()
                .map(user -> {
                    return userRepresentations.stream()
                            .filter(ur -> ur.getId().equals(user.getUuid()))
                            .findFirst()
                            .map(ur -> UserMapperManual.mapUserToUserResponse(user, ur))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .toList();

        // 4️⃣ Return as Page<UserResponse> with same pagination metadata
        return new PageImpl<>(userResponses, usersPage.getPageable(), usersPage.getTotalElements());
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
                        UserMapperManual.mapUserToUserResponse(user, userRepresentation)
                );
            }
        }
        return userResponses;
    }

    @Override
    public UserResponse getSingleUser(String uuid) {
        UserResponse userResponse = getAllUsers().stream().filter(user -> uuid.equals(user.uuid())).findFirst().orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
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
                        UserMapperManual.mapUserToUserResponse(singleUser.get(), userRepresentation)
                );
            }
        }
        return userResponses;
    }

    @Override
    public void deleteUser(String userId) {
        log.info("User id {} ", userId);
        // userId should be String, not Integer
        // Get the Keycloak user
        RealmResource realmResource = keycloak.realm("docuapi");
        UserResource userResource = realmResource.users().get(userId);
        UserRepresentation userRep = userResource.toRepresentation();
        if (userRep != null && userRep.isEnabled()) {
            // Soft delete in local DB
            User dbUser = userRepository.findByUuidAndIsDeletedFalse(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled"));
            dbUser.setIsDeleted(true);
            log.info("dbUser {} ", dbUser);
            userRepository.save(dbUser);
            // Disable user in Keycloak
            userRep.setEnabled(false);
            userResource.update(userRep);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled");
        }
    }

    @Override
    public void updateUser(String userUuid, UpdateUserDto updateUserDto) {
        RealmResource realmResource = keycloak.realm("docuapi");
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled"));
        UserResource userResource = realmResource.users().get(user.getUuid());
        UserRepresentation userRep = userResource.toRepresentation();
        if (userRep == null || !userRep.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled");
        }
        useMapper.updateUser(user, updateUserDto);
        user.setSlug(SlugUtil.toSlug(updateUserDto.fullName(), updateUserDto.userName()));
        user.setUpdateDate(LocalDate.now());
        User user1 = userRepository.save(user);

        if (updateUserDto.firstName() != null) userRep.setFirstName(updateUserDto.firstName());
        if (updateUserDto.lastName() != null) userRep.setLastName(updateUserDto.lastName());
        if (updateUserDto.email() != null) userRep.setEmail(updateUserDto.email());
        if (updateUserDto.userName() != null && !updateUserDto.userName().isBlank()) {
            userRep.setUsername(updateUserDto.userName()); // Only if allowed by Keycloak
        }
        log.info("updateUser {} ", userRep);
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
        User user = userRepository.findByUuidAndIsDeletedFalse(userUuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled"));
        UserResource userResource = realmResource.users().get(user.getUuid());
        UserRepresentation userRep = userResource.toRepresentation();
        if (userRep == null || !userRep.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or already disabled");
        }
        if (imageUrl == null || imageUrl.isBlank() || imageUrl.isEmpty()) {
            user.setImageUrl(null);
        } else {
            user.setImageUrl(imageUrl);
        }
        User user1 = userRepository.save(user);
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

    @Override
    public List<UserResponse> getAllPublicUser() {
        RealmResource realmResource = keycloak.realm("docuapi");
        List<User> user = userRepository.getUserByIsUserTrueAndIsAdvisorFalseAndIsStudentFalseAndIsAdminFalseAndIsDeletedFalse();
        List<UserRepresentation> userRepresentationList = realmResource.users().list().stream().filter(UserRepresentation::isEnabled).toList();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user1 : user) {
            UserRepresentation userRepresentation = userRepresentationList.stream().filter(userRepresentation1 -> userRepresentation1.getId().equals(user1.getUuid())).findFirst().get();
            userResponses.add(UserMapperManual.mapUserToUserResponse(user1, userRepresentation));
        }
        return userResponses;
    }

    @Override
    public List<UserResponse> getAllStudent() {
        RealmResource realmResource = keycloak.realm("docuapi");
        List<User> user = userRepository.getUserByIsUserTrueAndIsAdvisorFalseAndIsStudentTrueAndIsAdminFalseAndIsDeletedFalse();
        List<UserRepresentation> userRepresentationList = realmResource.users().list().stream().filter(UserRepresentation::isEnabled).toList();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user1 : user) {
            UserRepresentation userRepresentation = userRepresentationList.stream().filter(userRepresentation1 -> userRepresentation1.getId().equals(user1.getUuid())).findFirst().get();
            userResponses.add(UserMapperManual.mapUserToUserResponse(user1, userRepresentation));
        }
        return userResponses;
    }

    @Override
    public Map<String, Object> getAllMentor(Pageable pageable) {
        RealmResource realmResource = keycloak.realm("docuapi");

        Page<User> userPage = userRepository
                .getUserByIsUserTrueAndIsAdvisorTrueAndIsStudentFalseAndIsAdminFalseAndIsDeletedFalse(pageable);

        List<UserRepresentation> userRepresentationList = realmResource.users().list()
                .stream().filter(UserRepresentation::isEnabled).toList();

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(user1 -> {
                    UserRepresentation userRepresentation = userRepresentationList.stream()
                            .filter(ur -> ur.getId().equals(user1.getUuid()))
                            .findFirst()
                            .orElse(null);  // handle missing userRepresentation
                    return UserMapperManual.mapUserToUserResponse(user1, userRepresentation);
                })
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("content", userResponses);
        response.put("number", userPage.getNumber());
        response.put("totalPages", userPage.getTotalPages());
        response.put("totalElements", userPage.getTotalElements());

        return response;
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
    }


    @Override
    public void promoteAsMentor(String mentorUuidOrUsername) {
        RealmResource realmResource = keycloak.realm("docuapi");
        UserResource userResource = null;
        UserRepresentation userRepresentation = null;

        try {
            // Try direct lookup as UUID
            userResource = realmResource.users().get(mentorUuidOrUsername);
            userRepresentation = userResource.toRepresentation();
        } catch (NotFoundException e) {
            // If not found by UUID, search by username/email
            List<UserRepresentation> foundUsers = realmResource.users()
                    .search(mentorUuidOrUsername, true); // partial match allowed
            if (!foundUsers.isEmpty()) {
                userRepresentation = foundUsers.get(0);
                userResource = realmResource.users().get(userRepresentation.getId());
            }
        }

        if (userRepresentation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in Keycloak");
        }

        // ✅ Check local DB consistency
        Optional<User> userOpt = userRepository.getUserByUuidAndIsAdvisorIsFalseAndIsDeletedIsFalse(userRepresentation.getId());
        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found in local DB or already an advisor");
        }

        // ✅ Ensure Keycloak user is enabled
        if (!Boolean.TRUE.equals(userRepresentation.isEnabled())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User is disabled in Keycloak");
        }

        // ✅ Add ADVISER role
        RoleRepresentation adviserRole = realmResource.roles().get("ADVISER").toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(adviserRole));

        // ✅ Remove STUDENT role if present
        RoleRepresentation studentRole = realmResource.roles().get("STUDENT").toRepresentation();
        userResource.roles().realmLevel().remove(Collections.singletonList(studentRole));

        // ✅ Update local DB
        User user = userOpt.get();
        user.setIsAdvisor(true);
        user.setIsStudent(false);
        userRepository.save(user);
    }

    @Override
    public CurrentUser getCurrentUserSub() {
        JwtAuthenticationToken authentication =
                (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        return CurrentUser.builder()
                .id(authentication.getToken().getClaimAsString("sub"))
                .build();
    }

    @Override
    public UserProfileResponse getUserProfile(String uuid) {
        RealmResource realmResource = keycloak.realm("docuapi");
        List<UserRepresentation> userRepresentations = realmResource.users().list().stream().filter(UserRepresentation::isEnabled).toList();
        User user = userRepository.findByUuidAndIsDeletedFalse(uuid).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or is disabled"));
        UserResponse userResponse = UserMapperManual.mapUserToUserResponse(user,
                userRepresentations.stream()
                        .filter(userRepresentation -> userRepresentation.getId().equals(user.getUuid()))
                        .findFirst()
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found or is disabled"))
        );

        StudentResponse studentResponse = null;
        AdviserDetailResponse adviserDetailResponse = null;

        // ✅ Check role flag, not "status"
        if (Boolean.TRUE.equals(user.getIsStudent())) {
            StudentDetail studentDetail = studentDetailRepository.findByUser_Uuid(user.getUuid())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student details not found for user")
                    );
            studentResponse = StudentResponse.builder()
                    .uuid(user.getUuid())
                    .studentCardUrl(studentDetail.getStudentCardUrl())
                    .university(studentDetail.getUniversity())
                    .major(studentDetail.getMajor())
                    .yearsOfStudy(studentDetail.getYearsOfStudy())
                    .isStudent(studentDetail.getIsStudent())
                    .userUuid(studentDetail.getUser().getUuid())
                    .build();
        }

        if (Boolean.TRUE.equals(user.getIsAdvisor())) {
            AdviserDetail adviserDetail = adviserDetailRepository.findByUserUuid_Uuid(user.getUuid())
                    .orElseThrow(
                            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adviser details not found for user")
                    );
            adviserDetailResponse = AdviserDetailResponse.builder()
                    .yearsExperience(adviserDetail.getExperienceYears())
                    .linkedinUrl(adviserDetail.getLinkedinUrl())
                    .publication(adviserDetail.getPublication())
                    .availability(adviserDetail.getStatus())
                    .socialLinks(adviserDetail.getSocialLinks())
                    .userUuid(adviserDetail.getUser().getUuid())
                    .build();
        }

        return new UserProfileResponse(
                userResponse,
                studentResponse,
                adviserDetailResponse
        );
    }
}
