package ru.dev.bolnik.services;


import ru.dev.bolnik.dto.MailParams;

public interface MailSenderService {

    void send(MailParams mailParams);
}
