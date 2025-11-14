package com.elearning.api.payload.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DeleteReqID {

    @NotEmpty(message = "IDs list cannot be empty")
    @JsonProperty("ids")
    private List<Long> Ids;

}
