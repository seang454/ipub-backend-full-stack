package com.istad.docuhub.feature.adviserDetail;

import com.istad.docuhub.domain.AdviserDetail;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailRequest;
import com.istad.docuhub.feature.adviserDetail.dto.AdviserDetailResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdviserDetailImpl implements AdviserService {

    private final AdviserDetailRepository adviserDetailRepository;

    // Convert Domain -> Response DTO
    private AdviserDetailResponse mapToResponse(AdviserDetail adviserDetail) {
        return AdviserDetailResponse.builder()
                .yearsExperience(adviserDetail.getExperienceYears())
                .linkedinUrl(adviserDetail.getLinkedinUrl())
                .publication(adviserDetail.getPublication())
                .availability(adviserDetail.getStatus())
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
        adviserDetail.setStatus(adviserDetailRequest.status());
        adviserDetail.setIsDeleted(false);

        AdviserDetail saved = adviserDetailRepository.save(adviserDetail);
        return mapToResponse(saved);
    }

    @Override
    public AdviserDetailResponse getAdviserDetailByUuid(String uuid) {
        AdviserDetail adviserDetail = adviserDetailRepository.findAll()
                .stream()
                .filter(a -> a.getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Adviser detail not found with uuid: " + uuid));
        return mapToResponse(adviserDetail);
    }

    @Override
    public AdviserDetailResponse updateAdviserDetail(String uuid, AdviserDetailRequest adviserDetailRequest) {
        AdviserDetail adviserDetail = adviserDetailRepository.findAll()
                .stream()
                .filter(a -> a.getUuid().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Adviser detail not found with uuid: " + uuid));

        adviserDetail.setExperienceYears(adviserDetailRequest.experienceYears());
        adviserDetail.setLinkedinUrl(adviserDetailRequest.linkedinUrl());
        adviserDetail.setPublication(adviserDetailRequest.publication());
        adviserDetail.setSocialLinks(adviserDetailRequest.socialLinks());
        adviserDetail.setStatus(adviserDetailRequest.status());

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
}
