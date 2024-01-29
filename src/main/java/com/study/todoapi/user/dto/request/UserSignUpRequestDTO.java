package com.study.todoapi.user.dto.request;

import com.study.todoapi.user.entity.User;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSignUpRequestDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 20)
    private String password;

    @NotBlank
    @Size(min = 2, max = 5)
    private String userName;

    // 엔터티로 변환해주는 메서드
    public User toEntity(PasswordEncoder encoder, String profilePath) {
        return User.builder()
                .email(this.email)
                .password(encoder.encode(this.password))
                .userName(this.userName)
                .profileImg(profilePath)
                .build();
    }
}
