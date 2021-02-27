package ru.ctp.motyrev.objects;

import javafx.beans.property.SimpleStringProperty;

public class TimeSheet {

    private SimpleStringProperty work_num = new SimpleStringProperty("");
    private SimpleStringProperty work_stage = new SimpleStringProperty("");
    private SimpleStringProperty work_note = new SimpleStringProperty("");
    private SimpleStringProperty start_perc = new SimpleStringProperty("");

    private SimpleStringProperty day1 = new SimpleStringProperty("");
    private SimpleStringProperty day2 = new SimpleStringProperty("");
    private SimpleStringProperty day3 = new SimpleStringProperty("");
    private SimpleStringProperty day4 = new SimpleStringProperty("");
    private SimpleStringProperty day5 = new SimpleStringProperty("");
    private SimpleStringProperty day6 = new SimpleStringProperty("");
    private SimpleStringProperty day7 = new SimpleStringProperty("");
    private SimpleStringProperty day8 = new SimpleStringProperty("");
    private SimpleStringProperty day9 = new SimpleStringProperty("");
    private SimpleStringProperty day10 = new SimpleStringProperty("");
    private SimpleStringProperty day11 = new SimpleStringProperty("");
    private SimpleStringProperty day12 = new SimpleStringProperty("");
    private SimpleStringProperty day13 = new SimpleStringProperty("");
    private SimpleStringProperty day14 = new SimpleStringProperty("");
    private SimpleStringProperty day15 = new SimpleStringProperty("");
    private SimpleStringProperty day16 = new SimpleStringProperty("");
    private SimpleStringProperty day17 = new SimpleStringProperty("");
    private SimpleStringProperty day18 = new SimpleStringProperty("");
    private SimpleStringProperty day19 = new SimpleStringProperty("");
    private SimpleStringProperty day20 = new SimpleStringProperty("");
    private SimpleStringProperty day21 = new SimpleStringProperty("");
    private SimpleStringProperty day22 = new SimpleStringProperty("");
    private SimpleStringProperty day23 = new SimpleStringProperty("");
    private SimpleStringProperty day24 = new SimpleStringProperty("");
    private SimpleStringProperty day25 = new SimpleStringProperty("");
    private SimpleStringProperty day26 = new SimpleStringProperty("");
    private SimpleStringProperty day27 = new SimpleStringProperty("");
    private SimpleStringProperty day28 = new SimpleStringProperty("");
    private SimpleStringProperty day29 = new SimpleStringProperty("");
    private SimpleStringProperty day30 = new SimpleStringProperty("");
    private SimpleStringProperty day31 = new SimpleStringProperty("");

    private SimpleStringProperty end_perc = new SimpleStringProperty("");

    public TimeSheet () {

    }

    public TimeSheet (String work_num, String work_stage, String work_note, String start_perc,String day1, String day2, String day3, String day4, String day5,
                      String day6, String day7, String day8, String day9, String day10, String day11, String day12, String day13, String day14,
                      String day15, String day16, String day17, String day18, String day19, String day20, String day21, String day22,
                      String day23, String day24, String day25, String day26, String day27, String day28, String day29, String day30,
                      String day31, String end_perc) {

        this.work_num = new SimpleStringProperty(work_num);
        this.work_stage = new SimpleStringProperty(work_stage);
        this.work_note = new SimpleStringProperty(work_note);
        this.start_perc = new SimpleStringProperty(start_perc);
        this.day1 = new SimpleStringProperty(day1);
        this.day2 = new SimpleStringProperty(day2);
        this.day3 = new SimpleStringProperty(day3);
        this.day4 = new SimpleStringProperty(day4);
        this.day5 = new SimpleStringProperty(day5);
        this.day6 = new SimpleStringProperty(day6);
        this.day7 = new SimpleStringProperty(day7);
        this.day8 = new SimpleStringProperty(day8);
        this.day9 = new SimpleStringProperty(day9);
        this.day10 = new SimpleStringProperty(day10);
        this.day11 = new SimpleStringProperty(day11);
        this.day12 = new SimpleStringProperty(day12);
        this.day13 = new SimpleStringProperty(day13);
        this.day14 = new SimpleStringProperty(day14);
        this.day15 = new SimpleStringProperty(day15);
        this.day16 = new SimpleStringProperty(day16);
        this.day17 = new SimpleStringProperty(day17);
        this.day18 = new SimpleStringProperty(day18);
        this.day19 = new SimpleStringProperty(day19);
        this.day20 = new SimpleStringProperty(day20);
        this.day21 = new SimpleStringProperty(day21);
        this.day22 = new SimpleStringProperty(day22);
        this.day23 = new SimpleStringProperty(day23);
        this.day24 = new SimpleStringProperty(day24);
        this.day25 = new SimpleStringProperty(day25);
        this.day26 = new SimpleStringProperty(day26);
        this.day27 = new SimpleStringProperty(day27);
        this.day28 = new SimpleStringProperty(day28);
        this.day29 = new SimpleStringProperty(day29);
        this.day30 = new SimpleStringProperty(day30);
        this.day31 = new SimpleStringProperty(day31);
        this.end_perc = new SimpleStringProperty(end_perc);
    }

    public TimeSheet (String work_num, String work_stage, String work_note, String start_perc,String day1, String day2, String day3, String day4, String day5,
                      String day6, String day7, String day8, String day9, String day10, String day11, String day12, String day13, String day14,
                      String day15, String day16, String day17, String day18, String day19, String day20, String day21, String day22,
                      String day23, String day24, String day25, String day26, String day27, String day28, String day29, String day30,
                      String end_perc) {

        this.work_num = new SimpleStringProperty(work_num);
        this.work_stage = new SimpleStringProperty(work_stage);
        this.work_note = new SimpleStringProperty(work_note);
        this.start_perc = new SimpleStringProperty(start_perc);
        this.day1 = new SimpleStringProperty(day1);
        this.day2 = new SimpleStringProperty(day2);
        this.day3 = new SimpleStringProperty(day3);
        this.day4 = new SimpleStringProperty(day4);
        this.day5 = new SimpleStringProperty(day5);
        this.day6 = new SimpleStringProperty(day6);
        this.day7 = new SimpleStringProperty(day7);
        this.day8 = new SimpleStringProperty(day8);
        this.day9 = new SimpleStringProperty(day9);
        this.day10 = new SimpleStringProperty(day10);
        this.day11 = new SimpleStringProperty(day11);
        this.day12 = new SimpleStringProperty(day12);
        this.day13 = new SimpleStringProperty(day13);
        this.day14 = new SimpleStringProperty(day14);
        this.day15 = new SimpleStringProperty(day15);
        this.day16 = new SimpleStringProperty(day16);
        this.day17 = new SimpleStringProperty(day17);
        this.day18 = new SimpleStringProperty(day18);
        this.day19 = new SimpleStringProperty(day19);
        this.day20 = new SimpleStringProperty(day20);
        this.day21 = new SimpleStringProperty(day21);
        this.day22 = new SimpleStringProperty(day22);
        this.day23 = new SimpleStringProperty(day23);
        this.day24 = new SimpleStringProperty(day24);
        this.day25 = new SimpleStringProperty(day25);
        this.day26 = new SimpleStringProperty(day26);
        this.day27 = new SimpleStringProperty(day27);
        this.day28 = new SimpleStringProperty(day28);
        this.day29 = new SimpleStringProperty(day29);
        this.day30 = new SimpleStringProperty(day30);
        this.end_perc = new SimpleStringProperty(end_perc);
    }

    public TimeSheet (String work_num, String work_stage, String work_note, String day1, String day2, String day3, String day4, String day5,
                      String day6, String day7, String day8, String day9, String day10, String day11, String day12, String day13, String day14,
                      String day15, String day16, String day17, String day18, String day19, String day20, String day21, String day22,
                      String day23, String day24, String day25, String day26, String day27, String day28, String day29) {

        this.work_num = new SimpleStringProperty(work_num);
        this.work_stage = new SimpleStringProperty(work_stage);
        this.work_note = new SimpleStringProperty(work_note);
        this.day1 = new SimpleStringProperty(day1);
        this.day2 = new SimpleStringProperty(day2);
        this.day3 = new SimpleStringProperty(day3);
        this.day4 = new SimpleStringProperty(day4);
        this.day5 = new SimpleStringProperty(day5);
        this.day6 = new SimpleStringProperty(day6);
        this.day7 = new SimpleStringProperty(day7);
        this.day8 = new SimpleStringProperty(day8);
        this.day9 = new SimpleStringProperty(day9);
        this.day10 = new SimpleStringProperty(day10);
        this.day11 = new SimpleStringProperty(day11);
        this.day12 = new SimpleStringProperty(day12);
        this.day13 = new SimpleStringProperty(day13);
        this.day14 = new SimpleStringProperty(day14);
        this.day15 = new SimpleStringProperty(day15);
        this.day16 = new SimpleStringProperty(day16);
        this.day17 = new SimpleStringProperty(day17);
        this.day18 = new SimpleStringProperty(day18);
        this.day19 = new SimpleStringProperty(day19);
        this.day20 = new SimpleStringProperty(day20);
        this.day21 = new SimpleStringProperty(day21);
        this.day22 = new SimpleStringProperty(day22);
        this.day23 = new SimpleStringProperty(day23);
        this.day24 = new SimpleStringProperty(day24);
        this.day25 = new SimpleStringProperty(day25);
        this.day26 = new SimpleStringProperty(day26);
        this.day27 = new SimpleStringProperty(day27);
        this.day28 = new SimpleStringProperty(day28);
        this.day29 = new SimpleStringProperty(day29);
    }

    public TimeSheet (String work_num, String work_stage, String work_note, String day1, String day2, String day3, String day4, String day5,
                      String day6, String day7, String day8, String day9, String day10, String day11, String day12, String day13, String day14,
                      String day15, String day16, String day17, String day18, String day19, String day20, String day21, String day22,
                      String day23, String day24, String day25, String day26, String day27, String day28) {

        this.work_num = new SimpleStringProperty(work_num);
        this.work_stage = new SimpleStringProperty(work_stage);
        this.work_note = new SimpleStringProperty(work_note);
        this.day1 = new SimpleStringProperty(day1);
        this.day2 = new SimpleStringProperty(day2);
        this.day3 = new SimpleStringProperty(day3);
        this.day4 = new SimpleStringProperty(day4);
        this.day5 = new SimpleStringProperty(day5);
        this.day6 = new SimpleStringProperty(day6);
        this.day7 = new SimpleStringProperty(day7);
        this.day8 = new SimpleStringProperty(day8);
        this.day9 = new SimpleStringProperty(day9);
        this.day10 = new SimpleStringProperty(day10);
        this.day11 = new SimpleStringProperty(day11);
        this.day12 = new SimpleStringProperty(day12);
        this.day13 = new SimpleStringProperty(day13);
        this.day14 = new SimpleStringProperty(day14);
        this.day15 = new SimpleStringProperty(day15);
        this.day16 = new SimpleStringProperty(day16);
        this.day17 = new SimpleStringProperty(day17);
        this.day18 = new SimpleStringProperty(day18);
        this.day19 = new SimpleStringProperty(day19);
        this.day20 = new SimpleStringProperty(day20);
        this.day21 = new SimpleStringProperty(day21);
        this.day22 = new SimpleStringProperty(day22);
        this.day23 = new SimpleStringProperty(day23);
        this.day24 = new SimpleStringProperty(day24);
        this.day25 = new SimpleStringProperty(day25);
        this.day26 = new SimpleStringProperty(day26);
        this.day27 = new SimpleStringProperty(day27);
        this.day28 = new SimpleStringProperty(day28);
    }

    public String getStart_perc() {
        return start_perc.get();
    }

    public SimpleStringProperty start_percProperty() {
        return start_perc;
    }

    public void setStart_perc(String start_perc) {
        this.start_perc.set(start_perc);
    }

    public String getEnd_perc() {
        return end_perc.get();
    }

    public SimpleStringProperty end_percProperty() {
        return end_perc;
    }

    public void setEnd_perc(String end_perc) {
        this.end_perc.set(end_perc);
    }

    public String getWork_num() {
        return work_num.get();
    }

    public SimpleStringProperty work_numProperty() {
        return work_num;
    }

    public void setWork_num(String work_num) {
        this.work_num.set(work_num);
    }

    public String getWork_stage() {
        return work_stage.get();
    }

    public SimpleStringProperty work_stageProperty() {
        return work_stage;
    }

    public void setWork_stage(String work_stage) {
        this.work_stage.set(work_stage);
    }

    public String getWork_note() {
        return work_note.get();
    }

    public SimpleStringProperty work_noteProperty() {
        return work_note;
    }

    public void setWork_note(String work_note) {
        this.work_note.set(work_note);
    }

    public String getDay1() {
        return day1.get();
    }

    public SimpleStringProperty day1Property() {
        return day1;
    }

    public void setDay1(String day1) {
        this.day1.set(day1);
    }

    public String getDay2() {
        return day2.get();
    }

    public SimpleStringProperty day2Property() {
        return day2;
    }

    public void setDay2(String day2) {
        this.day2.set(day2);
    }

    public String getDay3() {
        return day3.get();
    }

    public SimpleStringProperty day3Property() {
        return day3;
    }

    public void setDay3(String day3) {
        this.day3.set(day3);
    }

    public String getDay4() {
        return day4.get();
    }

    public SimpleStringProperty day4Property() {
        return day4;
    }

    public void setDay4(String day4) {
        this.day4.set(day4);
    }

    public String getDay5() {
        return day5.get();
    }

    public SimpleStringProperty day5Property() {
        return day5;
    }

    public void setDay5(String day5) {
        this.day5.set(day5);
    }

    public String getDay6() {
        return day6.get();
    }

    public SimpleStringProperty day6Property() {
        return day6;
    }

    public void setDay6(String day6) {
        this.day6.set(day6);
    }

    public String getDay7() {
        return day7.get();
    }

    public SimpleStringProperty day7Property() {
        return day7;
    }

    public void setDay7(String day7) {
        this.day7.set(day7);
    }

    public String getDay8() {
        return day8.get();
    }

    public SimpleStringProperty day8Property() {
        return day8;
    }

    public void setDay8(String day8) {
        this.day8.set(day8);
    }

    public String getDay9() {
        return day9.get();
    }

    public SimpleStringProperty day9Property() {
        return day9;
    }

    public void setDay9(String day9) {
        this.day9.set(day9);
    }

    public String getDay10() {
        return day10.get();
    }

    public SimpleStringProperty day10Property() {
        return day10;
    }

    public void setDay10(String day10) {
        this.day10.set(day10);
    }

    public String getDay11() {
        return day11.get();
    }

    public SimpleStringProperty day11Property() {
        return day11;
    }

    public void setDay11(String day11) {
        this.day11.set(day11);
    }

    public String getDay12() {
        return day12.get();
    }

    public SimpleStringProperty day12Property() {
        return day12;
    }

    public void setDay12(String day12) {
        this.day12.set(day12);
    }

    public String getDay13() {
        return day13.get();
    }

    public SimpleStringProperty day13Property() {
        return day13;
    }

    public void setDay13(String day13) {
        this.day13.set(day13);
    }

    public String getDay14() {
        return day14.get();
    }

    public SimpleStringProperty day14Property() {
        return day14;
    }

    public void setDay14(String day14) {
        this.day14.set(day14);
    }

    public String getDay15() {
        return day15.get();
    }

    public SimpleStringProperty day15Property() {
        return day15;
    }

    public void setDay15(String day15) {
        this.day15.set(day15);
    }

    public String getDay16() {
        return day16.get();
    }

    public SimpleStringProperty day16Property() {
        return day16;
    }

    public void setDay16(String day16) {
        this.day16.set(day16);
    }

    public String getDay17() {
        return day17.get();
    }

    public SimpleStringProperty day17Property() {
        return day17;
    }

    public void setDay17(String day17) {
        this.day17.set(day17);
    }

    public String getDay18() {
        return day18.get();
    }

    public SimpleStringProperty day18Property() {
        return day18;
    }

    public void setDay18(String day18) {
        this.day18.set(day18);
    }

    public String getDay19() {
        return day19.get();
    }

    public SimpleStringProperty day19Property() {
        return day19;
    }

    public void setDay19(String day19) {
        this.day19.set(day19);
    }

    public String getDay20() {
        return day20.get();
    }

    public SimpleStringProperty day20Property() {
        return day20;
    }

    public void setDay20(String day20) {
        this.day20.set(day20);
    }

    public String getDay21() {
        return day21.get();
    }

    public SimpleStringProperty day21Property() {
        return day21;
    }

    public void setDay21(String day21) {
        this.day21.set(day21);
    }

    public String getDay22() {
        return day22.get();
    }

    public SimpleStringProperty day22Property() {
        return day22;
    }

    public void setDay22(String day22) {
        this.day22.set(day22);
    }

    public String getDay23() {
        return day23.get();
    }

    public SimpleStringProperty day23Property() {
        return day23;
    }

    public void setDay23(String day23) {
        this.day23.set(day23);
    }

    public String getDay24() {
        return day24.get();
    }

    public SimpleStringProperty day24Property() {
        return day24;
    }

    public void setDay24(String day24) {
        this.day24.set(day24);
    }

    public String getDay25() {
        return day25.get();
    }

    public SimpleStringProperty day25Property() {
        return day25;
    }

    public void setDay25(String day25) {
        this.day25.set(day25);
    }

    public String getDay26() {
        return day26.get();
    }

    public SimpleStringProperty day26Property() {
        return day26;
    }

    public void setDay26(String day26) {
        this.day26.set(day26);
    }

    public String getDay27() {
        return day27.get();
    }

    public SimpleStringProperty day27Property() {
        return day27;
    }

    public void setDay27(String day27) {
        this.day27.set(day27);
    }

    public String getDay28() {
        return day28.get();
    }

    public SimpleStringProperty day28Property() {
        return day28;
    }

    public void setDay28(String day28) {
        this.day28.set(day28);
    }

    public String getDay29() {
        return day29.get();
    }

    public SimpleStringProperty day29Property() {
        return day29;
    }

    public void setDay29(String day29) {
        this.day29.set(day29);
    }

    public String getDay30() {
        return day30.get();
    }

    public SimpleStringProperty day30Property() {
        return day30;
    }

    public void setDay30(String day30) {
        this.day30.set(day30);
    }

    public String getDay31() {
        return day31.get();
    }

    public SimpleStringProperty day31Property() {
        return day31;
    }

    public void setDay31(String day31) {
        this.day31.set(day31);
    }
}
