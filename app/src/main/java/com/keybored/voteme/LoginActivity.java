package com.keybored.voteme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String tag = "auth";
    private EditText emailField, passwordField;
    private ProgressDialog pDialog;

    private LinearLayout loginFields, loginButtons, signedInButtons;
    private TextView status;
    private CoordinatorLayout parentView;

    private FirebaseAuth mAuth;
    private Button signIn, signOut, createAccount, verify, goToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Input fields initialized
        emailField = (EditText)findViewById(R.id.username_input);
        passwordField = (EditText)findViewById(R.id.password_input);

        //Progress dialog for UX
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        //Buttons initialized
        status = (TextView)findViewById(R.id.status);
        signIn = (Button)findViewById(R.id.email_sign_in);
        signIn.setOnClickListener(this);
        createAccount = (Button)findViewById(R.id.create_account);
        createAccount.setOnClickListener(this);
        signOut = (Button)findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(this);
        verify = (Button)findViewById(R.id.verify_email);
        verify.setOnClickListener(this);
        goToMain = (Button)findViewById(R.id.goToMain);
        goToMain.setOnClickListener(this);

        //Layouts initialized
        loginFields = (LinearLayout)findViewById(R.id.login_input);
        loginButtons = (LinearLayout)findViewById(R.id.email_password_buttons);
        signedInButtons = (LinearLayout)findViewById(R.id.signed_in_buttons);
        parentView = (CoordinatorLayout)findViewById(R.id.login_activity_view);


        //Initialize authorization
        mAuth = FirebaseAuth.getInstance();

    }

    private void showDialog(){
        if(!pDialog.isShowing()) pDialog.show();
    }

    private void hideDialog(){
        if(pDialog.isShowing()) pDialog.dismiss();
    }

    private boolean validate(){
        boolean valid = true;

        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)){
            Snackbar.make(parentView, "Empty email", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }

        String password = passwordField.getText().toString();
        if(TextUtils.isEmpty(password)){
            Snackbar.make(parentView, "Empty password", Snackbar.LENGTH_SHORT).show();
            valid = false;
        }
        return valid;
    }


    private void createAccount(String email, String password){
        Log.i(tag, "email: " + email);
        if(!validate()) return;

        showDialog();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.i(tag, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Log.w(tag, "createUserWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
        hideDialog();
    }

    private void signIn(String email, String password){
        Log.i(tag, "signIn: " + email);
        if(!validate()) return;

        showDialog();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(tag, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Snackbar.make(parentView, "You have Logged In!", Snackbar.LENGTH_SHORT).show();
                            updateUI(user);
                        }else{
                            Log.w(tag, "signInWithEmail:failure",task.getException());
                            Snackbar.make(parentView, "Failed Login: Invalid email/password", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
        hideDialog();
    }

    private void signOut(){
        showDialog();
        mAuth.signOut();
        updateUI(null);
        hideDialog();
    }

    private void sendEmailVerification(){
        //disable button
        verify.setEnabled(false);

        showDialog();
        //Send verification email
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        verify.setEnabled(true);

                        if(task.isSuccessful()){
                            Snackbar.make(parentView, "Verification email sent to " + user.getEmail(), Snackbar.LENGTH_SHORT).show();
                        }else{
                            Log.e(tag, "sendEmailVerification",task.getException());
                            Snackbar.make(parentView, "Failed to send email", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        hideDialog();
    }


    @Override
    public void onClick(View v){
        int i = v.getId();
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        switch (i){
            case R.id.email_sign_in:
                signIn(email, password);
                break;
            case R.id.create_account:
                createAccount(email, password);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.verify_email:
                sendEmailVerification();
                break;
            case R.id.goToMain:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }

    private void updateUI(FirebaseUser user){
        if(user!=null){
            status.setText(R.string.logged_in);
            loginButtons.setVisibility(View.GONE);
            loginFields.setVisibility(View.GONE);
            signedInButtons.setVisibility(View.VISIBLE);
        }else {
            status.setText(R.string.log_in);
            loginButtons.setVisibility(View.VISIBLE);
            loginFields.setVisibility(View.VISIBLE);
            signedInButtons.setVisibility(View.GONE);
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        //Check if user is signed in and update UI accordingly
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        startActivity(new Intent(this, MainActivity.class));

    }

}
