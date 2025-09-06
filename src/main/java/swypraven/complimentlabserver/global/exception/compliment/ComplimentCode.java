package swypraven.complimentlabserver.global.exception.compliment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import swypraven.complimentlabserver.global.exception.common.ErrorCode;

@Getter
@AllArgsConstructor
public enum ComplimentCode implements ErrorCode {

    MASTER_EMPTY(HttpStatus.SERVICE_UNAVAILABLE, "칭찬 마스터 데이터가 비어 있습니다. 관리자에게 문의하세요.");

    private final HttpStatus httpStatus;
    private final String message;

    @Override
    public String getCode() {
        return name(); // enum 이름 자체를 코드로 사용
    }
}
