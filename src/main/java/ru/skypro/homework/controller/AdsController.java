package ru.skypro.homework.controller;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.skypro.homework.dto.*;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;

import javax.servlet.UnavailableException;
import javax.validation.Valid;

@Slf4j
@CrossOrigin(value = "http://localhost:3000")
@Tag(name = "Объявления", description = "контроллер для работы с объявлениями")
@RestController
@JsonIgnoreProperties(ignoreUnknown = true)
//@AllArgsConstructor
//@RequiredArgsConstructor
@RequestMapping("/ads")
public class AdsController {
    private final AdService adService;
    private final CommentsService commentsService;

    public AdsController(AdService adService, CommentsService commentsService) {
        this.adService = adService;
        this.commentsService = commentsService;
    }


    @Operation(
            summary = "Получение всех объявлений",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = AdsDto.class)
                                    )
                            }
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
                            description = "Created",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = AdDto.class)
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = " Unauthorized"
                    )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdDto> addAd(@RequestPart @Valid CreateOrUpdateAdDto properties,
                                       Authentication authentication,
                                       @RequestParam MultipartFile image) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        return ResponseEntity.ok(adService.addAds(properties, image, authentication, userName));
    }

    @Operation(
            summary = "Получение информации об объявлении",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = ExtendedAdDto.class)
                                    )
                            }
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

    @DeleteMapping("/{id}")
    public ResponseEntity<AdDto> removeAd(@PathVariable int id,
                                          Authentication authentication) throws UnavailableException {
        adService.removeAd(id, authentication);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Обновление информации об объявлении",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = AdDto.class)
                                    )
                            }
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
    public ResponseEntity<CreateOrUpdateAdDto> updateAds(@RequestBody @Valid CreateOrUpdateAdDto properties,
//    public ResponseEntity<CreateOrUpdateAdDto> updateAds(@RequestBody @Valid CreateOrUpdateAdDto createOrUpdateAdDto,
                                                         Authentication authentication,
                                                         @PathVariable int id) throws UnavailableException {
        return ResponseEntity.ok(adService.updateAds(properties, authentication, id));
//        return ResponseEntity.ok(adService.updateAds(createOrUpdateAdDto, authentication, id));
    }

    @Operation(
            summary = "Получение объявлений авторизованного пользователя",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = AdsDto.class)
                                    )
                            }
                    )
            }
    )
    @GetMapping("/me")
    public ResponseEntity<AdsDto> getAdsMe(Authentication authentication) {
        return ResponseEntity.ok(adService.getAdsMe(authentication));
    }

    @Operation(
            summary = "Получение комментариев объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CommentsDto.class)
                                    )
                            }
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
            summary = "Обновление картинки объявления",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = {
                                    @Content(
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = {
                                    @Content(
                                    )
                            }
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = {
                                    @Content(
                                    )
                            }
                    )
            }
    )
    @PatchMapping(value = "{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateImage(@PathVariable int id,
                                              Authentication authentication,
                                              @RequestParam MultipartFile image) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
//        adService.uploadImage(id, authentication, image);
        adService.uploadImage(id, authentication, image, userName );
        return ResponseEntity.ok().build();
    }


    @GetMapping(value = "/get/{filename}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_GIF_VALUE, "image/*"})
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) {
        return ResponseEntity.ok().body(adService.getAdImage(filename));
    }


    @Operation(
            summary = "Добавление комментария к объявлению",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CommentDto.class)
                                    )
                            }
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
//    @PostMapping(value = "/{id}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @PostMapping( "/{id}/comments")
    public ResponseEntity<CommentDto> addComment(Authentication authentication,
//                                                 @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "текст комментария")
//                                                 @Valid CreateOrUpdateCommentDto createOrUpdateCommentDto,
                                                 @RequestBody CreateOrUpdateCommentDto createOrUpdateCommentDto,
                                                 @PathVariable int id) {
//    @PostMapping("/{id}/comments")
//    public ResponseEntity<CommentDto> addComment(@RequestBody CreateOrUpdateCommentDto createOrUpdateCommentDto,
//                                                 @PathVariable int id,
//                                                 Authentication authentication) {
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
                            description = "OK",
                            content = {
                                    @Content(
                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            schema = @Schema(implementation = CommentDto.class)
                                    )
                            }
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
//public ResponseEntity<CreateOrUpdateCommentDto> updateComment(@RequestBody CreateOrUpdateCommentDto updateCommentDto,
//                                                              @PathVariable int adId,
//                                                              @PathVariable int commentId,
//                                                              Authentication authentication) {
    public ResponseEntity<CreateOrUpdateCommentDto> updateComment(Authentication authentication,
                                                                  @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "текст комментария")
                                                                  @Valid CreateOrUpdateCommentDto createOrUpdateCommentDto,
                                                                  @PathVariable int id,
                                                                  @PathVariable int commentId) {

        return ResponseEntity.ok(commentsService.updateComment(createOrUpdateCommentDto, id, commentId, authentication));
    }
//    @Configuration
//    class WebMvcConfig implements WebMvcConfigurer {
//        @Override
//        public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
//            configurer.defaultContentType(MediaType.APPLICATION_OCTET_STREAM);
//        }
//    }

}