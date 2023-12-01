package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.webjars.NotFoundException;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.exception.AdNotFoundException;
import ru.skypro.homework.exception.NoRightsException;
import ru.skypro.homework.exception.UserNotAdFoundException;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;
import ru.skypro.homework.service.UserService;

import javax.servlet.UnavailableException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import static ru.skypro.homework.dto.Role.ADMIN;

@RequiredArgsConstructor
@EqualsAndHashCode
@Service
@Slf4j
public class AdServiceImpl implements AdService {
    private final Logger logger = LoggerFactory.getLogger(AdServiceImpl.class);
    private final AdRepository adRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;
    private final UserService userService;
    private final CommentsService commentsService;

    @Value("${file.path.image}")
    private String filePath;


    /**
     * Метод создает объявление
     *
     * @param createOrUpdateAdDto title,price,description
     *                            //     * @param image               картинка объявления
     *                            //     * @param userEmail           login пользователя
     * @return AdDto
     */
    @Override
    public AdDto addAds(CreateOrUpdateAdDto createOrUpdateAdDto,
                        MultipartFile image,
                        Authentication authentication,
                        String userName) {
        User user = userService.findUserByUsername(authentication);
        Ad ad = CreateOrUpdateAdMapper.INSTANCE.toModel(createOrUpdateAdDto);
        String imageName = uploadImageOnSystem(image, userName);
        ad.setUser(user);
        ad.setAdImage(getUrlImage(imageName));
        adRepository.save(ad);
        logger.info("добавлено новое объявление: " + ad);
        return AdMapper.INSTANCE.toDto(ad, user);
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
        Ad ad = adRepository.findByPk(pk).orElseThrow(() -> new AdNotFoundException(pk));
        User user = userRepository.findById(ad.getUser().getId());
        logger.info("найдено объявление: " + ad, ad);
        return ExtendedAdMapper.INSTANCE.toDto(ad, user);
//        if (ad != null) {
//            User user = userRepository.findById(adRepository.findByPk(pk).getUser().getId());
//            logger.info("найдено объявление: " + ad, ad);
//            return ExtendedAdMapper.INSTANCE.toDto(ad, user);
//        }
//        throw new AdNotFoundException(pk);
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
        User user = userService.findUserByUsername(authentication);
        Ad ad = CreateOrUpdateAdMapper.INSTANCE.toModel(createOrUpdateAdDto);
        Ad newAd = adRepository.getReferenceById(pk);
//        int currentAuthor = newAd.getUser().getId();
        String currentAuthor = newAd.getUser().getUserName();
        if (userService.checkUserRole(currentAuthor, authentication)) {
            newAd.setTitle(ad.getTitle());
            newAd.setPrice(ad.getPrice());
            newAd.setDescription(ad.getDescription());
            logger.info("внесены изменения в объявление id =" + ad.getPk(), ad);
            adRepository.save(newAd);
        } else {

            throw new NoRightsException("нет прав для изменений");
        }
            return CreateOrUpdateAdMapper.INSTANCE.toDto(newAd, user);
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


    /**
     * Метод удаляет объявление(может удалять Admin или создатель объявления)
     *
     * @param pk id объявления
     *           //     * @param userName login пользователя
     */
    @Override
    public void removeAd(int pk, Authentication authentication) throws UnavailableException {
        Ad ad = adRepository.findByPk(pk).orElseThrow(() -> new AdNotFoundException(pk));
            Ad newAd = adRepository.getReferenceById(pk);
            String currentAuthor = newAd.getUser().getUserName();
            if (userService.checkUserRole(currentAuthor, authentication)) {
                if (ad.getAdImage() != null) {
                    try {
                        Files.delete(Path.of(System.getProperty("user.dir") + "/" + filePath
                                + ad.getAdImage().replaceAll("/ads/get", "")));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                commentsService.deleteAllCommentByPk(pk);
                adRepository.delete(ad);
                logger.info("удалено объявление id = " + pk);

            } else {
                throw new NoRightsException("нет прав для удаления");
        }
    }

    /**
     * Метод обновления картинки по объявлению
     *
     * @param pk    уникальный идентификатор объявления
     * @param image файл изображения для объявления
     */
    @Override
    public void uploadImage(int pk, Authentication authentication, MultipartFile image, String userName) {
        User user = userServiceImpl.findUserByUsername(authentication);
        Ad ad = adRepository.findByPk(pk).orElseThrow();
        if (ad == null) {
            logger.warn("не найдено объявление id = " + pk);
            throw new AdNotFoundException(pk);
        }
        if (ad.getAdImage() != null) {
            try {
                Files.delete(Path.of(System.getProperty("user.dir") + "/" + filePath + ad.getAdImage().replaceAll("/ads/get", "")));
            } catch (IOException e) {
                logger.error("произошла ошибка при попытке удаления изображения к объявлению id=" + id, ad);
                throw new RuntimeException(e);
            }
        }
        String imageName = uploadImageOnSystem(image, userName);
        ad.setAdImage(getUrlImage(imageName));
        ad.setUser(user);
        adRepository.save(ad);
        logger.info("у объявления id=" + ad.getPk() + " обновлено изображение", ad);
    }


    /**
     * Метод указывает расширение файла
     *
     * @return String
     */
    private String getExtension(String fileName) {
        return StringUtils.getFilenameExtension(fileName);
    }

    /**
     * Метод создает название файла
     *
     * @param image    картинка объявления
     * @param userName login пользователя
     * @return String
     */
    private String getFileName(String userName, MultipartFile image) {
        return String.format("image%s_%s.%s", userName, UUID.randomUUID(), getExtension(image.getOriginalFilename()));
    }

    /**
     * Метод создает Url файла
     *
     * @param fileName название картинки объявления
     * @return String
     */
    private String getUrlImage(String fileName) {
        return "/ads/get/" + fileName;
    }

    /**
     * Метод загружаем файл
     *
     * @param image файл
     * @return String имя загруженного файла
     */
//    private String uploadImageOnSystem(MultipartFile image, Authentication authentication) {
    private String uploadImageOnSystem(MultipartFile image, String userName) {
//        String userName = userServiceImpl.findUserByUsername(authentication).getUserName();
        String dir = System.getProperty("user.dir") + "/" + filePath;
        String imageName = getFileName(userName, image);
        try {
            Files.createDirectories(Path.of(dir));
            image.transferTo(new File(dir + "/" + imageName));
        } catch (IOException e) {
            logger.error("произошла ошибка при попытке сохранить изображение " + image.getOriginalFilename() + " для объявления на сервер", image);
            throw new RuntimeException(e);
        }
        logger.info("изображение " + imageName + " сохранено на сервере", image);
        return imageName;
    }

    /**
     * Метод получает массив байтов
     *
     * @param filename имя картинки
     * @return byte[]
     */

    @Override
    public byte[] getAdImage(String filename) {
        try {
            return Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/" + filePath + "/" + filename));
        } catch (IOException e) {
            logger.error("ошибка в названии image объявления " + filename);
            throw new RuntimeException(e);
        }
    }

}
