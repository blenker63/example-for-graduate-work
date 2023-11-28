package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;

import javax.servlet.UnavailableException;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@Tag(name = "Объявления", description = "контроллер для работы с объявлениями")
@RestController
@AllArgsConstructor

@RequestMapping("/ads")
public class AdsController {
    private final AdService adService;
    private final CommentsService commentsService;

    //public AdsController(AdService adService) {
//        this.adService = adService;
//    }


    @Operation(
            summary = "Получение всех объявлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<AdsDto> getAllAds() {
        return ResponseEntity.ok(adService.getAllAds());

    }

    @Operation(
            summary = "Добавление объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Created"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    )
            }
    )
    @PostMapping()
    public ResponseEntity<CreateOrUpdateAdDto> addAd(@RequestBody CreateOrUpdateAdDto createOrUpdateAdDto,
                                                     Authentication authentication) {
        return ResponseEntity.ok(adService.addAds(createOrUpdateAdDto, authentication));
    }

    @Operation(
            summary = "Получение информации об объявлении",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ExtendedAdDto> getAds(@PathVariable int id) {
        return new ResponseEntity<>(adService.getAds(id), HttpStatus.OK);
    }

    @Operation(
            summary = "Удаление объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "No Content"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )

    @DeleteMapping ("/remove/{id}")
    public ResponseEntity<AdDto> removeAd(@PathVariable int id,
                                          Authentication authentication) throws UnavailableException {
        adService.removeAd(id,authentication);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Обновление информации об объявлении",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<CreateOrUpdateAdDto> updateAds(@RequestBody CreateOrUpdateAdDto createOrUpdateAdDto,
                                                         Authentication authentication,
                                                         @PathVariable int id) throws UnavailableException {
        return ResponseEntity.ok(adService.updateAds(createOrUpdateAdDto, authentication, id));
    }

    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<AdsDto> getAdsMe(Authentication authentication) {
        return ResponseEntity.ok(adService.getAdsMe(authentication));
    }

    @Operation(
            summary = "Обновление картинки объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @PatchMapping("/{id}/image")
    public ResponseEntity<?> updateImage() {
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Получение комментариев объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @GetMapping("/{id}/comments")
    public ResponseEntity<CommentsDto> getComments(@PathVariable int id) {
        return ResponseEntity.ok(commentsService.getComments(id));
    }

    @Operation(
            summary = "Добавление комментария к объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> addComment(@RequestBody CreateOrUpdateCommentDto createOrUpdateCommentDto,
                                                 @PathVariable int id,
                                                 Authentication authentication) {
        return ResponseEntity.ok(commentsService.addComment(createOrUpdateCommentDto, id, authentication));
    }

    @Operation(
            summary = "Удаление комментария",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @DeleteMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable int adId,
                                              @PathVariable int commentId,
                                              Authentication authentication) {
        commentsService.removeComment(adId, commentId, authentication);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Обновление комментария",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found"
                    )
            }
    )
    @PatchMapping("/{adId}/comments/{commentId}")
    public ResponseEntity<CreateOrUpdateCommentDto> updateComment(@RequestBody CreateOrUpdateCommentDto updateCommentDto,
                                                                  @PathVariable int adId,
                                                                  @PathVariable int commentId,
                                                                  Authentication authentication) {
        return ResponseEntity.ok(commentsService.updateComment(updateCommentDto, adId, commentId, authentication));
    }

}
