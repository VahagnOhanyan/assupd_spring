package ru.ctp.motyrev.code;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import ru.ctp.motyrev.interfaces.impls.CollectionWorksBook;
import ru.ctp.motyrev.objects.Works;

import java.sql.SQLException;

public class Validator {

    private Boolean userTaskValidated = false;
    private Boolean taskValidated = false;
    private Boolean stageValidated = false;
    private String validStatus = "";



    private CollectionWorksBook worksBook = new CollectionWorksBook();
    ObservableList<ObservableList> taskArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> stageArray = FXCollections.observableArrayList();
    ObservableList<ObservableList> sheetArray = FXCollections.observableArrayList();

    DBconnection dBconnection = new DBconnection();

    public void validate() {
        dBconnection.openDB();
        try {
            dBconnection.query("SELECT task_number FROM public.task");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                taskArray.add(row);
            }

            dBconnection.query("SELECT stage_type_name FROM public.stage_type");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                stageArray.add(row);
            }

            dBconnection.query("SELECT sheet_id FROM public.sheet WHERE status_id != " +
                    "(SELECT s.status_id FROM public.status s " +
                    "join public.status_type st on st.status_type_id = s.status_type " +
                    "WHERE s.status_name = 'declined' AND st.status_type_name = 'incoming_sheets') " +
                    "AND " +
                    "status_id != " +
                    "(SELECT s.status_id FROM public.status s " +
                    "join public.status_type st on st.status_type_id = s.status_type " +
                    "WHERE s.status_name = 'approved' AND st.status_type_name = 'incoming_sheets')");
            while (dBconnection.getRs().next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=dBconnection.getRs().getMetaData().getColumnCount(); i++){
                    //Перебор колонок
                    row.add(dBconnection.getRs().getString(i));
                }
                sheetArray.add(row);
            }

            dBconnection.queryClose();

            worksBook.fillCollectionDB();

            for (Works work : worksBook.getWorkslist()) {

                if (work.getDesignation().equalsIgnoreCase("Админ. деятельность") || work.getDesignation().equalsIgnoreCase("Отпуск")
                        || work.getDesignation().equalsIgnoreCase("Больничный")
                        || work.getDesignation().equalsIgnoreCase("Обучение") || work.getDesignation().equalsIgnoreCase("IDLE")) {
                    userTaskValidated = true;
                } else {
                    for (int i = 0; i < taskArray.size(); i++) {
                        if (taskArray.get(i).toString().substring(1, taskArray.get(i).toString().length() - 1).equals(work.getDesignation())) {
                            taskValidated = true;
                            break;
                        }
                    }
                }

                for (int i = 0; i < stageArray.size(); i++) {
                    if (stageArray.get(i).toString().substring(1, stageArray.get(i).toString().length() - 1).equalsIgnoreCase(work.getStage())) {
                        stageValidated = true;
                        break;
                    }
                }

                if (userTaskValidated || (taskValidated & stageValidated)) {

                    validStatus = "validated";

                        dBconnection.getStmt().executeUpdate("UPDATE public.parsing_sheet SET status_id = " +
                                "(SELECT s.status_id FROM public.status s " +
                                "join public.status_type st on st.status_type_id = s.status_type " +
                                "WHERE s.status_name = '" + validStatus + "' AND st.status_type_name = 'incoming_works') " +
                                "WHERE parsing_sheet_id = '" + work.getId() + "' AND status_id != '7'");
                        dBconnection.getC().commit();
                }


                taskValidated = false;
                userTaskValidated = false;
                stageValidated = false;
            }

            worksBook.clear();
            worksBook.fillCollectionDB();


            int count = 0;

            for (int i = 0; i < sheetArray.size(); i++) {
                for (Works work: worksBook.getWorkslist()) {
                    if (work.getSheet_id().equals(sheetArray.get(i).toString().replace("[", "").replace("]", "")) && work.getStatus_id().equals("4")) {
                        count +=1;
                    }
                }

                    dBconnection.getStmt().executeUpdate("UPDATE public.sheet SET status_id = " +
                            "(SELECT status_id FROM public.status s " +
                            "join public.status_type st on status_type_id = s.status_type " +
                            "WHERE st.status_type_name = 'incoming_sheets' AND s.status_name = 'correct') " +
                            "WHERE sheet_id = '" + sheetArray.get(i).toString().replace("[", "").replace("]", "") + "' AND sheet_works_count = '"+ count +"'");
                    dBconnection.getC().commit();

                count = 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            dBconnection.closeDB();
            return;
        }

        dBconnection.closeDB();
        taskArray.clear();
        stageArray.clear();
        sheetArray.clear();

    }

}
