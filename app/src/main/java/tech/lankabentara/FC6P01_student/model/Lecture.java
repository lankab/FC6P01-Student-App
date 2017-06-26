package tech.lankabentara.FC6P01_student.model;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class Lecture {
    private String id;
    private String module_id;
    private String title;
    private String date;
    private String time;
    private String end_time;
    private String code;

    public Lecture() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleId() {
        return module_id;
    }

    public void setModuleId(String module_id) {
        this.module_id = module_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartTime() {
        return time;
    }

    public void setStartTime(String time) {
        this.time = time;
    }

    public String getEndTime() {
        return end_time;
    }

    public void setEndTime(String end_time) {
        this.end_time = end_time;
    }

    public String getRoomCode() {
        return code;
    }

    public void setRoomCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }



}
