package tech.lankabentara.FC6P01_student.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import tech.lankabentara.FC6P01_student.R;
import tech.lankabentara.FC6P01_student.sessionManager.SessionManager;

/**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class Splash extends AppCompatActivity {

    SessionManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        manager = new SessionManager();

        /****** Create Thread that will freeze the splash activity for 2 seconds before moving on *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 2 seconds
                    sleep(1 * 1000);

                    // After 5 seconds redirect to another intent
                    String status = manager.getPreferences(Splash.this, "status");
                    Log.d("status", status);
                    if (status.equals("1")) {
                        Intent i = new Intent(Splash.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        Intent i = new Intent(Splash.this, LoginActivity.class);
                        startActivity(i);
                    }


                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        System.exit(0);
    }

}
