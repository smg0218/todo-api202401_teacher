package com.study.todoapi.todo.controller;

import com.study.todoapi.auth.TokenUserInfo;
import com.study.todoapi.todo.dto.request.TodoCheckRequestDTO;
import com.study.todoapi.todo.dto.request.TodoCreateRequestDTO;
import com.study.todoapi.todo.dto.response.TodoDetailResponseDTO;
import com.study.todoapi.todo.dto.response.TodoListResponseDTO;
import com.study.todoapi.todo.entity.Todo;
import com.study.todoapi.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos")
//@CrossOrigin(origins = {"http://localhost:3000"}) // API접근을 허용할 클라이언트 아이피
public class TodoController {

    private final TodoService todoService;

    // 할 일 등록 요청
    @PostMapping
    public ResponseEntity<?> createTodo(
            @AuthenticationPrincipal TokenUserInfo userInfo // 토큰에 들어있는 파싱된 로그인유저정보를 시큐리티가 주입해줌
            , @Validated @RequestBody TodoCreateRequestDTO dto
            , BindingResult result
    ) {

        if (result.hasErrors()) {
            log.warn("DTO검증 에러!! : {}", result.getFieldError());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }

        try {
            TodoListResponseDTO dtoList = todoService.create(dto, userInfo.getEmail());
            return ResponseEntity.ok().body(dtoList);
        } catch (IllegalStateException e) {
          // 권한에 따른 에러
          log.warn(e.getMessage());
          return ResponseEntity
                  .status(HttpStatus.UNAUTHORIZED) //UNAUTHORIZED 대신 401도 가능
                  .body(e.getMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder().error(e.getMessage()).build());
        }

    }

    // 할 일 목록 조회 요청
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("/api/todos GET! ");

        TodoListResponseDTO retrieve = todoService.retrieve(userInfo.getEmail());

        return ResponseEntity.ok().body(retrieve);
    }

    // 할 일 삭제 요청
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @PathVariable String id) {

        log.info("/api/todos/{} DELETE !!", id);

        if (id == null || id.trim().equals("")) {
            return ResponseEntity
                    .badRequest()
                    .body(TodoListResponseDTO
                            .builder()
                            .error("ID는 공백일 수 없습니다!!")
                            .build());
        }

        try {
            TodoListResponseDTO dtoList = todoService.delete(id, userInfo.getEmail());
            return ResponseEntity.ok().body(dtoList);
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(TodoListResponseDTO.builder().error(e.getMessage()).build());
        }

    }

    // 할 일 완료 체크처리 요청
    @RequestMapping(method = {PUT, PATCH})
    public ResponseEntity<?> updateTodo(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody TodoCheckRequestDTO dto,
            HttpServletRequest request
    ) {

        log.info("/api/todos {}", request.getMethod());
        log.debug("dto: {}", dto);

        try {
            TodoListResponseDTO dtoList = todoService.check(dto, userInfo.getEmail());
            return ResponseEntity.ok().body(dtoList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(TodoListResponseDTO.builder().error(e.getMessage()).build());
        }
    }

}
