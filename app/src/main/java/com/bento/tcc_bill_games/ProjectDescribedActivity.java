package com.bento.tcc_bill_games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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

        LoginUser();
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
        if(is_leader) {
            p_button.setVisibility(View.VISIBLE);
        }else{
            p_button.setVisibility(View.GONE);
        }

        p_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    p_button.setText(R.string.project_described_update);
                    Intent intent = new Intent(ProjectDescribedActivity.this, AddProjectActivity.class);
                    intent.putExtra("projectSend", project);
                    intent.putExtra("logic", true);
                    startActivity(intent);
            }
        });
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
                        Intent intent = new Intent(ProjectDescribedActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private void configureScreen() {
        Bundle extras = getIntent().getExtras();
        if(extras!= null && extras.containsKey("projectSend") ) {
            project = (Project) extras.get("projectSend");
            logic = (Boolean) extras.get("logic");
            if(project.getName().equals("")) {
                Intent intent = new Intent(ProjectDescribedActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                txtName.setText(project.getName());
                txtCategory.setText(project.getGd());
                txtDescription.setText(project.getDescription());
                Picasso.get().load(project.getProject_url()).into(imgProj);
                configureButton(project.getProject_id());
            }
        }else{
            Intent intent = new Intent(ProjectDescribedActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void configureButton(String id){
        FirebaseFirestore.getInstance().collection("projects").whereEqualTo("project_id", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final Project project = doc.toObject(Project.class);
                    assert project != null;
                    if(project.getUuid().equals(FirebaseAuth.getInstance().getUid())){
                        is_leader = true;
                    }
                }
            }
        });
    }

}