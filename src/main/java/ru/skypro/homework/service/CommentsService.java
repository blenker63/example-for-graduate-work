package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Comment;

public interface CommentsService {
   CommentDto addComment(CreateOrUpdateCommentDto createOrUpdateCommentDto, int pk, Authentication authentication);

    CommentsDto getComments(int pk);

    void removeComment(int adId, int commentId, Authentication authentication);
    CreateOrUpdateCommentDto updateComment(CreateOrUpdateCommentDto updateCommentDto, int adId,
                                           int commentId,  Authentication authentication);
    void deleteAllCommentByPk(int pk);

}
