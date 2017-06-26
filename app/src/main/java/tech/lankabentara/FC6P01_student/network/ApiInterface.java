package tech.lankabentara.FC6P01_student.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import tech.lankabentara.FC6P01_student.model.AttendanceRecord;
import tech.lankabentara.FC6P01_student.model.Lecture;
import tech.lankabentara.FC6P01_student.model.Login;
import tech.lankabentara.FC6P01_student.model.SignAttendance;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public interface ApiInterface {

    // HTTP OPERATION => Fetch attendance records of specific student_id for set date.
    @GET("attendance_records.php")
    Call<List<AttendanceRecord>> getAttendanceRecords(@Query("date") String date, @Query("student_id") String student_id);

    // HTTP OPERATION => Fetch selected date's lectures
    @GET("selected_date_lectures.php")
    Call<List<Lecture>> getSelectedDateLectures(@Query("date") String attendanceDate, @Query("student_id") String attendanceStudent_id);

    /* HTTP OPERATION => Compare credential details to the one stored in DB.
       Upon match allow users to access all activities. */
    @GET("index.php/{email}/{password}")
    Call<Login> authenticate(@Path("email") String email, @Path("password") String password);

    // HTTP OPERATION => Insert student's attendance for selected lecture in progress if all conditions are met.
    @FormUrlEncoded
    @POST("sign_attendance.php")
    Call<SignAttendance> signAttendance(@Field("id") String id,
                                        @Field("date") String date,
                                        @Field("lectureRoomId") String lectureRoomId,
                                        @Field("studentId") String studentId,
                                        @Field("bluetoothId") String bluetoothId,
                                        @Field("moduleId") String moduleId);

}
