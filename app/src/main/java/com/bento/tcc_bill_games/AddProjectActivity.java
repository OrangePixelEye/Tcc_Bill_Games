package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class AddProjectActivity extends AppCompatActivity {

    private Button addImage;
    private ImageView projImage;
    private Button createProj;
    private EditText Name;
    private EditText Description;
    private Uri mSelectedUri;
    private Spinner sp;
    private TextView txt;
    private Button back;
    private int a;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        LoginUser();

        final Resources res = getResources();
        projImage = findViewById(R.id.imgAddProject);
        addImage = findViewById(R.id.btn_add_project_image);
        createProj = findViewById(R.id.btn_create_project);
        Name = findViewById(R.id.etAddProjectName);
        Description = findViewById(R.id.etProjectsDescription);
        sp = findViewById(R.id.sp_add_projects);
        txt = findViewById(R.id.txtErrorAddProject);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        back = findViewById(R.id.btn_add_project_back);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.games_styles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createProj.setVisibility(View.VISIBLE);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                final Resources r = getResources();
                txt.setText(r.getString(R.string.add_projects_add_category));

            }
        });


        createProj.setVisibility(View.INVISIBLE);
        createProj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProject();
                txt.setText(res.getString(R.string.error_register_loading));
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddProjectActivity.this, ProjectActivity.class);
                intent.putExtra("user",  user);
                startActivity(intent);
            }
        });

    }

    private void createProject() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(mSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String project_id = UUID.randomUUID().toString();
                        String uuid = FirebaseAuth.getInstance().getUid();
                        String name = Name.getText().toString();
                        String description = Description.getText().toString();
                        String gd = String.valueOf(sp.getSelectedItemPosition());
                        String profile_url = uri.toString();

                        Project proj = new Project(gd, project_id, uuid, name, description, profile_url);


                        FirebaseFirestore.getInstance().collection("projects").add(proj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.i("Teste", "registou");
                                Intent intent = new Intent(AddProjectActivity.this, ProjectActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Teste", e.getMessage(), e);
            }
        });
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
    private void LoginUser() {
        String doc = FirebaseAuth.getInstance().getUid();
        if (doc != null) {
            FirebaseFirestore.getInstance().collection("users").document(doc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        user = documentSnapshot.toObject(User.class);
                    } else {
                        Intent intent = new Intent(AddProjectActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }


}
