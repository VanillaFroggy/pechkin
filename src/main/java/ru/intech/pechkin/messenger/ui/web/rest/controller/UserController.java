package ru.intech.pechkin.messenger.ui.web.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.intech.pechkin.messenger.infrastructure.service.UserService;
import ru.intech.pechkin.messenger.infrastructure.service.dto.user.UserDto;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.GetPageOfUsersByDepartmentRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.GetPageOfUsersByFieldLikeRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.UpdateUserIconRequest;
import ru.intech.pechkin.messenger.ui.web.rest.dto.user.UpdateUsernameRequest;
import ru.intech.pechkin.messenger.ui.web.rest.mapper.UserRestMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messenger")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserRestMapper mapper;

    @GetMapping("/getUserById/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") UUID id) {
        return new ResponseEntity<>(
                userService.getUserById(id),
                HttpStatus.OK
        );
    }

    @GetMapping("/getUserByUsername/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(
                userService.getUserByUsername(username),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfUsersByUsernameLike")
    public ResponseEntity<Page<UserDto>> getPageOfUsersByUsernameLike(GetPageOfUsersByFieldLikeRequest request) {
        return new ResponseEntity<>(
                userService.getPageOfUsersByUsernameLike(mapper.getPageOfUsersByUsernameLikeRequestToDto(request)),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfUsersByDepartment")
    public ResponseEntity<Page<UserDto>> getPageOfUsersByDepartment(
            GetPageOfUsersByDepartmentRequest request
    ) {
        return new ResponseEntity<>(
                userService.getPageOfUsersByDepartment(
                        mapper.getPageOfUsersByDepartmentRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfUsersByDepartmentLike")
    public ResponseEntity<Page<UserDto>> getPageOfUsersByDepartmentLike(
            GetPageOfUsersByFieldLikeRequest request
    ) {
        return new ResponseEntity<>(
                userService.getPageOfUsersByDepartmentLike(
                        mapper.getPageOfUsersByFieldLikeRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfUsersByFioLike")
    public ResponseEntity<Page<UserDto>> getPageOfUsersByFioLike(
            GetPageOfUsersByFieldLikeRequest request
    ) {
        return new ResponseEntity<>(
                userService.getPageOfUsersByFioLike(
                        mapper.getPageOfUsersByFieldLikeRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/getPageOfUsersByPositionLike")
    public ResponseEntity<Page<UserDto>> getPageOfUsersByPositionLike(
            GetPageOfUsersByFieldLikeRequest request
    ) {
        return new ResponseEntity<>(
                userService.getPageOfUsersByPositionLike(
                        mapper.getPageOfUsersByFieldLikeRequestToDto(request)
                ),
                HttpStatus.OK
        );
    }

    @PutMapping("/updateUsername")
    public ResponseEntity<Void> updateUsername(@RequestBody UpdateUsernameRequest request) {
        userService.updateUsername(mapper.updateUsernameRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/updateUserIcon")
    public ResponseEntity<Void> updateUserIcon(@RequestBody UpdateUserIconRequest request) {
        userService.updateUserIcon(mapper.updateUserIconRequestToDto(request));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/blockUser/{id}")
    public ResponseEntity<Void> blockUser(@PathVariable("id") UUID id) {
        userService.blockUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/unblockUser/{id}")
    public ResponseEntity<Void> unblockUser(@PathVariable("id") UUID id) {
        userService.unblockUser(id);
        return ResponseEntity.noContent().build();
    }
}
