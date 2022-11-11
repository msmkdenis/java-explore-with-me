package ru.practicum.ewmmain.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminUsersController {

    private final AdminUserService adminUserService;

    @PostMapping
    public UserDto addUser(
            @RequestBody @Valid NewUserRequest newUserRequest
    ) {
        log.info("adminUsersController POST addUser получен NewUserRequest: {}", newUserRequest);
        return adminUserService.addUser(newUserRequest);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("adminUsersController DELETE deleteUser получен userId: {}", userId);
        adminUserService.deleteUser(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(
            @RequestParam List<Long> ids,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.info("adminUsersController GET getUsers ids: {}", ids);
        return adminUserService.getUsers(ids, from, size);
    }
}
