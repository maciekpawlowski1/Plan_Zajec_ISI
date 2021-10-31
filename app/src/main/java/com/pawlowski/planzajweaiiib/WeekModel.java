package com.pawlowski.planzajweaiiib;

public class WeekModel {
    private String monday;
    private String friday;
    private boolean downloaded;

    public WeekModel(String monday, String friday, boolean downloaded) {
        this.monday = monday;
        this.friday = friday;
        this.downloaded = downloaded;
    }

    public String getMonday() {
        return monday;
    }

    public void setMonday(String monday) {
        this.monday = monday;
    }

    public String getFriday() {
        return friday;
    }

    public void setFriday(String friday) {
        this.friday = friday;
    }

    public boolean isDownloaded() {
        return downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }
}
