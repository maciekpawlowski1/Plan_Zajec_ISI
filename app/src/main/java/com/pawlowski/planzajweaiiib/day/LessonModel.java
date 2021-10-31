package com.pawlowski.planzajweaiiib.day;

import android.util.Log;

import com.pawlowski.planzajweaiiib.api.LessonResponseModel;

import java.util.Locale;
import java.util.Objects;

public class LessonModel {
    private String tittle;
    private String room;
    private String time;
    private String teacher;
    private String fullInfo;
    private String day;
    private String group;

    private LessonModel() {
    }

    public LessonModel(String tittle, String room, String time, String teacher) {
        this.tittle = tittle;
        this.room = room;
        this.time = time;
        this.teacher = teacher;
    }

    public LessonModel(String tittle, String room, String time, String teacher, String fullInfo) {
        this.tittle = tittle;
        this.room = room;
        this.time = time;
        this.teacher = teacher;
        this.fullInfo = fullInfo;
    }



    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getFullInfo() {
        return fullInfo;
    }

    public void setFullInfo(String fullInfo) {
        this.fullInfo = fullInfo;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public static LessonModel parseFromLessonResponseModel(LessonResponseModel lessonResponseModel)
    {
        LessonModel lessonModel = new LessonModel();
        String fullInfo = lessonResponseModel.getTitle();
        lessonModel.setFullInfo(fullInfo.replace("<br/>", "\n"));
        int first = fullInfo.indexOf(',');
        String tittle = "";
        if(first != -1)
        {
            tittle = lessonResponseModel.getTitle().substring(0, first);
            lessonModel.setTittle(tittle);
        }




        int teacherStartIndex = fullInfo.indexOf("prowadzący:") + 12;
        int teacherEndIndex = fullInfo.length();//fullInfo.indexOf(",", teacherStartIndex+1);
        String teacher = "";
        if(teacherStartIndex != (-1 + 12) && teacherEndIndex != -1)
        {
            teacher = fullInfo.substring(teacherStartIndex, teacherEndIndex);
            teacher = teacher.replace("<br/>", "\n");
        }
        lessonModel.setTeacher(teacher);

        String room = "";
        int roomStartIndex = fullInfo.toLowerCase().indexOf("sala:") + 6;
        if(roomStartIndex != (-1 + 6))
        {
            int roomEndIndex = fullInfo.indexOf(',', roomStartIndex);
            if(roomEndIndex != -1)
            {
                room = fullInfo.substring(roomStartIndex, roomEndIndex);
            }
        }
        if(fullInfo.contains("online"))
        {
            room = "online";
        }
        lessonModel.setRoom(room);

        if(teacher.length() == 0 || room.length() == 0 || tittle.length() == 0)
        {
            lessonModel.setTeacher("");
            lessonModel.setRoom("");
            lessonModel.setTittle(fullInfo.replace("<br/>", "\n"));
        }

        String day = lessonResponseModel.getStart().substring(0, 10);
        String start = lessonResponseModel.getStart().substring(11, 16);
        String end = lessonResponseModel.getEnd().substring(11, 16);

        lessonModel.setTime(start + " - " + end);
        lessonModel.setDay(day);


        lessonModel.setGroup(getGroupString(lessonResponseModel.getGroup()));


        return lessonModel;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LessonModel that = (LessonModel) o;
        return Objects.equals(tittle, that.tittle) && Objects.equals(room, that.room) && Objects.equals(time, that.time) && Objects.equals(teacher, that.teacher) && Objects.equals(fullInfo, that.fullInfo) && Objects.equals(day, that.day) && Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tittle, room, time, teacher, fullInfo, day, group);
    }

    public static String getGroupString(float group)
    {
        if(Float.compare(group, 1.1f) == 0)
        {
            return "1a";
        }
        else if(Float.compare(group, 1.2f) == 0)
        {
            return "1b";
        }
        else if(Float.compare(group, 2.1f) == 0)
        {
            return "2a";
        }
        else if(Float.compare(group, 2.2f) == 0)
        {
            return "2b";
        }
        else if(Float.compare(group, 3.1f) == 0)
        {
            return "3a";
        }
        else if(Float.compare(group, 3.2f) == 0)
        {
            return "3b";
        }
        else if(Float.compare(group, 4.1f) == 0)
        {
            return "4a";
        }
        else if(Float.compare(group, 0) == 0)
        {
            return "wykład";
        }
        else
        {
            return (int)group+"";
        }

    }
}
