package ru.dev.bolnik.service;


import ru.dev.bolnik.entity.AppDocument;
import ru.dev.bolnik.entity.AppPhoto;

public interface FileService {

    AppDocument getDocument(String id);

    AppPhoto getPhoto(String id);
}
