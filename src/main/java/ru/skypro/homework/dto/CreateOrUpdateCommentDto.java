package ru.skypro.homework.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter
@Getter
@ToString
@RequiredArgsConstructor
@Schema(description = "сохранение и обновление комментария")
public class CreateOrUpdateCommentDto {
    @Schema(description = "текст комментария")
    @NotBlank
    @Size(min = 8, max = 64)
    private String  text;
}
