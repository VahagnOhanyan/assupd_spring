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
package com.ctp.asupdspring.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * todo Document type ParsingSheetEntity
 */
@Getter
@Setter
@Entity
@Table(name = "parsing_sheet", schema = "public", catalog = "pm_db_alpha")
public class ParsingSheetEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "parsing_sheet_id")
    private int parsingSheetId;
    @Basic
    @Column(name = "user_id_number")
    private String userIdNumber;
    @Basic
    @Column(name = "year")
    private int year;
    @Basic
    @Column(name = "month")
    private int month;
    @Basic
    @Column(name = "work_number")
    private String workNumber;
    @Basic
    @Column(name = "stage")
    private String stage;
    @Basic
    @Column(name = "work_name")
    private String workName;
    @Basic
    @Column(name = "customer")
    private String customer;
    @Basic
    @Column(name = "contract")
    private String contract;
    @Basic
    @Column(name = "request")
    private String request;
    @Basic
    @Column(name = "intensity")
    private String intensity;
    @Basic
    @Column(name = "overtime")
    private String overtime;
    @Basic
    @Column(name = "work_start_date")
    private String workStartDate;
    @Basic
    @Column(name = "work_end_date_pa")
    private String workEndDatePa;
    @Basic
    @Column(name = "work_end_date")
    private String workEndDate;
    @Basic
    @Column(name = "note")
    private String note;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "status_id", nullable = false)
    private StatusEntity statusId;
    @ManyToOne
    @JoinColumn(name = "sheet_id", referencedColumnName = "sheet_id", nullable = false)
    private SheetEntity sheetId;


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParsingSheetEntity that = (ParsingSheetEntity) o;
        return parsingSheetId == that.parsingSheetId && year == that.year && month == that.month && statusId == that.statusId && sheetId == that.sheetId &&
                Objects.equals(userIdNumber, that.userIdNumber) && Objects.equals(workNumber, that.workNumber) &&
                Objects.equals(stage, that.stage) && Objects.equals(workName, that.workName) &&
                Objects.equals(customer, that.customer) && Objects.equals(contract, that.contract) &&
                Objects.equals(request, that.request) && Objects.equals(intensity, that.intensity) &&
                Objects.equals(overtime, that.overtime) && Objects.equals(workStartDate, that.workStartDate) &&
                Objects.equals(workEndDatePa, that.workEndDatePa) && Objects.equals(workEndDate, that.workEndDate) &&
                Objects.equals(note, that.note);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parsingSheetId, userIdNumber, year, month, workNumber, stage, workName, customer, contract, request, intensity, overtime,
                workStartDate,
                workEndDatePa, workEndDate, note, statusId, sheetId);
    }

}
