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
package ru.ctp.motyrev.code;

import au.com.bytecode.opencsv.CSVReader;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class CSVLoader {

    private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
    private static final String SQL_INSERT_IMPORTED = "INSERT INTO ${table2}(${keys2}) VALUES(${values2})";
    private static final String TABLE_REGEX = "\\$\\{table}";
    private static final String TABLE_REGEX2 = "\\$\\{table2}";
    private static final String KEYS_REGEX = "\\$\\{keys}";
    private static final String KEYS_REGEX2 = "\\$\\{keys2}";
    private static final String VALUES_REGEX = "\\$\\{values}";
    private static final String VALUES_REGEX2 = "\\$\\{values2}";

    private final Connection connection;
    private char seprator;
    private String monthYear;

    public CSVLoader(Connection connection) {
        this.connection = connection;
        this.seprator = ';';
    }

    public void loadCSV(File csvFile, String tableName) throws Exception {

        if (null == this.connection) {
            throw new Exception("Not a valid connection.");
        }
        String[] headerRow = null;
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"), this.seprator)) {
            headerRow = csvReader.readNext();

            if (null == headerRow) {
                throw new FileNotFoundException(
                        "No columns defined in given CSV file." +
                                "Please check the CSV file format.");
            }
            String[] tableHeaderRow = new String[7];
            for (int i = 0; i < tableHeaderRow.length; i++) {
                tableHeaderRow[i] = returnRowName(headerRow[i]);
            }
            String questionmarks = StringUtils.repeat("?,", tableHeaderRow.length);
            questionmarks = (String) questionmarks.subSequence(0, questionmarks
                    .length() - 1);

            String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
            query = query
                    .replaceFirst(KEYS_REGEX, StringUtils.join(tableHeaderRow, ","));
            query = query.replaceFirst(VALUES_REGEX, questionmarks);

            String query2 = SQL_INSERT_IMPORTED.replaceFirst(TABLE_REGEX2, "onec_imported");
            query2 = query2.replaceFirst(KEYS_REGEX2, "monthyear");
            query2 = query2.replaceFirst(VALUES_REGEX2, "?");
            String[] nextLine;
            Connection con = null;
            PreparedStatement ps = null;
            PreparedStatement ps2 = null;
            try {
                con = this.connection;
                con.setAutoCommit(false);
                ps = con.prepareStatement(query);

                while ((nextLine = csvReader.readNext()) != null) {
                    if (nextLine[0] == null || nextLine[0].equals("") || nextLine[0].equals("Итого")) {
                        continue;
                    }
                    for (int i = 1; i <= tableHeaderRow.length; i++) {
                        if (i < 4) {
                            if (nextLine[i - 1] == null || nextLine[i - 1].equals("")) {
                                break;
                            }
                            ps.setString(i, nextLine[i - 1]);
                        } else {
                            if (nextLine[i - 1] == null || nextLine[i - 1].equals("")) {
                                nextLine[i - 1] = "0.0";
                            }
                            ps.setDouble(i, Double.parseDouble(nextLine[i - 1].replace(",", ".")));
                        }
                        if (i == 3) {
                            monthYear = nextLine[i - 1];
                        }
                    }
                    ps.addBatch();
                }
                ps.executeBatch();
                ps2 = con.prepareStatement(query2);
                ps2.setString(1, monthYear);
                ps2.addBatch();
                ps2.executeBatch();
                con.commit();
            } catch (Exception e) {
                con.rollback();
                e.printStackTrace();
                throw new Exception(
                        "Error occured while loading data from file to database."
                                + e.getMessage());
            } finally {
                if (null != ps) {
                    ps.close();
                }
                if (null != ps2) {
                    ps2.close();
                }
                con.close();
            }
        }
    }

    public char getSeprator() {
        return seprator;
    }

    public void setSeprator(char seprator) {
        this.seprator = seprator;
    }

    public String returnRowName(String row) {
        String r = null;
        switch (row.trim()) {
            case "Табельный номер":
                r = "num";
                break;
            case "ФИО":
                r = "fullname";
                break;
            case "Месяц, Год":
                r = "monthyear";
                break;
            case "Отработано":
                r = "worked";
                break;
            case "Больничный":
                r = "hospital";
                break;
            case "Отпуск":
                r = "vacation";
                break;
            case "Итого":
                r = "total";
                break;
            default:
                r = "num";
        }
        return r;
    }
}
