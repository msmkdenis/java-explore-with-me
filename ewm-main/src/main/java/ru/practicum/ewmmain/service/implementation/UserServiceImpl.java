package ru.practicum.ewmmain.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmmain.dto.mapper.UserMapper;
import ru.practicum.ewmmain.dto.user.NewUserRequest;
import ru.practicum.ewmmain.dto.user.UserDto;
import ru.practicum.ewmmain.entity.User;
import ru.practicum.ewmmain.exception.EntityNotFoundException;
import ru.practicum.ewmmain.repository.UserRepository;
import ru.practicum.ewmmain.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto addUser(NewUserRequest newUserRequest) {
        User newUser = UserMapper.toUserEntity(newUserRequest);
        newUser = userRepository.save(newUser);

        return UserMapper.toUserDto(newUser);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids == null) {
            return userRepository.findAll(PageRequest.of(getPageNumber(from, size), size))
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findUsersByIds(ids, PageRequest.of(getPageNumber(from, size), size))
                    .stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public void deleteUser(long id) {
        User user = checkUser(id);
        userRepository.delete(user);
    }

    private User checkUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("User id=%d не найден!", id)));
    }

    private int getPageNumber(int from, int size) {
        return from / size;
    }
}
