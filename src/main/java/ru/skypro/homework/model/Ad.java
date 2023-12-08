package ru.skypro.homework.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pk;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String adImage;
    private int price;
    private String title;
    private String description;
    @Transient
    private int countComment;
    @Transient
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ad", orphanRemoval = true)
    private List<Comment> commentList;

}
