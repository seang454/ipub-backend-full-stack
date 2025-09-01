package com.istad.docuhub.feature.feedback;

import com.istad.docuhub.domain.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
}
