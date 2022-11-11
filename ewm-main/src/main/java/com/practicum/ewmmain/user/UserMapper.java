package com.practicum.ewmmain.user;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper
public interface UserMapper {
    User toUserEntity(NewUserRequest newUserRequest);
    @Named("toUserDto")
    UserDto toUserDto(User user);
}
