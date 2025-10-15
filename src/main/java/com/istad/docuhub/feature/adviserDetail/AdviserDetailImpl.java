package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.domain.AdviserAssignment;
import com.istad.docuhub.domain.AdviserDetail;
import com.istad.docuhub.feature.adviserAssignment.AdviserAssignmentRepository;
import com.istad.docuhub.feature.adviserAssignment.dto.AdviserAssignmentResponse;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailRequest;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import com.istad.docuhub.feature.adviserDetail.dto.UpdateAdviserDetailRequest;
import com.istad.docuhub.feature.user.UserService;
import com.istad.docuhub.feature.user.dto.CurrentUser;
import com.istad.docuhub.utils.CurrentUserV2;
import com.istad.docuhub.utils.QuickService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdviserDetailImpl implements AdviserService {

    private final AdviserDetailRepository adviserDetailRepository;
    private final QuickService quickService;
    private final UserService userService;
    private final AdviserAssignmentRepository adviserAssignmentRepository;

    // Convert Domain -> Response DTO
    private AdviserDetailResponse mapToResponse(AdviserDetail adviserDetail) {
        return AdviserDetailResponse.builder()
                .experienceYears(adviserDetail.getExperienceYears())
                .linkedinUrl(adviserDetail.getLinkedinUrl())
                .publication(adviserDetail.getPublication())
                .status(adviserDetail.getStatus())
                .socialLinks(adviserDetail.getSocialLinks())
                .userUuid(adviserDetail.getUser() != null ? adviserDetail.getUser().getUuid() : null)
                .build();
    }

    @Override
    public AdviserDetailResponse createAdviserDetail(AdviserDetailRequest adviserDetailRequest) {
        AdviserDetail adviserDetail = new AdviserDetail();
        Integer id;
        Integer retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (adviserDetailRepository.existsById(id));

        adviserDetail.setId(id);
        adviserDetail.setUuid(UUID.randomUUID().toString());
        adviserDetail.setExperienceYears(adviserDetailRequest.experienceYears());
        adviserDetail.setLinkedinUrl(adviserDetailRequest.linkedinUrl());
        adviserDetail.setPublication(adviserDetailRequest.publication());
        adviserDetail.setSocialLinks(adviserDetailRequest.socialLinks());
        adviserDetail.setIsDeleted(false);

        AdviserDetail saved = adviserDetailRepository.save(adviserDetail);
        return mapToResponse(saved);
    }

    @Override
    public AdviserDetailResponse getAdviserDetailByUuid(String uuid) {
        AdviserDetail adviserDetail = adviserDetailRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Adviser detail not found with uuid: " + uuid));
        return mapToResponse(adviserDetail);
    }

    @Override
    public AdviserDetailResponse updateAdviserDetailByUuid(String uuid, UpdateAdviserDetailRequest updateRequest) {
        AdviserDetail adviserDetail = adviserDetailRepository.findAll()
                .stream()
                .filter(a -> a.getUser().getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Adviser detail not found with uuid: " + uuid));

        adviserDetail.setExperienceYears(updateRequest.experienceYears());
        adviserDetail.setLinkedinUrl(updateRequest.linkedinUrl());
        adviserDetail.setPublication(updateRequest.publication());
        adviserDetail.setSocialLinks(updateRequest.socialLinks());
        adviserDetail.setStatus(updateRequest.status());

        AdviserDetail updated = adviserDetailRepository.save(adviserDetail);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAdviserDetail(String uuid) {
        AdviserDetail adviserDetail = adviserDetailRepository.findAll()
                .stream()
                .filter(a -> a.getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Adviser detail not found with uuid: " + uuid));

        adviserDetail.setIsDeleted(true); // soft delete
        adviserDetailRepository.save(adviserDetail);
    }

    @Override
    public List<AdviserDetailResponse> getAllAdviserDetails() {
        return adviserDetailRepository.findAll()
                .stream()
                .filter(adviserDetail -> !Boolean.TRUE.equals(adviserDetail.getIsDeleted()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AdviserDetailResponse updateByToken(UpdateAdviserDetailRequest updateRequest) {
        CurrentUserV2 currentUser = quickService.currentUserInfor();

        // Handle null roles safely
        List<String> roles = currentUser.getRoles() != null ? currentUser.getRoles() : List.of();

        // Find adviser detail by the current user uuid
        AdviserDetail adviserDetail = adviserDetailRepository.findByUser_Uuid(currentUser.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Adviser detail not found"));

        boolean isAdmin = roles.contains("ADMIN");
        boolean isOwner = adviserDetail.getUser().getUuid().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to update this adviser detail");
        }

        // ✅ Update fields from request
        adviserDetail.setExperienceYears(updateRequest.experienceYears());
        adviserDetail.setLinkedinUrl(updateRequest.linkedinUrl());
        adviserDetail.setPublication(updateRequest.publication());
        adviserDetail.setSocialLinks(updateRequest.socialLinks());
        adviserDetail.setStatus(updateRequest.status());

        AdviserDetail saved = adviserDetailRepository.save(adviserDetail);

        // ✅ Map entity → response DTO
        return new AdviserDetailResponse(
                saved.getUuid(),
                saved.getExperienceYears(),
                saved.getLinkedinUrl(),
                saved.getPublication(),
                saved.getSocialLinks(),
                saved.getStatus(),
                saved.getUser().getUuid(),
                saved.getSpecialize()
        );
    }

    @Override
    public Page<AdviserAssignmentResponse> getAllAssignment(Pageable pageable) {
        CurrentUser subId = userService.getCurrentUserSub();
        Page<AdviserAssignment> adviserAssignmentResponses = adviserAssignmentRepository.findByAdvisorUuid(subId.id(), pageable);
        return adviserAssignmentResponses.map(adviserAssignmentResponse -> new AdviserAssignmentResponse(
                adviserAssignmentResponse.getUuid(),
                adviserAssignmentResponse.getPaper().getUuid(),
                adviserAssignmentResponse.getAdvisor().getUuid(),
                adviserAssignmentResponse.getAdmin().getUuid(),
                adviserAssignmentResponse.getDeadline(),
                adviserAssignmentResponse.getStatus(),
                adviserAssignmentResponse.getAssignedDate(),
                adviserAssignmentResponse.getUpdateDate()
        ));
    }

}
