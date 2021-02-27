package ru.ctp.motyrev.interfaces;

import ru.ctp.motyrev.objects.UserState;

public interface UserStateInterface {

    //добавить запись
    void add(UserState userState);

    //удалить запись
    void delete(UserState userState);

}
