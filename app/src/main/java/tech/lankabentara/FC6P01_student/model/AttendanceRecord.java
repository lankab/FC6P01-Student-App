package tech.lankabentara.FC6P01_student.model;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class AttendanceRecord {
    private String module_id;
    private String date;
    private String time;
    private String student_id;

    public AttendanceRecord() {
    }

    public String getModuleId() {
        return module_id;
    }

    public void setModuleId(String module_id) {
        this.module_id = module_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return time;
    }

    public void setStartTime(String time) {
        this.time = time;
    }

    public String getStudentId() {
        return student_id;
    }

    public void setStudentId(String student_id) {
        this.student_id = student_id;
    }

}
