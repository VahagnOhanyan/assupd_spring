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
package com.ctp.asupdspring.app.repo;

/**
 * todo Document type UserServiceImplTest
 */

/*
@SpringBootTest(classes = AsupdSpringApplication.class)
@Slf4j
class UserServiceImplTest {
    @Autowired
    UserService userService;
    @Autowired
    TimesheetViewRepository timesheetViewRepository;

    @Test
    void name() {
        */
/*List<Object[]>  resultList = new ArrayList<>();
        Map<UserEntity, Map<Integer, Integer>> res = new HashMap<>();
        Map<UserEntity, List<DailyWorkEntity>> userEntityListMap =
                timesheetViewRepository.getEmployeeSubOrdDetailed("Калько Андрей Константинович", "2022-07-01");
        for (Map.Entry<UserEntity, List<DailyWorkEntity>> entry : userEntityListMap.entrySet()) {
            Map<Integer, Integer> integerBigIntegerMap = entry.getValue().stream().collect(Collectors.groupingBy(DailyWorkEntity::getDayNum, Collectors.summingInt(DailyWorkEntity::getDailyIntensity)));
            res.put(entry.getKey(), integerBigIntegerMap);

        }
        for (Map.Entry<UserEntity, Map<Integer, Integer>> entry2 : res.entrySet()) {
            System.out.println(entry2.getKey().getUserFullname() + ": ");
            Object[] row = new Object[5];
            row[0] = entry2.getKey().getUserIdNumber();
            row[1] = entry2.getKey().getUserFullname();
            row[2] = "";
            for (Map.Entry<Integer, Integer> entry3 : entry2.getValue().entrySet()) {
                System.out.println(entry3.getKey() + ": " + entry3.getValue());
                row[3] = entry3.getKey();
                row[4] = entry3.getValue();
                resultList.add(row);
            }



        }  *//*

    }
}
*/
