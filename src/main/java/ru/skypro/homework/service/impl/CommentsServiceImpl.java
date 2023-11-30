package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.NoRightsException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.CommentsMapper;
import ru.skypro.homework.mapper.CreateOrUpdateAdMapper;
import ru.skypro.homework.mapper.CreateOrUpdateCommentMapper;
import ru.skypro.homework.mapper.UpdateUserMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;
import ru.skypro.homework.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Класс реализация интерфейса {@link CommentsService}
 */
@Service
@AllArgsConstructor
@Slf4j
public class CommentsServiceImpl implements CommentsService {
    private final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    private final UserService userService;
    //    private final AdService adService;
    private final AdRepository adRepository;
    private final CommentsRepository commentsRepository;


    /**
     * Добавление комментария
     * к объявлению
     * по первичному
     * ключу объявления
     *
     * @param updateCommentDto сущность
     *                         для добавления
     * @param adId             идентификатор
     *                         объявления
     * @param authentication   аутентификация
     *                         <p>
     *                         <p>
     *                         {
     * @return {
     * @throws AdNotFoundException объявление
     *                             не найдено
     * @link CommentsRepository#save(Object)
     * }
     * <p>
     * сохранение в
     * репозитории
     * @link CommentMapper#toDto(Comment, User)
     * }
     */

    @Override
    public CommentDto addComment(CreateOrUpdateCommentDto updateCommentDto, int adId, Authentication authentication) {
        Ad ad = adRepository.findByPk(adId).
                orElseThrow(() -> new AdNotFoundException(adId));

        User user = userService.findUserByUsername(authentication);
        Comment textComment = CreateOrUpdateCommentMapper.INSTANCE.toModel(updateCommentDto);
        Comment comment = new Comment();
        comment.setAd(ad);
        comment.setUser(user);
        //comment.setText(String.valueOf(textComment));
        comment.setText(updateCommentDto.getText());
        comment.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        commentsRepository.save(comment);
        log.info("комментарий успешно добавлен");

        return CommentMapper.INSTANCE.toDto(comment, user);
    }


    /**
     * Получение всех комментариев объявления
     *
     * @param adId идентификатор объявления
     *             <p>
     *             {@link CommentsRepository#findAllByAd_Pk(int)} поиск комментария
     * @return {@link CommentDto}
     * @throws AdNotFoundException объявление не найдено
     */
    @Override
    public CommentsDto getComments(int adId) {
        Ad ad = adRepository.findByPk(adId).
                orElseThrow(() -> new AdNotFoundException(adId));

        List<Comment> comments = commentsRepository.findAllByAd_Pk(ad.getPk());
        CommentsDto commentsDto = new CommentsDto();
        commentsDto.setCount(comments.size());
        commentsDto.setResults(CommentsMapper.INSTANCE.toDTO(comments));
        log.info("комментарии получены");
        return commentsDto;
    }


    /**
     * Удаление комментария объявления
     *
     * @param adId      идентификатор объявления
     * @param commentId идентификатор комментария
     *                  <p>
     *                  {@link CommentsRepository#findByAd_PkAndPk(int, int)} поиск комментария
     * @throws NoRightsException        нет прав для удаления
     * @throws CommentNotFoundException комментарий не найден
     */
    @Override
    public void removeComment(int adId, int commentId, Authentication authentication) {
        if (checkUserRole(commentId, authentication)) {
            throw new NoRightsException("нет прав для удаления");
        }
        Comment comment = commentsRepository.findByAd_PkAndPk(adId, commentId).
                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        commentsRepository.delete(comment);
        log.info("комментарий успешно удален");

    }

    /**
     * Обновление комментария
     *
     * @param adId           идентификатор объявления
     * @param commentId      идентификатор комментария
     * @param authentication аутентификация
     *                       <p>
     *                       {@link CommentsRepository#findByAd_PkAndPk(int, int)} поиск комментария
     *                       {@link CommentsRepository#save(Object)} сохранение в репозитории
     * @return {@link CreateOrUpdateCommentMapper#toDto(Comment)}
     * @throws NoRightsException        нет прав для удаления
     * @throws CommentNotFoundException комментарий не найден
     */
    @Override
    public CreateOrUpdateCommentDto updateComment(CreateOrUpdateCommentDto updateCommentDto, int adId,
                                                  int commentId, Authentication authentication) {
        if (checkUserRole(commentId, authentication)) {
            throw new NoRightsException("нет прав для редактирования");
        }
        Comment comment = commentsRepository.findByAd_PkAndPk(adId, commentId).
                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        comment.setText(updateCommentDto.getText());
        commentsRepository.save(comment);
        return CreateOrUpdateCommentMapper.INSTANCE.toDto(comment);
    }

    /**
     * Проверка прав для изменения, удаления
     *
     * @param commentId      идентификатор комментария
     * @param authentication аутентификация
     *                       <p>
     *                       {@link CommentsRepository#findByPk(int)} поиск комментария
     * @throws CommentNotFoundException комментарий не найден
     */
    private boolean checkUserRole(int commentId, Authentication authentication) {
        User user = userService.findUserByUsername(authentication);
        Comment comment = commentsRepository.findByPk(commentId).
                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        String currentAuthor = comment.getUser().getUserName();
        return !currentAuthor.equals(authentication.getName()) || user.getRole() != Role.ADMIN;

    }

    /**
     * Метод удаления всех комментариев объявления
     *
     * @param pk id объявления
     */

    public void deleteAllCommentByPk(int pk) {
        commentsRepository.deleteAll(commentsRepository.findAllByAd_Pk(pk));
        logger.info("удалены все комментарии по объявлению id -" + pk);

    }

}