package com.istad.docuhub.feature.star;

import com.istad.docuhub.domain.User;
import com.istad.docuhub.feature.star.dto.StarResponse;

import java.util.List;

public interface StarService {

    StarResponse starReaction(String paperUuid);

    void unstarReaction(String paperUuid);

    long countByPaperUuid(String paperUuid);

    List<User> getUsersByPaperUuid(String paperUuid);

}
