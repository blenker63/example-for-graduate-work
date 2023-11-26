package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.model.Comment;

import java.util.List;

public interface CommentsService {
    CommentDto addComment(CreateOrUpdateCommentDto updateCommentDto, int adId, Authentication authentication);
    List<CommentsDto> getComments(Long AdId, Authentication authentication);

    void removeComment(Long adId, Long commentId, Authentication authentication);
    CreateOrUpdateCommentDto updateComment(CreateOrUpdateCommentDto updateCommentDto, Long adId, Long commentId, Authentication authentication);

}
