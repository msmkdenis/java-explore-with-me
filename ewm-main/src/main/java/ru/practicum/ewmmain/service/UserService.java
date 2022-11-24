package ru.practicum.ewmmain.service;

import ru.practicum.ewmmain.dto.user.NewUserRequest;
import ru.practicum.ewmmain.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long id);
}
