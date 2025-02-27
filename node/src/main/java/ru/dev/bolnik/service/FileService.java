package ru.dev.bolnik.service;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.dev.bolnik.entity.AppDocument;
import ru.dev.bolnik.entity.AppPhoto;
import ru.dev.bolnik.service.enums.LinkType;

public interface FileService {

    AppDocument processDoc(Message telegramMessage);

    AppPhoto processPhoto(Message telegramMessage);

    String generateLink(Long docId, LinkType linkType);
}
