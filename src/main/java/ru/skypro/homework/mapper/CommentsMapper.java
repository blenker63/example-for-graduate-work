package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentsMapper {
    CommentsMapper INSTANCE = Mappers.getMapper(CommentsMapper.class);
    @Mapping(source = "user.id", target = "author")
    @Mapping(source = "user.userImage", target = "authorImage")
    @Mapping(source = "user.firstName", target = "authorFirstName")
    CommentDto toDto(Comment comment);

    @Mapping(source = "ad.countComment", target = "count")
    @Mapping(source = "ad.commentList", target = "results")
    List<CommentDto> toDTO(List<Comment> comments);
}
