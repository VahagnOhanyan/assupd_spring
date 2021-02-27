package ru.ctp.motyrev.interfaces;

import ru.ctp.motyrev.objects.Works;

public interface WorksBook {

    //добавить запись
    void add(Works work);

    //удалить запись
    void delete(Works work);

}
