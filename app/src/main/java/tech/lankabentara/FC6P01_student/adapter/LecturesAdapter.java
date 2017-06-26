package tech.lankabentara.FC6P01_student.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import tech.lankabentara.FC6P01_student.R;
import tech.lankabentara.FC6P01_student.activity.MainActivity;
import tech.lankabentara.FC6P01_student.model.AttendanceRecord;
import tech.lankabentara.FC6P01_student.model.Lecture;
import tech.lankabentara.FC6P01_student.sessionManager.SessionManager;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class LecturesAdapter extends RecyclerView.Adapter<LecturesAdapter.MyViewHolder> {
    private Context mContext;
    private static List<Lecture> lectures;
    private LectureAdapterListener listener;
    private SparseBooleanArray selectedItems;

    SessionManager manager;

    public static boolean noLecturesToday;
    public static int getItemSize;
    public static String date;

    public static AttendanceRecord attendanceRecord = new AttendanceRecord();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView from, subject, message, iconText, lecture_end, date;
        public ImageView iconImp, imgProfile;
        public LinearLayout lectureContainer;
        public RelativeLayout iconContainer, iconBack, iconFront;

        public MyViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);

            subject = (TextView) view.findViewById(R.id.lecture_start);
            lecture_end = (TextView) view.findViewById(R.id.lecture_end);
            message = (TextView) view.findViewById(R.id.txt_secondary);
            iconText = (TextView) view.findViewById(R.id.icon_text);
            date = (TextView) view.findViewById(R.id.date);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            iconFront = (RelativeLayout) view.findViewById(R.id.icon_front);
            iconImp = (ImageView) view.findViewById(R.id.icon_star);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            lectureContainer = (LinearLayout) view.findViewById(R.id.message_container);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
        }
    }

    public LecturesAdapter(Context mContext, List<Lecture> lectures, LectureAdapterListener listener) {
        this.mContext = mContext;
        this.lectures = lectures;
        this.listener = listener;
        selectedItems = new SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lecture_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Lecture lecture = lectures.get(position);

        // displaying text view data
        holder.from.setText(lecture.getTitle());
        holder.subject.setText("Starts at: " + lecture.getStartTime());
        holder.lecture_end.setText("Ends at:   " + lecture.getEndTime());
        holder.message.setText("Lecture Room: " + lecture.getRoomCode());

        date = lecture.getDate().toString();

        // displaying the first letter of From in icon text
        holder.iconText.setText(lecture.getModuleId());

        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));

        // handle is futureLecture
        futureLecture(holder, lecture, attendanceRecord);

        lectureIcon(holder);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.iconContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconClicked(position);
            }
        });

        holder.iconImp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onIconImportantClicked(position);
            }
        });

        holder.lectureContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLectureRowClicked(position);
            }
        });
    }

    private void lectureIcon(MyViewHolder holder) {

            holder.iconBack.setVisibility(View.GONE);
            holder.iconFront.setVisibility(View.VISIBLE);
            holder.iconFront.setAlpha(1);
        }

    private void futureLecture(MyViewHolder holder, Lecture lecture, AttendanceRecord attendanceRecord) {

        manager = new SessionManager();

        Date date = new Date();
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // Use London's time zone to format the date in
        dataFormat.setTimeZone(TimeZone.getTimeZone("Europe/London"));

        Date currDate = new Date();
        try {
            currDate = dataFormat.parse(dataFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Convert selected date into correct format(yyyy-MM-dd)
        String startDateString = lecture.getDate() + " " + lecture.getStartTime();
        String startDateString1 = lecture.getDate();
        String endDateString = lecture.getDate() + " " + lecture.getEndTime();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date startDate = new Date();
        Date endDate = new Date();
        try {
            startDate = df.parse(startDateString);
            endDate = df.parse(endDateString);

            // String newDateString = df.format(startDate);
            //System.out.println(newDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (startDate.after(currDate)) {

            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_future_lecture_24dp));

        } else if (startDate.before(currDate) & endDate.after(currDate)) {

            holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_lecture_in_progress_24dp));

        } else if (startDate.before(currDate) & endDate.before(currDate)) {

            boolean isAttended = false;
            for (int i = 0; i < MainActivity.AttendanceRecordData.size(); i++) {

                String signedDate =  MainActivity.AttendanceRecordData.get(i).getDate()+ " " + MainActivity.AttendanceRecordData.get(i).getStartTime();

                DateFormat df_signed = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                Date signedDateFormat = new Date();
                try {
                    signedDateFormat = df_signed.parse(signedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (MainActivity.AttendanceRecordData.get(i).getDate().equals(startDateString1) & (startDate.before(signedDateFormat) & endDate.after(signedDateFormat)) & MainActivity.AttendanceRecordData.get(i).getStudentId().equals(SessionManager.sessionUserId)) {
                    isAttended = true;

                } else if (!MainActivity.AttendanceRecordData.get(i).getDate().equals(startDateString1) & !MainActivity.AttendanceRecordData.get(i).getStartTime().equals(lecture.getStartTime()) & !MainActivity.AttendanceRecordData.get(i).getStudentId().equals(SessionManager.sessionUserId)) {
                    isAttended = false;
                }
            }
            if (isAttended) {
                holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_attended_24dp));

            } else if (isAttended == false) {
                holder.iconImp.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_absent_24dp));
            }
        }
    }

    @Override
    public int getItemCount() {
        getItemSize = lectures.size();
        return lectures.size();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items =
                new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public interface LectureAdapterListener {
        void onIconClicked(int position);

        void onIconImportantClicked(int position);

        void onLectureRowClicked(int position);
    }

}