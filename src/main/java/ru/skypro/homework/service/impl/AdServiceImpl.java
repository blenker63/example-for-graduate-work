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
    public List<AdDto> getAllAds(Authentication authentication) {
//        User user = userServiceImpl.findUserByUsername(authentication);
////        List<Ad> adList = adRepository.findAdByUser(user);
//        List<Ad> adList = adRepository.findAll();
//        AdsDto adsDto = new AdsDto();
//        adsDto.setCount(adList.size());
//        adsDto.setResults(adsMapper.INSTANCE.toDto(adList));
//        return adsDto;
//        return null;
        User user = userServiceImpl.findUserByUsername(authentication);
//        List<Ad> adList = adRepository.findAdByUser(user);
        List<Ad> adList = adRepository.findAll();
        AdsDto adsDto = new AdsDto();
        //adsDto.setCount(adList.size());
        //adsDto.setResults(adsMapper.INSTANCE.toDto(adList.size(), adList));
        return AdsMapper.INSTANCE.toDto(adList.size(), adList);
    }

@Override
    public ExtendedAdDto getAds(int pk){
//    Ad ad = AdMapper.INSTANCE.toModel(pk);
    Ad ad = adRepository.findByPk(pk);
//    User userId = ad.getUser(UserMapper.INSTANCE.toModel());
//    User userId = adRepository.findByPk(pk).getUser();
//    User user = userRepository.findById(userId);
    User user = userRepository.findById(adRepository.findByPk(pk).getUser());
//        return extendedAdMapper.INSTANCE.toDto(ad);
        return ExtendedAdMapper.INSTANCE.toDto(ad, user);
}
@Override
    public void removeAd(int pk) {
//        Ad ad = adRepository.findByPk(pk);
        adRepository.deleteById(pk);
//        return adRepository.deleteById(pk);
}

}
