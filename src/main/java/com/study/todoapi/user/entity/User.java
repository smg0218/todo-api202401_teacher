package com.study.todoapi.user.entity;

import com.study.todoapi.todo.entity.Todo;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter @Getter
@ToString(exclude = "todoList")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tbl_user")
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id; // account가 아니라 랜덤식별번호

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String userName;

    @CreationTimestamp
    private LocalDateTime joinDate;

    @Enumerated(EnumType.STRING)
//    @ColumnDefault("'COMMON'")
    @Builder.Default // 회원가입시 자동으로 기본값 처리
    private Role role = Role.COMMON;

    private String profileImg; // 프로필 사진 이미지 경로

    @OneToMany(mappedBy = "user")
    private List<Todo> todoList = new ArrayList<>();

    public void addTodo(Todo todo) {
        this.todoList.add(todo);
        todo.setUser(this);
    }
}
