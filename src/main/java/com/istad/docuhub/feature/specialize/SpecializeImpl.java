package com.istad.docuhub.feature.specialize;

import com.istad.docuhub.domain.Specialize;
import com.istad.docuhub.feature.specialize.dto.SpecializeRequest;
import com.istad.docuhub.feature.specialize.dto.SpecializeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpecializeImpl implements SpecializeService {

    private final SpecializeRepository specializeRepository;

    private SpecializeResponse mapToResponse(Specialize specialize) {
        return new SpecializeResponse(
                specialize.getUuid(),
                specialize.getName(),
                specialize.getSlug()
        );
    }

    @Override
    public SpecializeResponse createSpecialize(SpecializeRequest request) {
        if (specializeRepository.existsBySlug(request.slug())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Slug already exists");
        }

        int id;
        int retries = 0;
        do {
            if (retries++ > 50) {
                throw new RuntimeException("Unable to generate unique ID after 50 attempts");
            }
            id = new Random().nextInt(Integer.parseInt("1000000"));
        } while (specializeRepository.existsById(id));

        Specialize specialize = new Specialize();
        specialize.setId(id);
        specialize.setUuid(UUID.randomUUID().toString());
        specialize.setName(request.name());
        specialize.setSlug(request.slug());

        Specialize saved = specializeRepository.save(specialize);
        return mapToResponse(saved);
    }

    @Override
    public List<SpecializeResponse> getAllSpecializes() {
        return specializeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public SpecializeResponse getSpecializeByUuid(String uuid) {
        Specialize specialize = specializeRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialize not found"));
        return mapToResponse(specialize);
    }

    @Override
    public SpecializeResponse updateSpecialize(String uuid, SpecializeRequest request) {
        Specialize specialize = specializeRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialize not found"));

        specialize.setName(request.name());
        specialize.setSlug(request.slug());

        Specialize updated = specializeRepository.save(specialize);
        return mapToResponse(updated);
    }

    @Override
    public void deleteSpecialize(String uuid) {
        Specialize specialize = specializeRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Specialize not found"));
        specializeRepository.delete(specialize);
    }
}
