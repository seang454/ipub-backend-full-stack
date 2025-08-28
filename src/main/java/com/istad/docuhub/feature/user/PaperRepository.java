package com.istad.docuhub.Repository;

import com.istad.docuhub.domain.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Integer> {

    Optional<Paper> findPaperById(Integer id);

}
