package com.istad.docuhub.feature.paper.service;


import com.istad.docuhub.feature.paper.PaperRepository;
import com.istad.docuhub.feature.paper.dto.PaperRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaperServiceImpl implements PaperService {

    private final PaperRepository paperRepository;

    @Override
    public void createPaper(PaperRequest paperRequest) {

    }
}
