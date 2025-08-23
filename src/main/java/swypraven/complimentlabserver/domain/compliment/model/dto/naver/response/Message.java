package swypraven.complimentlabserver.domain.compliment.model.dto.naver.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
class Message {
    private String role;
    private String content;

    // status 객체에 대한 DTO
    static class Status {
        private String code;
        private String message;
    }
}
