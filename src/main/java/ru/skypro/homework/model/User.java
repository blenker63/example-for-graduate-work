package ru.skypro.homework.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.skypro.homework.dto.Role;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
@Table(name = "Users")
public class User  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String userImage;
    private String userName;
    private String password;
    @Transient
    private int countAd;
    @Transient
    @OneToMany(mappedBy = "user")
    private List<Ad> adList;
    @Transient
    @OneToMany(mappedBy = "user")
    private List<Comment> commentList;
}
