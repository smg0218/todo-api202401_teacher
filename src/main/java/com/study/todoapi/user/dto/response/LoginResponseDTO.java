package com.study.todoapi.user.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.study.todoapi.user.entity.Role;
import com.study.todoapi.user.entity.User;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private String email;
    private String userName;

    @JsonFormat(pattern = "yyyy년 MM월 dd일")
    private LocalDate joinDate;

    private String token; // 로그인 인증토큰

    private String role;

    public LoginResponseDTO(User user, String token) {
        this.email = user.getEmail();
        this.userName = user.getUserName();
        this.joinDate = LocalDate.from(user.getJoinDate());
        this.token = token;
        this.role = user.getRole().toString();
    }

}
