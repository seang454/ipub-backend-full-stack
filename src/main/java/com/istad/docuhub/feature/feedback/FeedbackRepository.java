package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.domain.Feedback;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

    @Override
    Page<Feedback> findAll(@NotNull Pageable pageable);

    Feedback findByPaper_Uuid(String paperUuid);

    List<Feedback> findByReceiver_Uuid(String receiverUuid);
}
