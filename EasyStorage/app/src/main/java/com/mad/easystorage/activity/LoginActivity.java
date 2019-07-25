package com.mad.easystorage.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.mad.easystorage.application.MyApplication;
import com.mad.easystorage.R;
import com.mad.easystorage.constants.Constants;

/**
 * When user doesn't log in, this activity will handle login and register function.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mPassEt;
    private EditText mEmailEt;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(Constants.LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyApplication.getInstance().addActivity(this);
        mAuth = FirebaseAuth.getInstance();
        updateStatus();
        // Set up click handlers and view item references
        findViewById(R.id.button_login).setOnClickListener(this);
        findViewById(R.id.button_register).setOnClickListener(this);
        mEmailEt = (EditText) findViewById(R.id.et_email);
        mPassEt = (EditText) findViewById(R.id.et_password);
    }

    /**
     * handle when user click on login button and register button
     *
     * @param v parse in current view
     */
    @Override
    public void onClick(View v) {
        Log.i(Constants.LOGTAG, "onClick");
        switch (v.getId()) {
            case R.id.button_login:
                Log.i(Constants.LOGTAG, "onClick, loginBtn");
                signUserIn();
                break;

            case R.id.button_register:
                Log.i(Constants.LOGTAG, "onClick, registerBtn");
                createUserAccount();
                break;
        }
    }

    /**
     * Check if the form field is isEmpty
     * @return
     */
    private boolean checkFormFields() {
        Log.i(Constants.LOGTAG, "checkFormFields");
        String email, password;

        email = mEmailEt.getText().toString();
        password = mPassEt.getText().toString();

        if (email.isEmpty()) {
            Log.i(Constants.LOGTAG, "checkFormFields, emailEmpty");
            mEmailEt.setError(getString(R.string.email_required));
            return false;
        }
        if (password.isEmpty()) {
            Log.i(Constants.LOGTAG, "checkFormFields, passwordEmpty");
            mPassEt.setError(getString(R.string.password_required));
            return false;
        }

        return true;
    }

    /**
     * update the status of user sign in
     */
    private void updateStatus() {
        Log.i(Constants.LOGTAG, "updateStatus");
        TextView tvStat = (TextView) findViewById(R.id.tv_status);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvStat.setText(getString(R.string.signed_in_with_colon) + user.getEmail());
        } else {
            tvStat.setText(R.string.signed_out);
        }
    }

    /**
     * update tvStat status with perameter String stat
     * @param stat
     */
    private void updateStatus(String stat) {
        Log.i(Constants.LOGTAG, "updateStatus");
        TextView tvStat = (TextView) findViewById(R.id.tv_status);
        tvStat.setText(stat);
    }

    /**
     * sign user in
     */
    private void signUserIn() {
        Log.i(Constants.LOGTAG, "signUserIn");
        if (!checkFormFields())
            return;

        String email = mEmailEt.getText().toString();
        String password = mPassEt.getText().toString();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.i(Constants.LOGTAG, "signUserIn, signInSuccessfully");
                                    Toast.makeText(LoginActivity.this, R.string.signed_in, Toast.LENGTH_SHORT)
                                            .show();
                                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivityForResult(myIntent, Constants.MAINACTIVITY_REQUEST);
                                } else {
                                    Log.i(Constants.LOGTAG, "signUserIn, signInFailed, networkError");
                                    Toast.makeText(LoginActivity.this, R.string.sign_in_failed, Toast.LENGTH_SHORT)
                                            .show();
                                }

                                updateStatus();
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            Log.i(Constants.LOGTAG, "signUserIn, signInFailed, invalidPassword");
                            updateStatus(getString(R.string.invalid_password));
                        } else if (e instanceof FirebaseAuthInvalidUserException) {
                            Log.i(Constants.LOGTAG, "signUserIn, signInFailed, countDoesntExist");
                            updateStatus(getString(R.string.no_account));
                        } else {
                            updateStatus(e.getLocalizedMessage());
                        }
                    }
                });
    }

    /**
     * create new account for the user
     */
    private void createUserAccount() {
        Log.i(Constants.LOGTAG, "createUserAccount");
        if (!checkFormFields()) return;
        String email = mEmailEt.getText().toString();
        String password = mPassEt.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.i(Constants.LOGTAG, "createUserAccount, userCreated");
                                    Toast.makeText(LoginActivity.this, R.string.user_created, Toast.LENGTH_SHORT)
                                            .show();
                                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivityForResult(myIntent, Constants.MAINACTIVITY_REQUEST);
                                } else {
                                    Log.i(Constants.LOGTAG, "createUserAccount, accountCreationFailed");
                                    Toast.makeText(LoginActivity.this, R.string.account_creation_failed, Toast.LENGTH_SHORT)
                                            .show();
                                }
                            }
                        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Log.i(Constants.LOGTAG, "createUserAccount, accountCreationFailed, emailAlreadyInUse");
                            updateStatus(getString(R.string.email_already_in_use));
                        } else {
                            Log.i(Constants.LOGTAG, "createUserAccount, accountCreationFailed, otherException");
                            updateStatus(e.getLocalizedMessage());
                        }
                    }
                });
    }
}
