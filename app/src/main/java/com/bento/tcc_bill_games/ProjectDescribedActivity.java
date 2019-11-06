package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.List;

public class ProjectDescribedActivity extends AppCompatActivity {
    private Button back;

    private Button p_button;
    private ImageView imgProj;
    private TextView txtName;
    private TextView txtDescription;
    private TextView txtCategory;
    private RecyclerView rv;
    private GroupAdapter adapter;
    Boolean logic;//true in the main's case and false in the projectActivity's case
    User user;
    Project project;
    Boolean is_leader = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_described);

        final Resources r = getResources();
        rv = findViewById(R.id.rv_project_described);
        imgProj = findViewById(R.id.imgProjectDescribed);
        txtName = findViewById(R.id.txtProjectDescribedName);
        txtDescription = findViewById(R.id.txtProjectDescribedDescription);
        txtCategory = findViewById(R.id.txtProjectDescribedCategory);
        back = findViewById(R.id.btn_project_described_back);
        p_button = findViewById(R.id.btn_project_described_p);

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
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        configureScreen();
    }

    private class NeedItem extends Item<ViewHolder> {
        String name,line;
        public NeedItem(String a,String b){
            this.name = a;
            this.line = b;
        }
        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txt_area = viewHolder.itemView.findViewById(R.id.txt_profile_area);
            TextView txt_line = viewHolder.itemView.findViewById(R.id.txt_profile_line);
            txt_area.setText(this.name);
            txt_line.setText(this.line);
        }

        @Override
        public int getLayout() {
            return R.layout.item_profile_categories;
        }
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

                final int size = project.getAreaN().size();
                for (int i = 0; i < size; i++)
                {
                    String area = project.getAreaN().get(i);
                    String line = project.getLineN().get(i);
                    adapter.add(new NeedItem(area,line));
                }

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
                            p_button.setVisibility(View.GONE);
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
         FirebaseFirestore.getInstance().collection("users").document(project.getUuid()).collection("interest").add(interest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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