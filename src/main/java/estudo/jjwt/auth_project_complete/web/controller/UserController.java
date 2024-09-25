package estudo.jjwt.auth_project_complete.web.controller;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.service.UserService;
import estudo.jjwt.auth_project_complete.web.dto.UserChangePasswordDto;
import estudo.jjwt.auth_project_complete.web.dto.UserCreateDto;
import estudo.jjwt.auth_project_complete.web.dto.UserResponseDto;
import estudo.jjwt.auth_project_complete.web.mapper.UserMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v2/users")
@RestController
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id){
        User user = service.findById(id);
        return ResponseEntity.ok(UserMapper.toResponseDto(user));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAll(){
        List<User> users = service.findAll();
        return ResponseEntity.ok(UserMapper.toListResponseDto(users));
    }

    @PostMapping
    public ResponseEntity<UserResponseDto> insert(@RequestBody @Valid UserCreateDto dto){
        User user = UserMapper.toUser(dto);
        return ResponseEntity.status(201).body(UserMapper.toResponseDto(service.insert(user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserChangePasswordDto dto){
        service.updatePasswordById(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

}
