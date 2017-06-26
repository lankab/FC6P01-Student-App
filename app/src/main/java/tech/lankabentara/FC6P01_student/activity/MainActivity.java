package tech.lankabentara.FC6P01_student.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.itemanimators.AlphaCrossFadeAnimator;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import tech.lankabentara.FC6P01_student.R;
import tech.lankabentara.FC6P01_student.TypeFaceUtil.TypeFaceUtil;
import tech.lankabentara.FC6P01_student.adapter.LecturesAdapter;
import tech.lankabentara.FC6P01_student.helper.DividerItemDecoration;
import tech.lankabentara.FC6P01_student.model.AttendanceRecord;
import tech.lankabentara.FC6P01_student.model.Lecture;
import tech.lankabentara.FC6P01_student.model.SignAttendance;
import tech.lankabentara.FC6P01_student.network.ApiClient;
import tech.lankabentara.FC6P01_student.network.ApiInterface;
import tech.lankabentara.FC6P01_student.sessionManager.SessionManager;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, LecturesAdapter.LectureAdapterListener, DatePickerDialog.OnDateSetListener {

    public static TextView emptyView, tvDate;
    public static SimpleDateFormat df;
    public static String outputDateStr;
    public static List<AttendanceRecord> AttendanceRecordData = new ArrayList<>();
    private List<AttendanceRecord> attendanceRecords = new ArrayList<>();
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    int Year, Month, Day;
    SessionManager manager;
    Intent intent = null;
    private List<Lecture> lectures = new ArrayList<>();
    private RecyclerView recyclerView;
    private LecturesAdapter mAdapter;
    private CoordinatorLayout coordinatorLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionMode actionMode;
    private ImageView leftNav, rightNav;
    private AccountHeader headerResult = null; //save our header or result
    private Drawer result = null;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TypeFaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Roboto-Regular.ttf"); // font from assets: "assets/fonts/Roboto-Regular.ttf

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        manager = new SessionManager();

        // Create a few sample profile
        final IProfile profile = new ProfileDrawerItem().withName("  "+SessionManager.sessionUserId).withIcon(R.drawable.profile);

        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(
                        profile
                )
                .withSavedInstance(savedInstanceState)
                .withSelectionListEnabledForSingleProfile(false)
                .build();

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withHasStableIds(true)
                .withItemAnimator(new AlphaCrossFadeAnimator())
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(

                        new PrimaryDrawerItem().withName(R.string.drawer_item_view_lectures_drawer).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(1),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_set_bluetooth).withIcon(R.drawable.ic_bluetooth_24dp).withIdentifier(2),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_logout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(3)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        if (drawerItem != null) {

                            if (drawerItem.getIdentifier() == 1) {

                            } else if (drawerItem != null && drawerItem.getIdentifier() == 2) {
                                bluetoothSnackBar();

                            } else if (drawerItem != null && drawerItem.getIdentifier() == 3) {
                                manager.setPreferences(MainActivity.this, "status", "0");
                                finish();
                                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(loginIntent);
                            }
                            if (intent != null) {
                                MainActivity.this.startActivity(intent);
                                intent = null;
                            }
                        }
                        return false;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withShowDrawerOnFirstLaunch(true)
                .build();

        // set the selection upon launch to the item with the identifier 1
        if (savedInstanceState == null) {
            result.setSelection(1, false);
        }

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        /**
         * Code block below converts formatted current tvDate value into a
         * simple yyy-MM-dd for fetching purposes with retrofit2
         * as well as calculating selected date's next day.
         */
        calendar = Calendar.getInstance();

        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);

        Calendar c = Calendar.getInstance();

        df = new SimpleDateFormat("dd-MM-yyyy");
        df.setTimeZone(java.util.TimeZone.getTimeZone("GMT+01:00"));
        String formattedDate = df.format(c.getTime());

        // formattedDate have current date/time
        Toast.makeText(this, formattedDate + " --" + c.getTime(), Toast.LENGTH_SHORT).show();

        tvDate = (TextView) findViewById(R.id.date); // => Defining toolbar selected date
        tvDate.setText(formattedDate); //=> Setting TextView to selected date

        String currentDate = tvDate.getText().toString();

        String datestr = currentDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = dateFormat.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertedDateStart = dateFormat2.format(date);


        leftNav = (ImageView) findViewById(R.id.left_nav);
        rightNav = (ImageView) findViewById(R.id.right_nav);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyView = (TextView) findViewById(R.id.empty_view);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new LecturesAdapter(this, lectures, this);
        getSelectedDateLectures(convertedDateStart, SessionManager.sessionUserId);
        getAttendanceRecords(convertedDateStart, SessionManager.sessionUserId);


        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigatePreviousDay();
            }
        });

        rightNav.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                navigateNextDay();
            }
        });

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkRecyclerViewIsEmpty();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                checkRecyclerViewIsEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkRecyclerViewIsEmpty();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        // show loader and fetch lectures
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {

                    }
                }
        );

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_date_picker) {
            datePickerDialog = DatePickerDialog.newInstance(MainActivity.this, Year, Month, Day);

            datePickerDialog.setThemeDark(false);

            datePickerDialog.showYearPickerFirst(false);

            datePickerDialog.setAccentColor(Color.parseColor("#4C0B5F"));

            datePickerDialog.setTitle("Select Date To View Lectures Attendance");

            datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * onDateSet() triggered by selected date from datePickerView runs
     * HTTP method to fetch lectures for selected date as well as
     * displaying formatted selected date in nav toolbar
     */
    @Override
    public void onDateSet(DatePickerDialog view, int Year, int Month, int Day) {

        // For some reason selected month's output is previous month
        Month += 1;

        // Selected date is formatted in the same manner as DB's date which will be compared to find a match.
        String date = Year + "-" + String.format("%02d", Month) + "-" + String.format("%02d", Day);
        String formattedDate = String.format("%02d", Year) + "-" + String.format("%02d", Month) + "-" + String.format("%02d", Day);

        swipeRefreshLayout.setRefreshing(true);

        //Converts selected date from datePickerView into required format
        try {
            DateFormat inputFormat = new SimpleDateFormat("yy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

            Date pickedDate = inputFormat.parse(formattedDate);
            outputDateStr = outputFormat.format(pickedDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Method below takes care of the networking aspect of retrieving lectures data for selected date dialog.
        getSelectedDateLectures(date, SessionManager.sessionUserId);
        getAttendanceRecords(date, SessionManager.sessionUserId);
        MainActivity.tvDate.setText(outputDateStr);//=> Setting TextView to selected date NOT CURRENTLY WORKING

        Toast.makeText(MainActivity.this, date, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the lectures again
        String currentDate = tvDate.getText().toString();

        // Code block below converts formatted current tvDate value into a
        // simple yyy-MM-dd for fetching purposes with retrofit2
        // as well as calculating selected date's next day.
        String datestr = currentDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = dateFormat.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertedDateStart = dateFormat2.format(date);

        // Fetches lectures for next day
        getSelectedDateLectures(convertedDateStart, SessionManager.sessionUserId);
        getAttendanceRecords(convertedDateStart, SessionManager.sessionUserId);
    }

    /**
     * Code block below converts formatted current tvDate value into a
     * simple yyy-MM-dd for fetching purposes with retrofit2
     * as well as calculating selected date's previous day.
     */
    private void navigatePreviousDay() {
        String currentDate = tvDate.getText().toString();

        String datestr = currentDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = dateFormat.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertedDateStart = dateFormat2.format(date);

        // Following code calculates selected date's previous day.
        SimpleDateFormat dateFormats = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormats.parse(convertedDateStart));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

        // Previous day is assigned to tomorrowAsString
        String previousDay = dFormat.format(yesterday);

        try {
            DateFormat inputFormat = new SimpleDateFormat("yy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date pickedDate = inputFormat.parse(previousDay);
            outputDateStr = outputFormat.format(pickedDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tvDate.setText(outputDateStr);
        tvDate.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_right));

        // Fetches lectures for next day
        getSelectedDateLectures(previousDay, SessionManager.sessionUserId);
        getAttendanceRecords(previousDay, SessionManager.sessionUserId);
    }

    /**
     * Code block below converts formatted current tvDate value into a
     * simple yyy-MM-dd for fetching purposes with retrofit2
     * as well as calculating selected date's next day.
     */
    private void navigateNextDay(){
        String currentDate = tvDate.getText().toString();

        String datestr = currentDate;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;
        try {
            date = dateFormat.parse(datestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String convertedDateStart = dateFormat2.format(date);

        // Following code calculates selected date's next day.
        SimpleDateFormat dateFormats = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(dateFormats.parse(convertedDateStart));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.add(Calendar.DATE, 1);
        Date tomorrow = cal.getTime();

        DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Next day is assigned to tomorrowAsString
        String nextDay = dFormat.format(tomorrow);

        try {
            DateFormat inputFormat = new SimpleDateFormat("yy-MM-dd");
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            //String inputDateStr="2013-06-24";
            Date pickedDate = inputFormat.parse(nextDay);
            outputDateStr = outputFormat.format(pickedDate);
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        tvDate.setText(outputDateStr);
        tvDate.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_left));
        //=> to be deleted Toast.makeText(MainActivity.this, "Next date clicked." +nextDay, Toast.LENGTH_LONG).show();

        // Fetches lectures for next day
        getSelectedDateLectures(nextDay, SessionManager.sessionUserId);
        getAttendanceRecords(nextDay, SessionManager.sessionUserId);
    }

    /**
     * Fetches selected lectures' date by making HTTP request
     * url: http://lankabentara.tech/FC6P01/android_sign_attendance/selected_date_lectures.php
     */
    private void getSelectedDateLectures(String selectedDate, String StudentId) {

        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<Lecture>> call = apiService.getSelectedDateLectures(selectedDate, StudentId);
        call.enqueue(new Callback<List<Lecture>>() {
            @Override
            public void onResponse(Call<List<Lecture>> call, Response<List<Lecture>> response) {

                // clear the inbox
                lectures.clear();

                swipeRefreshLayout.setRefreshing(false);


                // TODO - avoid looping
                // the loop was performed to add colors to each lecture
                for (Lecture lecture : response.body()) {

                    lectures.add(lecture);
                }
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            }

            @Override
            public void onFailure(Call<List<Lecture>> call, Throwable t) {
                internetSnackBar();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * Fetches selected lectures' date by making HTTP request
     * url: http://lankabentara.tech/FC6P01/android_sign_attendance/attendance_records.php
     */
    private void getAttendanceRecords(String selectedDate, String StudentId) {
        AttendanceRecordData.clear();
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        Call<List<AttendanceRecord>> call = apiService.getAttendanceRecords(selectedDate, StudentId);
        call.enqueue(new Callback<List<AttendanceRecord>>() {
            @Override
            public void onResponse(Call<List<AttendanceRecord>> call, Response<List<AttendanceRecord>> response) {


                swipeRefreshLayout.setRefreshing(false);

                AttendanceRecordData = response.body();

                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<List<AttendanceRecord>> call, Throwable t) {
                internetSnackBar();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    /**
     * Attempts to register student's lecture in progress
     * attendance upon all conditions/validations being met.
     */
    private void signAttendance(String id, String date, String lectureRoomId, String studentId, String bluetoothId, String moduleId) {

        ApiInterface signAttendanceApiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<SignAttendance> mService = signAttendanceApiService.signAttendance(id, date, lectureRoomId, studentId, bluetoothId, moduleId);
        mService.enqueue(new Callback<SignAttendance>() {
            @Override
            public void onResponse(Call<SignAttendance> call, Response<SignAttendance> response) {

                SignAttendance mSignAttendanceObject = response.body();
                String returnedResponse = mSignAttendanceObject.isSignedAttendance;

                //showProgress(false);
                if (returnedResponse.trim().equals("1")) {

                    Toast.makeText(MainActivity.this, "Successfully registered attendance - good job!", Toast.LENGTH_SHORT).show();

                }
                if (returnedResponse.trim().equals("2")) {

                    Toast.makeText(MainActivity.this, "You have already registered attendance for this lecture.", Toast.LENGTH_SHORT).show();
                }
                //Making Toast longer than LENGTH_LONG
                if (returnedResponse.trim().equals("3")) {
                    for (int i = 0; i < 2; i++) {
                        Toast.makeText(MainActivity.this, "You don't seem to be eligible to sign attendance. Make sure your phone's bluetooth adapter is on, although if this issue persists, please discuss this with your lecturer right after the lecture ends.", Toast.LENGTH_LONG).show();
                    }
                }
                if (returnedResponse.trim().equals("4")) {

                    Toast.makeText(MainActivity.this, "Failure to register attendance.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignAttendance> call, Throwable t) {
                call.cancel();
                Toast.makeText(MainActivity.this, "Please check your network connection and internet permission", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onIconClicked(int position) {
        final Lecture lecture = lectures.get(position);
        // Convert selected date into correct format(yyyy-MM-dd)
        String startDateString = lecture.getDate() + " " + lecture.getStartTime();
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
        Date date = new Date();
        DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dataFormat.setTimeZone(java.util.TimeZone.getTimeZone("Europe/London"));

        Date currDate = new Date();
        try {
            currDate = dataFormat.parse(dataFormat.format(date));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (startDate.before(currDate) & endDate.after(currDate)) {
            lectureSelection(position);
        } else {
            Toast.makeText(MainActivity.this, "Lecture is not currently in progress.", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onIconImportantClicked(int position) {
        // Star icon is clicked,
        // mark the lecture as important
        Lecture lecture = lectures.get(position);
        //  lecture.setImportant(!lecture.isImportant());
        lectures.set(position, lecture);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLectureRowClicked(int position) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated
        if (mAdapter.getSelectedItemCount() > 0) {
        } else {
            // read the lecture which removes bold from the row
            Lecture lecture = lectures.get(position);
            //  lecture.setRead(true);
            lectures.set(position, lecture);
            mAdapter.notifyDataSetChanged();

        }
    }

    private void lectureSelection(int position) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        final TextView mModuleCode = (TextView) mView.findViewById(R.id.tvEmail);
        final TextView mModuleTime = (TextView) mView.findViewById(R.id.tvPassword);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
        final Lecture lecture = lectures.get(position);

        mModuleCode.setText(lecture.getModuleId() + " -" + lecture.getTitle());
        mModuleTime.setText("Starts at: " + lecture.getStartTime() + "\nEnds   at: " + lecture.getEndTime());
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    // Device does not support Bluetooth
                } else {
                    if (!mBluetoothAdapter.isEnabled()) {
                        // Handle Bluetooth is not enable
                        bluetoothSnackBar();
                    } else {
                        String macAddress = android.provider.Settings.Secure.getString(MainActivity.this.getContentResolver(), "bluetooth_address");
                        signAttendance(lecture.getId(), lecture.getDate(), lecture.getRoomCode(), SessionManager.sessionUserId, macAddress, lecture.getModuleId());
                    }
                }

            }
        });
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page")
                // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();

    }

    private void checkRecyclerViewIsEmpty() {
        if (mAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void bluetoothSnackBar() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter.isEnabled()) {
            // Device is on
            // Bluetooth is not enable
            Snackbar bluetoothSnackbar = Snackbar
                    .make(coordinatorLayout, "Your bluetooth is on, would you like to turn it off?", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Manage", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                            intent.addCategory(Intent.CATEGORY_LAUNCHER);
                            ComponentName cn = new ComponentName("com.android.settings",
                                    "com.android.settings.bluetooth.BluetoothSettings");
                            intent.setComponent(cn);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    });

            bluetoothSnackbar.setActionTextColor(Color.BLUE);

            View sbView = bluetoothSnackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            bluetoothSnackbar.show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enable
                Snackbar bluetoothSnackbar = Snackbar
                        .make(coordinatorLayout, "Bluetooth needs to be on in order to register attendance.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Turn on", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                                ComponentName cn = new ComponentName("com.android.settings",
                                        "com.android.settings.bluetooth.BluetoothSettings");
                                intent.setComponent(cn);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });

                bluetoothSnackbar.setActionTextColor(Color.BLUE);

                View sbView = bluetoothSnackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.WHITE);
                bluetoothSnackbar.show();
            }
        }
    }

    private void internetSnackBar() {
        Snackbar internetSnackbar = Snackbar
                .make(coordinatorLayout, "No internet connection.", Snackbar.LENGTH_INDEFINITE)
                .setAction("Turn on", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(Intent.ACTION_MAIN, null);

                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                    }
                });

        internetSnackbar.setActionTextColor(Color.MAGENTA);

        View sbView = internetSnackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        internetSnackbar.show();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());

    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}