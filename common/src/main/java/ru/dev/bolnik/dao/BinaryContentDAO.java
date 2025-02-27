package ru.dev.bolnik.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.dev.bolnik.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
