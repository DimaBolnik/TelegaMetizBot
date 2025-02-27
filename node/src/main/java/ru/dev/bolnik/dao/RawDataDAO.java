package ru.dev.bolnik.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.dev.bolnik.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {
}
