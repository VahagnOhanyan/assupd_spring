package com.ctp.asupdspring.app.interfaces;

import com.ctp.asupdspring.app.objects.Works;

public interface WorksBook {

    //добавить запись
    void add(Works work);

    //удалить запись
    void delete(Works work);

}
