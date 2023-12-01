package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

import java.time.LocalDateTime;

import static liquibase.repackaged.net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Table(name = "Comments")
public class Comment {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
//    @Transient
    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;
    private long createdAt;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    private String text;

}
