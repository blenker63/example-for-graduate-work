package ru.skypro.homework.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

import java.util.List;

@Mapper(uses = AdsMapper.class)
public interface AdsMapper {
    AdsMapper INSTANCE = Mappers.getMapper(AdsMapper.class);
    @Mapping(target = "author", source = "user.id")
    AdDto toDtoAd(Ad ad);

    @Mapping(target = "author", source = "user.id")
    List<AdDto> toDto(List<Ad> adMeList);
}
