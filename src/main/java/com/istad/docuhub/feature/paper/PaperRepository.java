package com.istad.docuhub.feature.paper;

import com.istad.docuhub.domain.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperRepository extends JpaRepository<Paper, Integer> {
}
