package com.bento.tcc_bill_games;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class UserActivity extends AppCompatActivity {

    private TextView txtAreaUser;
    private TextView txtTelefoneUser;
    private TextView txtCidadeUser;
    private TextView txtEmailUser;
    private EditText edtxtNameUser;
    private ImageView imgUser;
    private EditText edtxtUserameUser;
    private Button btn_change_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        txtAreaUser = findViewById(R.id.txtAreaUser);
        txtTelefoneUser = findViewById(R.id.txtTelefoneUser);
        txtCidadeUser = findViewById(R.id.txtCidadeUser);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        edtxtNameUser = findViewById(R.id.edtxtNameUser);
        imgUser = findViewById(R.id.imageviewUser);
        edtxtUserameUser = findViewById(R.id.edtxtUsernameUser);
        btn_change_photo = findViewById(R.id.btn_change_photo);
        configureScreen();

        btn_change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void configureScreen() {

    }
}
