package com.ctp.asupdspring.app.interfaces;

import com.ctp.asupdspring.app.objects.UserState;

public interface UserStateInterface {

    //добавить запись
    void add(UserState userState);

    //удалить запись
    void delete(UserState userState);

}
