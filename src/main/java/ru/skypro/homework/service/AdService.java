package ru.skypro.homework.service;


import org.springframework.security.core.Authentication;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.model.Ad;

public interface AdService {
//    AdsDto getAllAds ();
//    AdDto addAd();

    CreateOrUpdateAdDto addAd(CreateOrUpdateAdDto createOrUpdateAdDto, Authentication authentication);

//    AdsDto getAllAds(Authentication authentication);
}
