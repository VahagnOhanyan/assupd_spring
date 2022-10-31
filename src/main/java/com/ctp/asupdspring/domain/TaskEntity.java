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

import lombok.*;

import javax.persistence.*;
import java.sql.Date;

/**
 * todo Document type TaskEntity
 */
@Getter
@Setter
@ToString
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "task", schema = "public", catalog = "pm_db_alpha")
public class TaskEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "task_id")
    private int taskId;
    @Basic
    @Column(name = "task_number")
    private String taskNumber;
    @Basic
    @Column(name = "task_name")
    private String taskName;
    @Basic
    @Column(name = "task_description")
    private String taskDescription;
    @Basic
    @Column(name = "parent_task_id")
    private Integer parentTaskId;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private StatusEntity statusId;
    @Basic
    @Column(name = "task_income_date")
    private Date taskIncomeDate;
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id")
    private ProjectEntity projectId;
    @ManyToOne
    @JoinColumn(name = "request_id", referencedColumnName = "request_id")
    private RequestEntity requestId;
    @Basic
    @Column(name = "task_pa_intensity")
    private Integer taskPaIntensity;
    @Basic
    @Column(name = "task_tz_intensity")
    private Integer taskTzIntensity;
    @Basic
    @Column(name = "task_out")
    private Boolean taskOut;
    @Basic
    @Column(name = "task_unit_fact")
    private Integer taskUnitFact;
    @ManyToOne
    @JoinColumn(name = "task_uom_id", referencedColumnName = "task_uom_id")
    private TaskUomEntity taskUomId;
    @Basic
    @Column(name = "task_unit_plan")
    private Integer taskUnitPlan;
    @Basic
    @Column(name = "req_date_start")
    private Date reqDateStart;
    @Basic
    @Column(name = "req_date_end")
    private Date reqDateEnd;


}