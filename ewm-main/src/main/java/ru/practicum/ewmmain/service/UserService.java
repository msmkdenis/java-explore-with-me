package ru.practicum.ewmmain.service;

import ru.practicum.ewmmain.dto.user.NewUserRequest;
import ru.practicum.ewmmain.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserRequest newUserRequest);

    List<UserDto> getByIds(List<Long> ids, int from, int size);

    void delete(long id);
}
