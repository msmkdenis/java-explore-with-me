package com.practicum.ewmmain.user;

import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public static User toUserEntity(NewUserRequest newUserRequest) {
        return new User(
                null,
                newUserRequest.getName(),
                newUserRequest.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
