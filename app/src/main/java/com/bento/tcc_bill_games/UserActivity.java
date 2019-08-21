package com.bento.tcc_bill_games;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;

public class UserActivity extends AppCompatActivity {

    private TextView txtAreaUser;
    private TextView txtTelefoneUser;
    private TextView txtCidadeUser;
    private TextView txtEmailUser;
    private EditText edtxtNameUser;
    private ImageView imgUser;
    private Uri mSelectedUri;
    private EditText edtxtUserameUser;
    private Button btn_change_photo;
    private Button update_profile;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //UI references
        txtAreaUser = findViewById(R.id.txtAreaUser);
        txtTelefoneUser = findViewById(R.id.txtTelefoneUser);
        txtCidadeUser = findViewById(R.id.txtCidadeUser);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        edtxtNameUser = findViewById(R.id.edtxtNameUser);
        imgUser = findViewById(R.id.imageviewUser);
        edtxtUserameUser = findViewById(R.id.edtxtUsernameUser);
        btn_change_photo = findViewById(R.id.btn_change_photo);
        update_profile = findViewById(R.id.btn_update_profile);
        update_profile.setVisibility(View.GONE);
        btn_change_photo.setVisibility(View.INVISIBLE);
        back = findViewById(R.id.btn_useractivity_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        configureScreen();

        //Button events
        btn_change_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhoto();

            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserProfile();
            }
        });

    }

    private void UpdateUserProfile() {

    }

    private void updatePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            Log.i("Teste", "até aqui bl");
            mSelectedUri = data.getData();
            Bitmap bitmap = null;
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                Log.i("Teste", "pegou a foto");
                Picasso.get().load(mSelectedUri).into(imgUser);
                imgUser.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
                update_profile.setVisibility(View.VISIBLE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void configureScreen() {
        Bundle extras = getIntent().getExtras();
        User user;
        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
            if(user.getName().equals("")) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                edtxtNameUser.setText(user.getName());
                edtxtUserameUser.setText(user.getUsername());
                Picasso.get().load(user.getProfile_url()).into(imgUser);

            }
        }else{
            Log.i("Teste","erro ao mandar a classe usuario");
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
}
