package ru.skypro.homework.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long pk;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String adImage;
    private int price;
    private String title;
    private String description;
    private int countComment;
    @Transient
    private List<Comment> commentList;

}
