package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.UserNotAdFoundException;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import javax.servlet.UnavailableException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import static ru.skypro.homework.dto.Role.ADMIN;

@Service
@AllArgsConstructor
@Slf4j

public class AdServiceImpl implements AdService {
    private final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    private AdRepository adRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;


    /**
     * Метод создает объявление
     *
     * @param createOrUpdateAdDto title,price,description
     *                            //     * @param image               картинка объявления
     *                            //     * @param userEmail           login пользователя
     * @return AdDto
     */
    @Override
    public CreateOrUpdateAdDto addAds(CreateOrUpdateAdDto createOrUpdateAdDto, Authentication authentication) {
        User user = userServiceImpl.findUserByUsername(authentication);
        Ad ad = CreateOrUpdateAdMapper.INSTANCE.toModel(createOrUpdateAdDto);
        ad.setUser(user);
        adRepository.save(ad);
        logger.info("добавлено новое объявление: " + ad);
        return CreateOrUpdateAdMapper.INSTANCE.toDto(ad, user);
    }

    /**
     * Метод выводит AdsDto (кол-во объявлений и все объявления)
     *
     * @return AdsDto
     */
    @Override
    public AdsDto getAllAds() {
        List<Ad> adList = adRepository.findAll();
        AdsDto adsDto = new AdsDto();
        adsDto.setCount(adList.size());
        adsDto.setResults(AdsMapper.INSTANCE.toDto(adList));
        logger.warn("выведены все объявления");
        return adsDto;
    }

    /**
     * Метод выдает информацию по объявлению
     *
     * @param pk id объявления
     * @return ExtendedAdDto
     */
    @Override
    public ExtendedAdDto getAds(int pk) {
        Ad ad = adRepository.findByPk(pk);
        if (ad != null) {
            User user = userRepository.findById(adRepository.findByPk(pk).getUser().getId());
            logger.info("найдено объявление: " + ad, ad);
            return ExtendedAdMapper.INSTANCE.toDto(ad, user);
        }
        throw new AdNotFoundException(pk);
    }

    /**
     * Метод удаляет объявление(может удалять Admin или создатель объявления)
     *
     * @param pk id объявления
     *           //     * @param userName login пользователя
     */
    @Override
    public void removeAd(int pk, Authentication authentication) throws UnavailableException {
        User user = userServiceImpl.findUserByUsername(authentication);
        Ad ad = adRepository.findByPk(pk);
        if (ad == null) {
            logger.warn("объявление id =" + pk + " не найдено");
            throw new AdNotFoundException(pk);
        }
        if (user.getRole().equals(ADMIN) || ad.getUser().getId() == user.getId()) {
//        try {
//            Files.delete(Path.of(System.getProperty("user.dir") + "/" + filePath + ad.getImage().replaceAll("/ads/get", "")));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        commentService.deleteAllCommentByPk(pk);
            adRepository.deleteById(pk);
            logger.info("удалено объявление id = " + ad.getPk(), ad);
        } else {
            logger.warn("у пользователя " + user.getId() + " не достаточно прав для удаления объявления id = " + ad.getPk(), ad);
            throw new UnavailableException(user.getFirstName(), user.getId());
        }
//    adRepository.deleteById(pk);
    }

    /**
     * Метод обновляет объявление
     *
     * @param pk                  id объявления
     * @param createOrUpdateAdDto title,price,description
     * @return AdDto
     */

    @Override
    public CreateOrUpdateAdDto updateAds(CreateOrUpdateAdDto createOrUpdateAdDto, Authentication authentication, int pk) throws UnavailableException {
        User user = userServiceImpl.findUserByUsername(authentication);
        Ad ad = CreateOrUpdateAdMapper.INSTANCE.toModel(createOrUpdateAdDto);
        Ad newAd = adRepository.getReferenceById(pk);
        if (ad == null) {
            logger.warn("не найдено объявление id=" + pk);
            throw new AdNotFoundException(pk);
        }
        if (user.getRole().equals(ADMIN) || ad.getUser().getId() == user.getId()) {
            newAd.setTitle(ad.getTitle());
            newAd.setPrice(ad.getPrice());
            newAd.setDescription(ad.getDescription());
            logger.info("внесены изменения в объявление id=" + ad.getPk(), ad);
            adRepository.save(newAd);
            return CreateOrUpdateAdMapper.INSTANCE.toDto(newAd, user);
        } else {
            logger.warn("у пользователя " + user.getId() + " не достаточно прав для удаления объявления id = " + ad.getPk(), ad);
            throw new UnavailableException(user.getFirstName(), user.getId());
        }
    }

    /**
     * Метод выводит AdsDto (кол-во объявлений пользователя и все его объявления)
     *
     * @return AdsDto
     */
    @Override
    public AdsDto getAdsMe(Authentication authentication) {
        User user = userServiceImpl.findUserByUsername(authentication);
        List<Ad> adMeList = adRepository.findAdByUser(user);
            AdsDto adsDto = new AdsDto();
        if (adMeList == null) {
            throw new UserNotAdFoundException(user.getId());
        } else {
            adsDto.setCount(adMeList.size());
            adsDto.setResults(AdsMapper.INSTANCE.toDto(adMeList));
            logger.warn("выведены объявления авторизованного пользователя c id " + user.getId());
        }
            return adsDto;
    }
}
