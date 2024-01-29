package com.study.todoapi.todo.service;

import com.study.todoapi.todo.dto.request.TodoCheckRequestDTO;
import com.study.todoapi.todo.dto.request.TodoCreateRequestDTO;
import com.study.todoapi.todo.dto.response.TodoDetailResponseDTO;
import com.study.todoapi.todo.dto.response.TodoListResponseDTO;
import com.study.todoapi.todo.entity.Todo;
import com.study.todoapi.todo.repository.TodoRepository;
import com.study.todoapi.user.entity.Role;
import com.study.todoapi.user.entity.User;
import com.study.todoapi.user.repoistory.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional  // JPA 사용시 필수
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    // 할 일 등록
    public TodoListResponseDTO create(TodoCreateRequestDTO dto, String email) {
        Optional<User> foundUser = userRepository.findByEmail(email);

        foundUser.ifPresent(user -> {

            // 권한에 따른 글쓰기 제한 처리
            // 일반 회원(COMMON)이 5개 초과의 일정을 등록하면 예외를 발생시킨다.
            if (user.getRole() == Role.COMMON && user.getTodoList().size() >= 5) {
                throw new IllegalStateException("일반 회원은 더 이상 일정을 작성할 수 없습니다.");
            }
            Todo todo = todoRepository.save(dto.toEntity(user));
            // 양방향 매핑에서는 한쪽이 수정(삽입, 삭제)되면 반대편에는 수동으로 갱신을 해줘야 한다
            user.addTodo(todo);
            log.info("새로운 할 일이 저장되었습니다. 제목: {}", dto.getTitle());
        });

        return retrieve(email);
    }


    // 할 일 목록 불러오기
    public TodoListResponseDTO retrieve(String email) {

//        List<Todo> todoList = todoRepository.findAll();
//        List<Todo> allByUser = todoRepository.findAllByUser(userRepository.findByEmail(email));

        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("유저정보가 없습니다!"));

        List<Todo> todoList = user.getTodoList();

        // 엔터티 리스트를 DTO리스트로 매핑
        List<TodoDetailResponseDTO> dtoList = todoList.stream()
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();
    }

    // 할 일 삭제
    public TodoListResponseDTO delete(String id, String email) {

        try {
            Todo todo = todoRepository.findById(id).orElseThrow();
            
            // 양방향에서는 반대편 리스트는 수동으로 갱신해야 함
            User user = userRepository.findByEmail(email).orElseThrow();
            user.getTodoList().remove(todo);

            todoRepository.deleteById(id);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, error: {}",
                    id, e.getMessage());
            throw new RuntimeException("삭제에 실패했습니다!!");
        }
        return retrieve(email);
    }

    // 할 일 체크 처리
    public TodoListResponseDTO check(TodoCheckRequestDTO dto, String email) {

        Optional<Todo> target = todoRepository.findById(dto.getId());

        target.ifPresent(todo -> {
            todo.setDone(dto.isDone());
            todoRepository.save(todo);
        });

        return retrieve(email);
    }

}
