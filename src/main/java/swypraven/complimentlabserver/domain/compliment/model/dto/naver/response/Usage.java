package swypraven.complimentlabserver.domain.compliment.model.dto.naver.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;

}
