package ru.skypro.homework.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.CreateOrUpdateAdDto;
import ru.skypro.homework.dto.ExtendedAdDto;
import ru.skypro.homework.mapper.*;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;

import java.util.List;


@Service
//@Data
@AllArgsConstructor
@Slf4j
//@RequiredArgsConstructor

public class AdServiceImpl implements AdService {
    private AdRepository adRepository;
    private final UserRepository userRepository;
    private final UserServiceImpl userServiceImpl;
//    private final ExtendedAdMapper extendedAdMapper;
//    private final AdMapper adMapper;
//    private final AdsMapper adsMapper;
//    private final User user;


    @Override
    public CreateOrUpdateAdDto addAds(CreateOrUpdateAdDto createOrUpdateAdDto, Authentication authentication) {
        User user = userServiceImpl.findUserByUsername(authentication);
        Ad ad = CreateOrUpdateAdMapper.INSTANCE.toModel(createOrUpdateAdDto);
        ad.setUser(user);
        adRepository.save(ad);
        return CreateOrUpdateAdMapper.INSTANCE.toDto(ad, user);
    }

    @Override
    public AdsDto getAllAds() {
        List<Ad> adList = adRepository.findAll();
        AdsDto adsDto = new AdsDto();
        adsDto.setCount(adList.size());
        adsDto.setResults(AdsMapper.INSTANCE.toDto(adList));
        return adsDto;
    }

@Override
    public ExtendedAdDto getAds(int pk){
    Ad ad = adRepository.findByPk(pk);
    User user = userRepository.findById(adRepository.findByPk(pk).getUser());
        return ExtendedAdMapper.INSTANCE.toDto(ad, user);
}
@Override
    public void removeAd(int pk) {
        adRepository.deleteById(pk);
}
    @Override
    public CreateOrUpdateAdDto updateAds(CreateOrUpdateAdDto createOrUpdateAdDto, Authentication authentication, int pk) {
        User user = userServiceImpl.findUserByUsername(authentication);
        Ad ad = CreateOrUpdateAdMapper.INSTANCE.toModel(createOrUpdateAdDto);
        Ad newAd = adRepository.getReferenceById(pk);
        newAd.setTitle(ad.getTitle());
        newAd.setPrice(ad.getPrice());
        newAd.setDescription(ad.getDescription());
        adRepository.save(newAd);
        return CreateOrUpdateAdMapper.INSTANCE.toDto(newAd, user);
    }
    @Override
    public AdsDto getAdsMe(Authentication authentication) {
        User user = userServiceImpl.findUserByUsername(authentication);
        List<Ad> adMeList = adRepository.findAdByUser(user);
        System.out.println(adMeList);
        AdsDto adsDto = new AdsDto();
        adsDto.setCount(adMeList.size());
        adsDto.setResults(AdsMapper.INSTANCE.toDto(adMeList));
        System.out.println(adsDto);
        return adsDto;
    }


}
