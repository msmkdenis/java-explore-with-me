package com.practicum.ewmmain.user;

import com.practicum.ewmmain.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserServiceImpl implements AdminUserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        User newUser = userMapper.toUserEntity(newUserRequest);
        log.info("newUser after mapping: {}", newUser);

        newUser = userRepository.save(newUser);
        log.info("newUser after save: {}", newUser);

        return userMapper.toUserDto(newUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        return userRepository.findUsersByIds(ids, PageRequest.of(getPageNumber(from, size), size))
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long id) {
        User user = checkUser(id);
        userRepository.delete(user);
    }

    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%d не найден!", id))
        );
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
