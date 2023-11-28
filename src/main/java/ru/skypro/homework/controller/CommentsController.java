package ru.skypro.homework.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.CommentDto;
import ru.skypro.homework.dto.CommentsDto;
import ru.skypro.homework.dto.CreateOrUpdateCommentDto;
import ru.skypro.homework.service.AdService;
import ru.skypro.homework.service.CommentsService;

import javax.validation.Valid;

//@Slf4j
//@CrossOrigin(value = "http://localhost:3000")
//@Tag(name = "Комментарии", description = "контроллер для работы с комментариями")
//@RestController
////@RequiredArgsConstructor
//
//@RequestMapping("/ads")
//public class CommentsController {
//    private final CommentsService commentsService;
//
//    public CommentsController(CommentsService commentsService) {
//        this.commentsService = commentsService;
//    }
//
//    @Operation(
//            summary = "Получение комментариев объявления",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "OK"
//                    ),
//                    @ApiResponse(
//                            responseCode = "401",
//                            description = " Unauthorized",
//                            content = {
//                                    @Content(
//                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                                            schema = @Schema(implementation = CommentsDto.class)
//                                    )
//                            }
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "Not found"
//                    )
//            }
//    )
//    @GetMapping("/{id}/comments")
//    public ResponseEntity<CommentsDto> getComments(@PathVariable int idComment) {
//        return ResponseEntity.ok(commentsService.getComments(idComment));
//    }
//
//    @Operation(
//            summary = "Добавление комментария к объявлению",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "OK",
//                            content = {
//                                    @Content(
//                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                                            schema = @Schema(implementation = CommentDto.class)
//                                    )
//                            }
//                    ),
//                    @ApiResponse(
//                            responseCode = "401",
//                            description = " Unauthorized"
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "Not found"
//                    )
//            }
//    )
//    @PostMapping("{id}/comments")
//    public CommentDto addComment(Authentication authentication,
//                                 @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "текст комментария")
//                                 @Valid CreateOrUpdateCommentDto createOrUpdateCommentDto,
//                                 @PathVariable int id) {
//        return commentsService.addComment(createOrUpdateCommentDto, id, authentication);
//    }
//
//    @Operation(
//            summary = "Удаление комментария",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "OK"
//                    ),
//                    @ApiResponse(
//                            responseCode = "401",
//                            description = " Unauthorized"
//                    ),
//                    @ApiResponse(
//                            responseCode = "403",
//                            description = "Forbidden"
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "Not found"
//                    )
//            }
//    )
//    @DeleteMapping("/{adId}/comments/{commentId}")
//    public ResponseEntity<Void> removeComment(Authentication authentication,
//                                              @PathVariable @Parameter(description = "уникальный идентификатор объявления") int pk,
//                                              @PathVariable @Parameter(description = "уникальный идентификатор комментария") int commentId) {
//        commentsService.removeComment(pk, commentId, authentication);
//        return ResponseEntity.ok().build();
//    }
//
//    @Operation(
//            summary = "Обновление комментария",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "OK",
//                            content = {
//                                    @Content(
//                                            mediaType = MediaType.APPLICATION_JSON_VALUE,
//                                            schema = @Schema(implementation = CommentDto.class)
//                                    )
//                            }
//                    ),
//                    @ApiResponse(
//                            responseCode = "401",
//                            description = " Unauthorized"
//                    ),
//                    @ApiResponse(
//                            responseCode = "403",
//                            description = "Forbidden"
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "Not found"
//                    )
//            }
//    )
//    @PatchMapping("/{adId}/comments/{commentId}")
//    public CreateOrUpdateCommentDto updateComment(Authentication authentication,
//                                                  @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "текст комментария")
//                                                  @Valid CreateOrUpdateCommentDto createOrUpdateCommentDto,
//                                                  @PathVariable @Parameter(description = "уникальный идентификатор объявления") int pk,
//                                                  @PathVariable @Parameter(description = "уникальный идентификатор комментария") int commentId) {
//
//        return commentsService.updateComment(createOrUpdateCommentDto, pk, commentId, authentication);
//    }
//
//}
