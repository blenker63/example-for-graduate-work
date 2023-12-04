package ru.skypro.homework.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.dto.AdsDto;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;
import ru.skypro.homework.service.impl.AdServiceImpl;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdsController.class)
public class AdsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdService adService;
    @MockBean
    private SecurityContextHolder securityContextHolder;
    @MockBean
    private Authentication authentication;

    AdDto adDtoTest1 = new AdDto(1, 1, "image1", 11, "title1");
    AdDto adDtoTest2 = new AdDto(2, 1, "image2", 22, "title2");
    AdDto adDtoTest3 = new AdDto(3, 2, "image3", 33, "title3");

    @Test
    void getAllAdsTest() throws Exception {
        AdsDto adsDtoTest = new AdsDto(3, List.of(adDtoTest1, adDtoTest2, adDtoTest3));
        when(adService.getAllAds()).thenReturn(adsDtoTest);
        mockMvc.perform(MockMvcRequestBuilders.get("/ads"))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.count").value(adsDtoTest.getCount()))
                .andExpect((ResultMatcher) jsonPath("$.results[0].author").value(adDtoTest1.getAuthor()));
        Mockito.verify(adService, new Times(1)).getAllAds();
    }
}
