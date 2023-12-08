package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.dto.UserDto;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.PasswordChangeException;
import ru.skypro.homework.mapper.NewPasswordMapper;
import ru.skypro.homework.mapper.UpdateUserMapper;
import ru.skypro.homework.mapper.UserMapper;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.security.MyUserPrincipal;
import ru.skypro.homework.service.UserService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static liquibase.repackaged.net.sf.jsqlparser.parser.feature.Feature.comment;

/**
 * Класс реализация интерфейса {@link UserService} и {@link UserDetailsService}
 */
@Service
@AllArgsConstructor
@EqualsAndHashCode
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;



    /**
     * Редактирование данных пользователя
     * {@link UpdateUserMapper#toModel(UpdateUserDto, User)}
     *
     * @return {@link UserRepository#save(Object)},
     */
    @Override
    public UpdateUserDto update(UpdateUserDto updateUserDto, Authentication authentication) {
        User user = findUserByUsername(authentication);
        UpdateUserMapper.INSTANCE.toModel(updateUserDto, user);
        userRepository.save(user);
        log.info("пользователь обновлен");
        return UpdateUserMapper.INSTANCE.toDTO(user);

    }

    /**
     * Изменение пароля пользователя
     * {@link PasswordEncoder#encode(CharSequence)}
     * {@link NewPasswordMapper#toDto(User)},
     *
     * @return {@link UserRepository#save(Object)},
     * @throws PasswordChangeException пароль не изменен
     */
    @Override
    public NewPasswordDto setPassword(NewPasswordDto newPasswordDto, Authentication authentication) {
        User user = findUserByUsername(authentication);
        String currentPassword = user.getPassword();
        if (encoder.matches(newPasswordDto.getCurrentPassword(), currentPassword)) {
            user.setPassword(encoder.encode(newPasswordDto.getNewPassword()));
            userRepository.save(user);
            log.info("пароль успешно обновлен");
            NewPasswordMapper.INSTANCE.toDto(user);
            return newPasswordDto;
        } else {
            throw new PasswordChangeException("ошибка изменения пароля");
        }
    }

    /**
     * Предоставление информации о зарегистрированном пользователе
     *
     * @return {@link UserMapper#toDto(User)},
     */
    @Override
    public UserDto getUserDto(Authentication authentication) {
        User user = findUserByUsername(authentication);
        log.info("пользователь найден");
        return UserMapper.INSTANCE.toDto(user);
    }

    /**
     * Обновление аватарки пользователя
     * {@link User#setUserImage(String)}





     */
    @Override
    public void updateImage(MultipartFile image, Authentication authentication, String userName) {
        User user = findUserByUsername(authentication);
        String dir = System.getProperty("user.dir") + "/" + "avatars";
        try {
            Files.createDirectories(Path.of(dir));
            String fileName = String.format("avatar%s.%s", user.getEmail(),
                    StringUtils.getFilenameExtension(image.getOriginalFilename()));
            image.transferTo(new File(dir + "/" + fileName));
            user.setUserImage("/users/get/" + fileName);
            log.info("изображение " + fileName + " для аватара пользователя, сохранено на сервере", image);
        } catch (IOException e) {
            log.error("произошла ошибка при попытке сохранить изображение " + image.getOriginalFilename() + ", для аватара пользователя " + userName + ", на сервер", image);
            throw new RuntimeException(e);
        }
        userRepository.save(user);
    }

    /**
     * Проверка авторизации пользователя в базе
     * {@link UserRepository#findByUserName(String)}
     *
     * @return {@link MyUserPrincipal}
     * @throws UsernameNotFoundException пользователь не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь '%s' не найден", username)));
        return new MyUserPrincipal(user);
    }

    /**
     * Проверка авторизованного пользователя в базе
     *
     * @return {@link UserRepository#findByUserName(String)}
     * @throws UsernameNotFoundException пользователь не найден
     */
    public User findUserByUsername(Authentication authentication) {
        return userRepository.findByUserName(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("Пользователь '%s' не найден", authentication.getName())));

    }
    @Override
    public byte[] getUserImage(String filename) {
        try {
            return Files.readAllBytes(Paths.get(System.getProperty("user.dir")
                    + "/"
                    + "avatars"
                    + "/"
                    + filename));
        } catch (IOException e) {
            log.error("ошибка в названии image Аватара" + filename);
            throw new RuntimeException(e);
        }
    }
    /**
     * Проверка прав для изменения, удаления
     *
     * @param authentication аутентификация
     *                       <p>
     *                       {@link CommentsRepository#findByPk(int)} поиск комментария
     * @throws CommentNotFoundException комментарий не найден
     */
    public boolean checkUserRole(String  currentAuthor, Authentication authentication) {
        User user = findUserByUsername(authentication);
        return currentAuthor.equals(authentication.getName()) || user.getRole() == Role.ADMIN;
    }
}
