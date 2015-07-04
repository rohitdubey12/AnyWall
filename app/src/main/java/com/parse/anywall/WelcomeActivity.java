package com.parse.anywall;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Activity which displays a registration screen to the user.
 */
public class WelcomeActivity extends Activity {

  private Dialog progressDialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    Button loginButton = (Button) findViewById(R.id.login_button);
    loginButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Starts an intent of the log in activity
        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
      }
    });

    // Sign up button click handler
    Button signupButton = (Button) findViewById(R.id.signup_button);
    signupButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        // Starts an intent for the sign up activity
        startActivity(new Intent(WelcomeActivity.this, SignUpActivity.class));
      }
    });
  }



  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
  }

  public void onLoginClick(View v) {
    progressDialog = ProgressDialog.show(WelcomeActivity.this, "", "Logging you in...", true);

    List<String> permissions = Arrays.asList("public_profile", "email");
    // NOTE: for extended permissions, like "user_about_me", your app must be reviewed by the Facebook team
    // (https://developers.facebook.com/docs/facebook-login/permissions/)

    ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {

      @Override
      public void done(ParseUser user, ParseException err) {
        Log.v("FIN", "CALL BACK RECEVIED");
        progressDialog.dismiss();
        if (user == null) {
          Log.d("FIN", "Uh oh. The user cancelled the Facebook login.");
        } else if (user.isNew()) {
          Log.d("FIN", "User signed up and logged in through Facebook! "+user.getUsername()+user.getEmail()+user.getObjectId());
          Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
          startActivity(intent);

        } else {
          Log.d("FIN", "User logged in through Facebook! "+user.getUsername()+user.getEmail()+user.getObjectId());
          Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
          startActivity(intent);
        }
      }
    });
  }


}
