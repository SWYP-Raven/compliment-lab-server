package swypraven.complimentlabserver.domain.compliment.model.response;

import swypraven.complimentlabserver.domain.compliment.model.dto.DayComplimentDto;

import java.util.List;

public record ComplimentListResponse(List<DayComplimentDto> compliments) {}

