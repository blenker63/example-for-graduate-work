package ru.skypro.homework.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.NewPasswordDto;
import ru.skypro.homework.dto.RegisterDto;
import ru.skypro.homework.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
uses = {NewPasswordMapper.class})

public interface NewPasswordMapper {
    NewPasswordMapper INSTANCE = Mappers.getMapper(NewPasswordMapper.class);
    @Mapping(source = "user.password", target = "currentPassword")
    NewPasswordDto toDto(User user);

    @Mapping(source = "newPasswordDto.newPassword", target = "password")
    User toModel(NewPasswordDto newPasswordDto);
    @InheritConfiguration
    void updateModel(NewPasswordDto newPasswordDto, @MappingTarget User user);

}
