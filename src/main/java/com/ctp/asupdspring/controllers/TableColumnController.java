/*
 * Copyright (c) 2008-2022
 * LANIT
 * All rights reserved.
 *
 * This product and related documentation are protected by copyright and
 * distributed under licenses restricting its use, copying, distribution, and
 * decompilation. No part of this product or related documentation may be
 * reproduced in any form by any means without prior written authorization of
 * LANIT and its licensors, if any.
 *
 * $
 */
package com.ctp.asupdspring.controllers;

/**
 * todo Document type TableColumnController
 */
public class TableColumnController {
    private static String tableColName;
    public static String generateColName(String bdColName) {
        switch (bdColName) {
            case "userIdNumber":
                tableColName = "Таб. номер";
                break;
            case "taskName":
                tableColName = "Наименование задачи";
                break;
            case "taskId":
                tableColName = "ID задачи";
                break;
            case "customerName":
                tableColName = "Заказчик";
                break;
            case "contractNumber":
                tableColName = "Номер контракта";
                break;
            case "projectName":
                tableColName = "Проект";
                break;
            case "requestNumber":
                tableColName = "Номер заявки";
                break;
            case "taskNumber":
                tableColName = "Номер задачи";
                break;
            case "taskIncomeDate":
                tableColName = "Дата получения";
                break;
            case "taskPaIntensity":
                tableColName = "Тр-ть план. внутр.";
                break;
            case "taskTzIntensity":
                tableColName = "Тр-ть план. внешн.";
                break;
            case "taskOut":
                tableColName = "Аутсорс";
                break;
            case "statusName":
                tableColName = "Статус";
                break;
            case "projectId":
                tableColName = "ID проекта";
                break;
            case "contractId":
                tableColName = "ID контракта";
                break;
            case "contractName":
                tableColName = "Название контракта";
                break;
            case "userFullname":
                tableColName = "ФИО";
                break;
            case "userTel":
                tableColName = "Телефон";
                break;
            case "userAdress":
                tableColName = "Адрес";
                break;
            case "userEmail":
                tableColName = "Email";
                break;
            case "customerId":
                tableColName = "ID заказчика";
                break;
            case "customerFullName":
                tableColName = "Полное наименование";
                break;
            case "userRoleName":
                tableColName = "Роль";
                break;
            case "siteName":
                tableColName = "Площадка";
                break;
            case "userActivityName":
                tableColName = "Статус пользователя";
                break;
            case "requestId":
                tableColName = "ID заявки";
                break;
            case "requestDescription":
                tableColName = "Описание заявки";
                break;
            case "taskUnitPlan":
                tableColName = "Ед. план";
                break;
            case "taskUnitFact":
                tableColName = "Ед. факт";
                break;
            case "taskUomName":
                tableColName = "Ед. изм. задачи";
                break;
            case "taskState":
                tableColName = "Состояние задач";
                break;
            case "siteId":
                tableColName = "ID площадки";
                break;
            case "siteCode":
                tableColName = "Код";
                break;
            case "percentage":
                tableColName = "Процент заполн-ти";
                break;
            case "day1":
                tableColName = "01";
                break;
            case "day2":
                tableColName = "02";
                break;
            case "day3":
                tableColName = "03";
                break;
            case "day4":
                tableColName = "04";
                break;
            case "day5":
                tableColName = "05";
                break;
            case "day6":
                tableColName = "06";
                break;
            case "day7":
                tableColName = "07";
                break;
            case "day8":
                tableColName = "08";
                break;
            case "day9":
                tableColName = "09";
                break;
            case "day10":
                tableColName = "10";
                break;
            case "day11":
                tableColName = "11";
                break;
            case "day12":
                tableColName = "12";
                break;
            case "day13":
                tableColName = "13";
                break;
            case "day14":
                tableColName = "14";
                break;
            case "day15":
                tableColName = "15";
                break;
            case "day16":
                tableColName = "16";
                break;
            case "day17":
                tableColName = "17";
                break;
            case "day18":
                tableColName = "18";
                break;
            case "day19":
                tableColName = "19";
                break;
            case "day20":
                tableColName = "20";
                break;
            case "day21":
                tableColName = "21";
                break;
            case "day22":
                tableColName = "22";
                break;
            case "day23":
                tableColName = "23";
                break;
            case "day24":
                tableColName = "24";
                break;
            case "day25":
                tableColName = "25";
                break;
            case "day26":
                tableColName = "26";
                break;
            case "day27":
                tableColName = "27";
                break;
            case "day28":
                tableColName = "28";
                break;
            case "day29":
                tableColName = "29";
                break;
            case "day30":
                tableColName = "30";
                break;
            case "day31":
                tableColName = "31";
                break;
        }
        return tableColName;
    }

}
