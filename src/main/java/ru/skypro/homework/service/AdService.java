package ru.skypro.homework.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;

import javax.servlet.UnavailableException;
import java.util.List;

@Service
public interface AdService {
    AdDto addAds(CreateOrUpdateAdDto createOrUpdateAdDto,  MultipartFile image,
                               Authentication authentication,  String userName);
    CreateOrUpdateAdDto updateAds(CreateOrUpdateAdDto createOrUpdateAdDto, Authentication authentication, int pk) throws UnavailableException;
    AdsDto getAllAds();
    ExtendedAdDto getAds(int pk);
    AdsDto getAdsMe(Authentication authentication);
    void removeAd(int pk, Authentication authentication) throws UnavailableException;
    void uploadImage(int id, Authentication authentication, MultipartFile image, String userName);

    byte[] getAdImage(String filename);

}
