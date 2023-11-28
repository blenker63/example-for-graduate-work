package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.CommentNotFoundException;
import ru.skypro.homework.exception.NoRightsException;
import ru.skypro.homework.mapper.CommentMapper;
import ru.skypro.homework.mapper.CommentsMapper;
import ru.skypro.homework.mapper.CreateOrUpdateAdMapper;
import ru.skypro.homework.mapper.CreateOrUpdateCommentMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.CommentsRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;
import ru.skypro.homework.service.UserService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class CommentsServiceImpl implements CommentsService {
    private final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    UserService userService;
    AdService adService;
    AdRepository adRepository;
    CommentsRepository commentsRepository;

    /**
     * Метод добавления комментария к объявлению
     *
     * @param pk уникальный идентификатор объявления
     */
    @Override
    public CommentDto addComment(CreateOrUpdateCommentDto updateCommentDto, int pk, Authentication authentication) {
//        Ad ad = adRepository.findByPk(adId).
//                orElseThrow(()-> new AdNotFoundException("объявление не найдено"));
        Ad ad = adRepository.findByPk(pk);
        System.out.println(ad);
        if (ad == null) {
            logger.warn("объявление id =" + pk + " не найдено");
            throw new AdNotFoundException(pk);
        }
        User user = userService.findUserByUsername(authentication);
        Comment comment = new Comment();
        comment.setAd(ad);
        comment.setUser(user);
        comment.setText(updateCommentDto.getText());
        comment.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
        commentsRepository.save(comment);
        log.info("комментарий успешно добален");

        return CommentMapper.INSTANCE.toDto(comment, user);
    }

    /**
     * Метод получения комментариев по id объявления
     *
     * @param pk уникальный идентификатор объявления
     */
    @Override
    public CommentsDto getComments(int pk) {
//        Ad ad = adRepository.findByPk(AdId).
//                orElseThrow(()-> new AdNotFoundException("объявление не найдено"));
        Ad ad = adRepository.findByPk(pk);
        System.out.println(ad);
        if (ad == null) {
            logger.warn("объявление id =" + pk + " не найдено");
            throw new AdNotFoundException(pk);
        }
        List<Comment> comments = commentsRepository.findAllByAd_Pk(ad.getPk());
        CommentsDto commentsDto = new CommentsDto();
        commentsDto.setCount(comments.size());
        commentsDto.setResults(CommentsMapper.INSTANCE.toDTO(comments));
        log.info("комментарии получены");
        return commentsDto;
    }

    /**
     * Метод удаления конкретного комментария авторизированного юзера по ID объявления
     *
     * @param adId      уникальный идентификатор объявления
     * @param commentId уникальный идентификатор комментария
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

    private boolean checkUserRole(int commentId, Authentication authentication) {
        User user = userService.findUserByUsername(authentication);
        Comment comment = commentsRepository.findById(commentId).
                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
        String currentAuthor = comment.getUser().getUserName();
        return !currentAuthor.equals(authentication.getName()) || user.getRole() != Role.ADMIN;

    }
}


//@Service
//@AllArgsConstructor
//@Slf4j
//public class CommentsServiceImpl implements CommentsService {
//    private final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
//    UserService userService;
//    AdService adService;
//    AdRepository adRepository;
//    CommentsRepository commentsRepository;
//
//
//    /**
//     * Метод добавления комментария к объявлению
//     *
//     * @param pk уникальный идентификатор объявления
//     * @return CommentMapper.INSTANCE.toDto
//     */
//    @Override
////    public CommentDto addComment(CreateOrUpdateCommentDto updateCommentDto, int pk, Authentication authentication) {
//    public CommentDto addComment(CreateOrUpdateCommentDto createOrUpdateCommentDto, int pk, Authentication authentication) {
////        Ad ad = adRepository.findByPk(pk)
////                orElseThrow(() -> new AdNotFoundException(pk));
//        System.out.println("что то не то");
//        User user = userService.findUserByUsername(authentication);
//        System.out.println(user);
//        Ad ad = adRepository.findByPk(pk);
//        System.out.println(ad);
//        if (ad == null) {
//            logger.warn("объявление id =" + pk + " не найдено");
//            throw new AdNotFoundException(pk);
//        }
//        System.out.println("текст -  " + createOrUpdateCommentDto.getText());
////        Comment comment = CreateOrUpdateCommentMapper.INSTANCE.toModel(createOrUpdateCommentDto);
//        Comment newComment = new Comment();
//        newComment.setAd(ad);
//        newComment.setUser(user);
////        newComment.setText(createOrUpdateCommentDto.getText());
//        newComment.setText(CreateOrUpdateCommentMapper.INSTANCE.toModel(createOrUpdateCommentDto).getText());
//        newComment.setCreatedAt(LocalDateTime.now().toInstant(ZoneOffset.UTC).toEpochMilli());
//        commentsRepository.save(newComment);
//        log.info("комментарий успешно добавлен");
//        System.out.println("новый коммент -  " + newComment);
//        System.out.println(user);
////        return CreateOrUpdateCommentMapper.INSTANCE.toDto(comment, user);
//        return CommentMapper.INSTANCE.toDto(newComment, user);
//    }
//
//    /**
//     * Метод получения комментариев по id объявления
//     *
//     * @param adId уникальный идентификатор объявления
//     */
//    @Override
//    public CommentsDto getComments(int adId) {
//        Ad ad = adRepository.findById(adId).
//                orElseThrow(() -> new AdNotFoundException(adId));
////                orElseThrow(()-> new AdNotFoundException("объявление не найдено"));
//        List<Comment> comments = commentsRepository.findAllByAd_Pk(ad.getPk());
//        CommentsDto commentsDto = new CommentsDto();
//        commentsDto.setCount(comments.size());
//        commentsDto.setResults(CommentsMapper.INSTANCE.toDTO(comments));
//        log.info("комментарии получены");
//        return commentsDto;
//    }
//
//    /**
//     * Метод удаления конкретного комментария авторизированного юзера по ID объявления
//     *
//     * @param adId      уникальный идентификатор объявления
//     * @param commentId уникальный идентификатор комментария
//     */
//    @Override
//    public void removeComment(int adId, int commentId, Authentication authentication) {
//        if (checkUserRole(commentId, authentication)) {
//            throw new NoRightsException("нет прав для удаления");
//        }
//        Comment comment = commentsRepository.findByAd_PkAndPk(adId, commentId).
//                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
//        commentsRepository.delete(comment);
//        log.info("комментарий успешно удален");
//
//    }
//
//    /**
//     * Метод редактирования комментария по ID объявления
//     *
//     * @param pk        уникальный идентификатор объявления
//     * @param commentId уникальный идентификатор комментария
//     */
//    @Override
//    public CreateOrUpdateCommentDto updateComment(CreateOrUpdateCommentDto updateCommentDto, Authentication authentication,
//                                                  int commentId, int pk) {
//        User user = userService.findUserByUsername(authentication);
//        if (checkUserRole(commentId, authentication)) {
//            throw new NoRightsException("нет прав для редактирования");
//        }
//        Comment comment = commentsRepository.findByAd_PkAndPk(pk, commentId).
//                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
//        comment.setText(updateCommentDto.getText());
//        commentsRepository.save(comment);
//        return CreateOrUpdateCommentMapper.INSTANCE.toDto(comment, user);
//    }
//
//    private boolean checkUserRole(int commentId, Authentication authentication) {
//        User user = userService.findUserByUsername(authentication);
//        Comment comment = commentsRepository.findById(commentId).
//                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
//        String currentAuthor = comment.getUser().getUserName();
//        return !currentAuthor.equals(authentication.getName()) || user.getRole() != Role.ADMIN;
//
//    }
//
//    /**
//     * Метод удаления всех комментариев по ID объявления
//     *
//     * @param pk уникальный идентификатор объявления
//     */
//
//    public void deleteAllCommentByPk(int pk) {
//        List<Comment> allCommentByPk = commentsRepository.findByAd_Pk(pk);
//        commentsRepository.deleteAll(allCommentByPk);
//        logger.warn("у объявления id=" + pk + " успешно удалены все комментарии");
//    }
//
//    @Override
//    public CreateOrUpdateCommentDto updateComment(CreateOrUpdateCommentDto updateCommentDto, int adId,
//                                                  int commentId, Authentication authentication) {
//        if (checkUserRole(commentId, authentication)) {
//            throw new NoRightsException("нет прав для редактирования");
//        }
//        Comment comment = commentsRepository.findByAd_PkAndPk(adId, commentId).
//                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
//        comment.setText(updateCommentDto.getText());
//        commentsRepository.save(comment);
//        return CreateOrUpdateCommentMapper.INSTANCE.toDto(comment);
//    }
//
//    private boolean checkUserRole(int commentId, Authentication authentication) {
//        User user = userService.findUserByUsername(authentication);
//        Comment comment = commentsRepository.findById(commentId).
//                orElseThrow(() -> new CommentNotFoundException("Комментарий не найден"));
//        String currentAuthor = comment.getUser().getUserName();
//        return !currentAuthor.equals(authentication.getName()) || user.getRole() != Role.ADMIN;
//
//    }
//}
