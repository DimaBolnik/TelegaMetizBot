package ru.dev.bolnik.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.dev.bolnik.entity.AppUser;

import java.util.Optional;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

   Optional<AppUser> findByEmail(String email);
   Optional<AppUser> findByTelegramUserId(Long id);
}
