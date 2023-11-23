package ru.skypro.homework.repository;

import org.springframework.cglib.transform.impl.AddInitTransformer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skypro.homework.dto.AdDto;
import ru.skypro.homework.model.Ad;
import ru.skypro.homework.model.User;

import java.util.List;

@Repository
public interface AdRepository extends JpaRepository<Ad, Long> {
    List<Ad> findAdByUser(User user);

    Ad findByPk(int pk);
//    Ad findAdByUser_Id(int pk);
//
//    AdDto deleteById(int pk);

}


