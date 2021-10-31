package com.pawlowski.planzajweaiiib.day;

public class SavedLessonModel extends LessonModel{

    public SavedLessonModel(String tittle, String room, String time, String teacher) {
        super(tittle, room, time, teacher);
    }

    public SavedLessonModel(String tittle, String room, String time, String teacher, String fullInfo) {
        super(tittle, room, time, teacher, fullInfo);
    }


    int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
