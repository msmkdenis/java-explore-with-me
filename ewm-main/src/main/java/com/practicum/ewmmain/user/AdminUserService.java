package com.practicum.ewmmain.user;

import java.util.List;

public interface AdminUserService {
    UserDto addUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(long id);
}
