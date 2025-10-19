package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.exception.DataNotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDto save(NewUserRequest newUserRequest) {
        User user = userRepository.save(userMapper.toUser(newUserRequest));
        return userMapper.toUserDto(user);
    }

    @Override
    public void delete(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("User with id=" + userId + "was not found"));
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, int from, int size) {
        if (ids == null || ids.isEmpty()) {
            return userRepository.findAll(PageRequest.of(from / size, size))
                    .stream()
                    .map(userMapper::toUserDto)
                    .toList();
        } else {
            return userRepository.findByIdIn(ids, PageRequest.of(from / size, size))
                    .stream()
                    .map(userMapper::toUserDto)
                    .toList();
        }
    }
}
