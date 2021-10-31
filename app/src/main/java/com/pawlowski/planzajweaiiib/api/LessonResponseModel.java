package com.pawlowski.planzajweaiiib.api;

public class LessonResponseModel {
    private String title;
    private String start;
    private String end;
    private int eid;
    private float group;
    private String backgroundColor;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public float getGroup() {
        return group;
    }

    public void setGroup(float group) {
        this.group = group;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public String toString() {
        return "LessonModel{" +
                "tittle='" + title + '\'' +
                ", start='" + start + '\'' +
                ", end='" + end + '\'' +
                ", eid=" + eid +
                ", group=" + group +
                ", backgroundColor='" + backgroundColor + '\'' +
                '}';
    }
}
