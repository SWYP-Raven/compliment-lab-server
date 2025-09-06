package swypraven.complimentlabserver.domain.compliment.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ComplimentLogPatchRequest(
        @JsonProperty("is_read") Boolean isRead,
        @JsonProperty("is_archived") Boolean isArchived
) {}
