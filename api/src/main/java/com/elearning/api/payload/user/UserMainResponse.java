package com.elearning.api.payload.user;

import com.elearning.common.common.Pagination;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserMainResponse {

    List<UserResponse> users;

    Map<String, Long> userCount;

    Map<String, Long> userCountByDate;

    Pagination pagination;

    @Builder
    public UserMainResponse(List<UserResponse> userResponses, Map<String, Long> userCount, Map<String, Long> userCountByDate, Page<?> page) {
        this.users = userResponses;
        this.userCount = userCount;
        this.userCountByDate = userCountByDate;
        this.pagination = new Pagination(page);
    }


}
