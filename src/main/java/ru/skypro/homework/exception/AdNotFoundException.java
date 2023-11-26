package ru.skypro.homework.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.metamodel.SingularAttribute;
import java.io.Serializable;
@RequiredArgsConstructor
public class AdNotFoundException extends RuntimeException {
    private final int pk;
    @Override
    public String getMessage(){
        return "Объявление с таким id: " + pk + " - не найдено";
    }
}
