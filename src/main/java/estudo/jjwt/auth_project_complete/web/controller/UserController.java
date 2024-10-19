package estudo.jjwt.auth_project_complete.web.controller;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.jwt.JwtUserDetails;
import estudo.jjwt.auth_project_complete.repository.projection.UserProjection;
import estudo.jjwt.auth_project_complete.service.UserService;
import estudo.jjwt.auth_project_complete.web.dto.PageableDto;
import estudo.jjwt.auth_project_complete.web.dto.UserChangePasswordDto;
import estudo.jjwt.auth_project_complete.web.dto.UserCreateDto;
import estudo.jjwt.auth_project_complete.web.dto.UserResponseDto;
import estudo.jjwt.auth_project_complete.web.exception.CustomExceptionBody;
import estudo.jjwt.auth_project_complete.web.mapper.PageableMapper;
import estudo.jjwt.auth_project_complete.web.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RequestMapping("/api/v2/users")
@RestController
@Tag(name = "Usuários", description = "Recurso que permite gerenciar as operações CRUD para usuários")
public class UserController {

    @Autowired
    private UserService service;

    @Operation(summary = "Lista um usuário", description = "Busca o usuário pelo seu Id e retorna ao cliente.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso!", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "Usuário possui permissão insuficiente.", content = {
                            @Content(schema = @Schema(implementation = CustomExceptionBody.class))
                    })
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') " +
            "or (hasAuthority('MIN_ACCESS') " +
            "and #id == authentication.principal.id)")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        User user = service.findById(id);
        return ResponseEntity.ok(UserMapper.toResponseDto(user));
    }

    @Operation(summary = "Lista todos os usuários", description = "Retorna todos os usuários cadastrados no banco de dados.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sucesso!", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = UserResponseDto.class)))
                    }),
                    @ApiResponse(responseCode = "403", description = "Usuário possui permissão insuficiente.", content = {
                            @Content(schema = @Schema(implementation = CustomExceptionBody.class))
                    })
            })
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<PageableDto> getAll(Pageable page) {
        Page<UserProjection> usersPage = service.findAll(page);
        return ResponseEntity.ok(PageableMapper.toDto(usersPage));
    }

    @Operation(summary = "Cria um novo usuário", description = "Insere um usuário no banco de dados.", responses = {
            @ApiResponse(responseCode = "201", description = "Inserido com sucesso!", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))
            }),
            @ApiResponse(responseCode = "409", description = "Já existe um usuário com o email informado.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class)),
            }),
            @ApiResponse(responseCode = "400", description = "Dados inválidos.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
            })
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> insert(@RequestBody @Valid UserCreateDto dto) {
        User user = UserMapper.toUser(dto);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/" + user.getId())
                        .build().toUri())
                .body(UserMapper.toResponseDto(service.insert(user)));
    }

    @Operation(summary = "Deleta um usuário", description = "Remove um usuário do banco de dados pelo seu Id.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Deletado com sucesso!", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado.", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "Usuário possui permissão insuficiente", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
                    })
            })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('MIN_ACCESS') and #id == authentication.principal.id)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Atualiza um usuário", description = "Muda a senha de um usuário encontrado pelo Id.",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "204", description = "Atualizado com sucesso", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = Void.class))
                    }),
                    @ApiResponse(responseCode = "400", description = "Dados inválidos", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = CustomExceptionBody.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "Usuário possui permissão insuficiente.", content = {
                            @Content(schema = @Schema(implementation = CustomExceptionBody.class))
                    })
            })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('MIN_ACCESS', 'ADMIN') and #id == authentication.principal.id")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Valid UserChangePasswordDto dto) {
        service.updatePasswordById(id, dto.getCurrentPassword(), dto.getNewPassword(), dto.getConfirmPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restricted-info")
    public ResponseEntity<User> getPrincipalInformations(@AuthenticationPrincipal JwtUserDetails details) {
        return ResponseEntity.ok(service.findById(details.getId()));
        // o @AuthenticationPrincipal é utilizado para retornar um objeto
        // que implementa a interface UserDetails (representa o usuário autenticado no sistema)
    }

}
