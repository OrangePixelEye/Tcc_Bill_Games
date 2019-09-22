package com.bento.tcc_bill_games;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.String;

public class LoginActivity extends AppCompatActivity {
    //UI references
    private EditText email;
    private EditText password;
    private Button btn_login;
    private TextView register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //UI layout references
        email = findViewById(R.id.edit_email_register);
        password = findViewById(R.id.edit_password_register);
        btn_login = findViewById(R.id.btn_register);
        register = findViewById(R.id.textview_register_login);

        //On click event for button
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //variables for verification
                String temail = email.getText().toString();
                String tpassword = password.getText().toString();

                //if for email validation
                if(temail.isEmpty() || temail == null || tpassword.isEmpty() || tpassword == null){
                    Toast.makeText(LoginActivity.this, "O email e senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                //creating a email and password for login
                FirebaseAuth.getInstance().signInWithEmailAndPassword(temail,tpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Event activated when user click in the register text
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}