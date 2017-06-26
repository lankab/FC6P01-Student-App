package tech.lankabentara.FC6P01_student.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import tech.lankabentara.FC6P01_student.R;
import tech.lankabentara.FC6P01_student.constants.Constants;
import tech.lankabentara.FC6P01_student.model.Login;
import tech.lankabentara.FC6P01_student.network.ApiInterface;
import tech.lankabentara.FC6P01_student.sessionManager.SessionManager;
import tech.lankabentara.FC6P01_student.utils.FontManager;

 /**
 * Created by Lanka Bentara
 * www.lankabentara.tech
 */

public class LoginActivity extends AppCompatActivity {
    public static final String BASE_URL = "http://lankabentara.tech/FC6P01/android_sign_attendance/api/";
    private final String TAG = "LoginActivity";
    SessionManager manager;

    FontManager FM;
    EditText etUsername, etPassword;
    LinearLayout signinLayout;
    TextView signInbtn;
    TextView notRegistered;
    TextView usernameicon, passwordicon;
    ProgressBar mProgressView;
    TextView failedLoginMessage;
    View focusView = null;
    private View mLoginFormView;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Assign instantiate SessionManager instance.
        manager = new SessionManager();

        FM = new FontManager(getApplicationContext());
        InitUI();

        signinLayout = (LinearLayout) findViewById(R.id.signinLayout);
        signinLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                attemptLogin();
            }
        });

        notRegistered = (TextView) findViewById(R.id.notRegistered);
        notRegistered.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, R.string.not_registered, Toast.LENGTH_LONG).show();
            }
        });
    }

     /**
      *Initialises UI components. It is indeed inconsistent with the other activities' UI initialisation.
      *This is due to wanting to try a different way to initialise UI components.
      */
    public void InitUI() {

        etUsername = (EditText) findViewById(R.id.usernameedt);
        etPassword = (EditText) findViewById(R.id.passwordedt);
        signInbtn = (TextView) findViewById(R.id.signinbtn);
        notRegistered = (TextView) findViewById(R.id.notRegistered);
        usernameicon = (TextView) findViewById(R.id.usernameicon);
        passwordicon = (TextView) findViewById(R.id.passwordicon);
        FM.setAppMedium(etUsername);
        FM.setAppMedium(etPassword);
        FM.setAppMedium(etPassword);
        FM.setAppRegular(signInbtn);
        FM.setAppMedium(notRegistered);
        setUsernameIcon(usernameicon);
        setPasswordIcon(passwordicon);
    }

     /**
      * Contains method to make network request in case of validations being met
      */
    private void attemptLogin() {
        boolean mCancel = this.loginValidation();
        if (mCancel) {
            focusView.requestFocus();
        } else {
            loginProcessWithRetrofit(email, password);
        }
    }

     /**
      * Runs all validation conditions before attempting to make any network requests
      */
    private boolean loginValidation() {
        // Reset errors.
        etUsername.setError(null);
        etPassword.setError(null);

        // Store values at the time of the login attempt.
        email = etUsername.getText().toString();
        password = etPassword.getText().toString();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            etPassword.setError(getString(R.string.error_invalid_password));
            focusView = etPassword;
            cancel = true;
        }

        // Check for a valid userID
        if (TextUtils.isEmpty(email)) {
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getString(R.string.error_field_required));
            focusView = etPassword;
            cancel = true;
        }

        if ((TextUtils.isEmpty(email)) && (TextUtils.isEmpty(password))) {
            etUsername.setError(getString(R.string.error_field_required));
            focusView = etUsername;
            cancel = true;
        }
        return cancel;
    }

     /**
      * Checks that password length is correct
      */
    private boolean isPasswordValid(String password) {
        return password.length() > 0;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private ApiInterface getInterfaceService() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final ApiInterface mInterfaceService = retrofit.create(ApiInterface.class);
        return mInterfaceService;
    }

     /**
      *Contains all necessary code in order to make network request, using model=>login & apiInterface
      */
    private void loginProcessWithRetrofit(final String email, String password) {

        ApiInterface mApiService = this.getInterfaceService();
        Call<Login> mService = mApiService.authenticate(email, password);
        mService.enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {

                Login mLoginObject = response.body();
                String returnedResponse = mLoginObject.isLogin;

                if (returnedResponse.trim().equals("1")) {

                    // Pass logged-in status to shared preferences in order start user's session
                    manager.setPreferences(LoginActivity.this, "1", email);
                    String status = manager.getPreferences(LoginActivity.this, Constants.KEY_SESSION_STATUS);
                    Log.d(Constants.KEY_SESSION_STATUS, status);

                    // redirect to Main Activity page
                    Intent loginIntent = new Intent(LoginActivity.this, MainActivity.class);
                    loginIntent.putExtra("EMAIL", email);
                    startActivity(loginIntent);
                }
                if (returnedResponse.trim().equals("0")) {
                    // Toast msg will indicate what needs to be done if login credentials are unknown to students.
                    Toast.makeText(LoginActivity.this, R.string.login_failure, Toast.LENGTH_LONG).show();
                    etPassword.requestFocus();
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                call.cancel();
                Toast.makeText(LoginActivity.this, "Please check your network connection and internet permission", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);

    }

     /**
      * Displays user icon left-side of username TextView
      */
    public void setUsernameIcon(TextView tv) {

        Typeface font = Typeface.createFromAsset(LoginActivity.this.getAssets(), "FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(LoginActivity.this.getString(R.string.fa_user));
    }

     /**
      * Displays lock icon left-side of password TextView
      */
    public void setPasswordIcon(TextView tv) {

        Typeface font = Typeface.createFromAsset(LoginActivity.this.getAssets(), "FontAwesome.otf");
        tv.setTypeface(font);
        tv.setText(LoginActivity.this.getString(R.string.fa_lock));
    }

}
