package org.wpsim.PeasantFamily.Data;

public class TaskLog {

    String date;
    String task;

    @Override
    public String toString() {
        return "TaskLog{" +
                "  date='" + date + '\'' +
                ", task='" + task + '\'' +
                '}';
    }

    public TaskLog(String date, String task) {
        this.date = date;
        this.task = task;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

}
