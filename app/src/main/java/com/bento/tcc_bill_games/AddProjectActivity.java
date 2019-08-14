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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AddProjectActivity extends AppCompatActivity {

    private Button addImage;
    private ImageView projImage;
    private Button createProj;
    private EditText name;
    private EditText description;
    private Uri mSelectedUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        projImage = findViewById(R.id.imgAddProject);
        addImage = findViewById(R.id.btn_add_project_image);
        createProj = findViewById(R.id.btn_create_project);
        name = findViewById(R.id.etAddProjectName);
        description = findViewById(R.id.etProjectsDescription);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        createProj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProject();
            }
        });

    }

    private void createProject() {
       /* String uuid = FirebaseAuth.getInstance().getUid();
        String name = name.getText().toString();
        String description = description.getText().toString();


        User user = new User(uid, username, profile_url, name);

        FirebaseFirestore.getInstance().collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("Teste","registou");
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        })*/
    }

    private void selectPhoto() {
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
                Picasso.get().load(mSelectedUri).into(projImage);
                projImage.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
                addImage.getBackground().setAlpha(0);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
