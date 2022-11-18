package ru.practicum.ewmmain.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewmmain.dto.user.NewUserRequest;
import ru.practicum.ewmmain.dto.user.UserShortDto;
import ru.practicum.ewmmain.entity.User;
import ru.practicum.ewmmain.dto.user.UserDto;

@UtilityClass
public class UserMapper {

    public static User toUserEntity(NewUserRequest newUserRequest) {
        return User.builder()
                .email(newUserRequest.getEmail())
                .name(newUserRequest.getName())
                .build();
    }

    public static UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserShortDto userShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
