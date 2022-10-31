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

import au.com.bytecode.opencsv.CSVReader;
import com.ctp.asupdspring.app.repo.OnecEntityRepository;
import com.ctp.asupdspring.app.repo.OnecImportedEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor

@Component
public class CSVLoader {
    private final OnecEntityRepository onecEntityRepository;
    private final OnecImportedEntityRepository onecImportedEntityRepository;

    @Transactional
    public void loadCSV(File csvFile) throws Exception {

        String[] headerRow = null;
        char seprator = ';';
        try (CSVReader csvReader = new CSVReader(new InputStreamReader(new FileInputStream(csvFile), StandardCharsets.UTF_8), seprator)) {
            headerRow = csvReader.readNext();

            if (null == headerRow) {
                throw new FileNotFoundException(
                        "No columns defined in given CSV file." +
                                "Please check the CSV file format.");
            }

            String[] nextLine;

            while ((nextLine = csvReader.readNext()) != null) {
                if (nextLine[0] == null || nextLine[0].equals("") || nextLine[0].equals("Итого")) {
                    continue;
                }
                String num = nextLine[0];

                String fullname = nextLine[1];

                String monthyear = nextLine[2];

                Double worked = nextLine[3] == null || nextLine[3].equals("") ? 0.0 : Double.parseDouble(nextLine[3]);

                Double hospital = nextLine[4] == null || nextLine[4].equals("") ? 0.0 : Double.parseDouble(nextLine[4]);

                Double vacation = nextLine[5] == null || nextLine[5].equals("") ? 0.0 : Double.parseDouble(nextLine[5]);

                double total = nextLine[6] == null || nextLine[6].equals("") ? 0.0 : Double.parseDouble(nextLine[6]);
                onecEntityRepository.updateByNum(fullname, monthyear, worked, hospital, vacation, total, num);
                onecImportedEntityRepository.update(monthyear);
            }
        }
    }
}
