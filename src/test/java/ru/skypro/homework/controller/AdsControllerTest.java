package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.mapper.AdMapper;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;
import ru.skypro.homework.repository.AdRepository;
import ru.skypro.homework.repository.UserRepository;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;
import ru.skypro.homework.service.UserService;
import ru.skypro.homework.service.impl.AdServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdsController.class)
public class AdsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;
    @MockBean
    private CommentsService commentsService;
    @MockBean
    private SecurityContextHolder securityContextHolder;
    @MockBean
    private Authentication authentication;
    @MockBean
    AdRepository adRepository;
    @MockBean
    UserRepository userRepository;
    @MockBean
    UserService userService;

    AdDto adDtoTest1 = new AdDto(1, 1, "image1", 11, "title1");
    AdDto adDtoTest2 = new AdDto(2, 1, "image2", 22, "title2");
    AdDto adDtoTest3 = new AdDto(3, 2, "image3", 33, "title3");




    @Test
    @WithUserDetails
    void getAllAdsTest() throws Exception {
        AdsDto adsDtoTest = new AdsDto(3, List.of(adDtoTest1, adDtoTest2, adDtoTest3));
        when(adService.getAllAds()).thenReturn(adsDtoTest);
        mockMvc.perform(MockMvcRequestBuilders.get("/ads")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
        Mockito.verify(adService, times(1)).getAllAds();
    }

}
