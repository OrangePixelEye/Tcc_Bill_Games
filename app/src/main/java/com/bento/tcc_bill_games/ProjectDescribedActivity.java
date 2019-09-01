package com.bento.tcc_bill_games;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.squareup.picasso.Picasso;

public class ProjectDescribedActivity extends AppCompatActivity {
    private Button back;
    private ImageView imgProj;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtCategory;
    Boolean logic;//true in the main's case and false in the projectActivity's case
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_described);

        imgProj = findViewById(R.id.imgProjectDescribed);
        txtName = findViewById(R.id.txtProjectDescribedName);
        txtDescription = findViewById(R.id.txtProjectDescribedDescription);
        txtCategory = findViewById(R.id.txtProjectDescribedCategory);
        back = findViewById(R.id.btn_project_described_back);

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
        Project project;
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
            }
        }else{
            Log.i("Teste","erro ao mandar a classe projects");
            Intent intent = new Intent(ProjectDescribedActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }

}
