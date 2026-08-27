package swypraven.complimentlabserver.domain.chat.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;

}
