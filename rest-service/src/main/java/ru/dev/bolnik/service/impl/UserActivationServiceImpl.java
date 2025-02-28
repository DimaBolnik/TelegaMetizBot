package ru.dev.bolnik.service.impl;

import org.springframework.stereotype.Service;
import ru.dev.bolnik.dao.AppUserDAO;
import ru.dev.bolnik.entity.AppUser;
import ru.dev.bolnik.service.UserActivationService;
import ru.dev.bolnik.utils.Decoder;


@Service
public class UserActivationServiceImpl implements UserActivationService {

    private final AppUserDAO appUserDAO;
    private final Decoder decoder;

    public UserActivationServiceImpl(AppUserDAO appUserDAO, Decoder decoder) {
        this.appUserDAO = appUserDAO;
        this.decoder = decoder;
    }

    @Override
    public boolean activation(String cryptoUserId) {
        Long userId = decoder.idOf(cryptoUserId);
        var optional = appUserDAO.findById(userId);
        if (optional.isPresent()) {
            AppUser user = optional.get();
            user.setIsActive(true);
            appUserDAO.save(user);
            return true;
        }
        return false;
    }
}
