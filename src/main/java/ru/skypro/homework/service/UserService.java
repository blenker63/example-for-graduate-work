package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.model.User;

@Service
public interface UserService {

    UpdateUserDto update(UpdateUserDto updateUserDto, Authentication authentication);

    NewPasswordDto setPassword(NewPasswordDto newPasswordDto, Authentication authentication);

    UserDto getUserDto(Authentication authentication);

    void updateImage(MultipartFile image, Authentication authentication, String userName);

    User findUserByUsername(Authentication authentication);

    byte[] getUserImage(String filename);

}
