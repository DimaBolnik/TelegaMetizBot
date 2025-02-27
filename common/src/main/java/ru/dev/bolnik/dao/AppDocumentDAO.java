package ru.dev.bolnik.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.dev.bolnik.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
