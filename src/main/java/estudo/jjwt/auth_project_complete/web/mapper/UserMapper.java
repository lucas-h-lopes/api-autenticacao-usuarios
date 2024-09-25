package estudo.jjwt.auth_project_complete.web.mapper;

import estudo.jjwt.auth_project_complete.entity.User;
import estudo.jjwt.auth_project_complete.web.dto.UserCreateDto;
import estudo.jjwt.auth_project_complete.web.dto.UserResponseDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.util.List;

public class UserMapper {

    public static User toUser(UserCreateDto dto){
        return new ModelMapper().map(dto, User.class);
    }

    public static UserResponseDto toResponseDto(User user){
        String role = user.getRole().name();
        PropertyMap<User, UserResponseDto> props = new PropertyMap<User, UserResponseDto>() {
            @Override
            protected void configure() {
                map().setRole(role);
            }
        };
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(props);
        return mapper.map(user, UserResponseDto.class);
    }

    public static List<UserResponseDto> toListResponseDto(List<User> users){
        return users.stream().map(UserMapper::toResponseDto).toList();
    }
}
