package ru.dev.bolnik.services;


import ru.dev.bolnik.dto.MailParams;

public interface ConsumerService {
    void consumeRegistrationMail(MailParams mailParams);
}