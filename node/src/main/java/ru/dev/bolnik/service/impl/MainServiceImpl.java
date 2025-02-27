package ru.dev.bolnik.service.impl;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.dev.bolnik.dao.AppUserDAO;
import ru.dev.bolnik.dao.RawDataDAO;
import ru.dev.bolnik.entity.AppDocument;
import ru.dev.bolnik.entity.AppPhoto;
import ru.dev.bolnik.entity.AppUser;
import ru.dev.bolnik.entity.RawData;
import ru.dev.bolnik.enums.UserState;
import ru.dev.bolnik.exceptions.UploadFileException;
import ru.dev.bolnik.service.AppUserService;
import ru.dev.bolnik.service.FileService;
import ru.dev.bolnik.service.MainService;
import ru.dev.bolnik.service.ProducerService;
import ru.dev.bolnik.service.enums.LinkType;
import ru.dev.bolnik.service.enums.ServiceCommands;

import java.util.Optional;

import static ru.dev.bolnik.enums.UserState.BASIC_STATE;
import static ru.dev.bolnik.enums.UserState.WAIT_FOR_EMAIL_STATE;
import static ru.dev.bolnik.service.enums.ServiceCommands.*;


@Log4j
@Service
public class MainServiceImpl implements MainService {

    private final RawDataDAO rawDataDAO;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;
    private final AppUserService appUserService;
    private final ProducerService producerService;


    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService, AppUserService appUserService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
        this.appUserService = appUserService;
    }

//    @Transactional
    @Override
    public void processTextMessage(Update update) {
        saveRawDate(update);
        AppUser appUser = findOrSaveAppUser(update);
        UserState userState = appUser.getState();
        String text = update.getMessage().getText();
        String output = "";

        ServiceCommands serviceCommands = ServiceCommands.fromValue(text);

        if (CANCEL.equals(serviceCommands)) {
            output = cancelProcess(appUser);
        } else if (BASIC_STATE.equals(userState)) {
            output = processServiceCommand(appUser, text);
        } else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
            output = appUserService.setEmail(appUser, text);
        } else {
            log.debug("Unknown user state: " + userState);
            output = "Неизвестная ошибка! Введите /cancel и попробуйте снова!";
        }

        Long chatId = update.getMessage().getChatId();
        sendAnswer(output, chatId);
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawDate(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            String link = fileService.generateLink(doc.getId(), LinkType.GET_DOC);
            String answer = "Документ успешно загружен! " +
                            "Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению загрузка файла не удалась. Повторите попутку позже.";
            sendAnswer(error, chatId);
        }
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawDate(update);
        AppUser appUser = findOrSaveAppUser(update);
        Long chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }
        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            String link = fileService.generateLink(photo.getId(), LinkType.GET_PHOTO);
            String answer = "Фото успешно загружено!" +
                            " Ссылка для скачивания: " + link;
            sendAnswer(answer, chatId);
        } catch (UploadFileException ex) {
            log.error(ex);
            String error = "К сожалению загрузка фото не удалась. Повторите попытку позже.";
            sendAnswer(error, chatId);
        }


    }

    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        UserState userState = appUser.getState();
        if (!appUser.getIsActive()) {
            String error = "Зарегистрируйтесь или активируйте свою " +
                           "учетную запись для загрузки контента.";
            sendAnswer(error, chatId);
            return true;
        } else if (!BASIC_STATE.equals(userState)) {
            String error = "Отмените текущую команду с " +
                           "помощью /cancel для отправки файлов.";
            sendAnswer(error, chatId);
            return true;
        }
        return false;
    }

    private void sendAnswer(String output, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(output);
        producerService.producerAnswer(sendMessage);
    }

    private String processServiceCommand(AppUser appUser, String cmd) {
        ServiceCommands serviceCommands = ServiceCommands.fromValue(cmd);
        if (REGISTRATION.equals(serviceCommands)) {
            return appUserService.registerUser(appUser);
        } else if (HELP.equals(serviceCommands)) {
            return help();
        } else if (START.equals(serviceCommands)) {
            return "Приветствую! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд: \n" +
               " /cancel - отмена выполнения текушей команды;\n" +
               " /registration - регистрация пользователя.";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setState(BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();
        Optional<AppUser> appUserOpt = appUserDAO.findByTelegramUserId(telegramUser.getId());
        if (appUserOpt.isEmpty()) {
            AppUser transientUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .userName(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActive(false)
                    .state(BASIC_STATE)
                    .build();
            return appUserDAO.save(transientUser);
        }
        return appUserOpt.get();
    }

    private void saveRawDate(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
