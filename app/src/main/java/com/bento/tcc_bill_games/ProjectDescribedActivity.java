package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProjectDescribedActivity extends AppCompatActivity {
    private Button back;

    private Button p_button;
    private ImageView imgProj;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtCategory;
    Boolean logic;//true in the main's case and false in the projectActivity's case
    User user;
    Project project;
    Boolean is_leader = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_described);

        final Resources r = getResources();
        imgProj = findViewById(R.id.imgProjectDescribed);
        txtName = findViewById(R.id.txtProjectDescribedName);
        txtDescription = findViewById(R.id.txtProjectDescribedDescription);
        txtCategory = findViewById(R.id.txtProjectDescribedCategory);
        back = findViewById(R.id.btn_project_described_back);
        p_button = findViewById(R.id.btn_project_described_p);

        configureScreen();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(logic) {
                    Intent intent = new Intent(ProjectDescribedActivity.this, MainActivity.class);
                    intent.putExtra("user", user);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(ProjectDescribedActivity.this, ProjectActivity.class);
                    intent.putExtra("user", user);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }



    private void configureScreen() {
        Bundle extras = getIntent().getExtras();
        if(extras!= null && extras.containsKey("projectSend") ) {
            project = (Project) extras.get("projectSend");
            logic = (Boolean) extras.get("logic");
            user = (User) extras.get("user");
            if(project==null) {
                Intent intent = new Intent(ProjectDescribedActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                txtName.setText(project.getName());
                txtCategory.setText(project.getGd());
                txtDescription.setText(project.getDescription());
                Picasso.get().load(project.getProject_url()).into(imgProj);

                if(project.getUuid().equals(user.getUuid())){
                    p_button.setText(R.string.project_described_update);
                    p_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(ProjectDescribedActivity.this, AddProjectActivity.class);
                            intent.putExtra("projectSend", project);
                            intent.putExtra("logic", true);
                            startActivity(intent);
                        }
                    });
                }else{
                    p_button.setText(R.string.project_described_join);
                    p_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRequest();
                        }
                    });
                }
            }
        }else{
            Intent intent = new Intent(ProjectDescribedActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void sendRequest() {
         Interest interest = new Interest(user.getUuid(),user.getName(),project.getProject_id(),project.getName());
         FirebaseFirestore.getInstance().collection("user").document(project.getUuid()).collection("interest").add(interest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
             @Override
             public void onSuccess(DocumentReference documentReference) {
                 Toast.makeText(ProjectDescribedActivity.this, "Solicitação enviada", Toast.LENGTH_SHORT).show();
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(ProjectDescribedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
             }
         });
    }
}