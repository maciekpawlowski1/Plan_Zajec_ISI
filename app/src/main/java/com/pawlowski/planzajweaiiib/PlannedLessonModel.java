package com.pawlowski.planzajweaiiib;

import com.pawlowski.planzajweaiiib.day.LessonModel;

public class PlannedLessonModel extends LessonModel {
    public PlannedLessonModel(String tittle, String room, String time, String teacher) {
        super(tittle, room, time, teacher);
    }
    int dayOfWeek;
    int numberOfTimes;

    int id;

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }


    public int getNumberOfTimes() {
        return numberOfTimes;
    }

    public void setNumberOfTimes(int numberOfTimes) {
        this.numberOfTimes = numberOfTimes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
