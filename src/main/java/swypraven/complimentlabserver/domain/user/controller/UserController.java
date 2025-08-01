package swypraven.complimentlabserver.domain.user.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import swypraven.complimentlabserver.global.exception.user.UserErrorCode;
import swypraven.complimentlabserver.global.exception.user.UserException;
import swypraven.complimentlabserver.global.response.ApiResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<ApiResponse<String>> get() {
        throw new UserException(UserErrorCode.USER_NOT_FOUND);
    }

}
