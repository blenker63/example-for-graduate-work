package ru.skypro.homework.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.model.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Integer> {
    List<Comment> findAllByAd_Pk(int id);

    Optional<Comment> findByPk(int id);

    Optional<Comment> findByAd_PkAndPk(int adId, int commentId);

}
