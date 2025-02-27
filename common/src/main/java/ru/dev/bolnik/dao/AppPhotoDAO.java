package ru.dev.bolnik.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.dev.bolnik.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
