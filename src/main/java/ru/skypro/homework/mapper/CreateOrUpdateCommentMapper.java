package ru.skypro.homework.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.dto.UpdateUserDto;
import ru.skypro.homework.model.Comment;
import ru.skypro.homework.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateOrUpdateCommentMapper {
    CreateOrUpdateCommentMapper INSTANCE = Mappers.getMapper(CreateOrUpdateCommentMapper.class);

    @Mapping(target = "text", source = "comment.text")
    CreateOrUpdateCommentDto toDto(Comment comment);
//    CreateOrUpdateCommentDto toDto(Comment comment, User user);

    @Mapping(target = "text", source = "createOrUpdateCommentDto.text")
    Comment toModel(CreateOrUpdateCommentDto createOrUpdateCommentDto);


}
