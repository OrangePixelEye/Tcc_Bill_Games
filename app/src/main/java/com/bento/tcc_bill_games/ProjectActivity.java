package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.Group;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.util.List;

import javax.annotation.Nullable;

public class ProjectActivity extends AppCompatActivity {

    private Button btn_new_project;
    private GroupAdapter adapter;
    private Button back;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        LoginUser();
        back = findViewById(R.id.btn_project_back);
        //UI references
        btn_new_project = findViewById(R.id.btn_new_project);
        RecyclerView rv = findViewById(R.id.rv_projects);
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        /*fetchProjects();*/

        btn_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectActivity.this, AddProjectActivity.class);
                intent.putExtra("user", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectActivity.this, MainActivity.class);
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
                        Intent intent = new Intent(ProjectActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}

    /*private void fetchProjects() {
        FirebaseFirestore.getInstance().collection("projects").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("Teste", e.getMessage());
                    return;
                }
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    Project project = doc.toObject(Project.class);
                    //Log.i("Teste", project.getName());

                    adapter.add(new ProjectItem(project));
                }
            }
        });
    }

    private class ProjectItem extends Item<ViewHolder> {
        private Project project;

        public ProjectItem(Project p) {
            this.project = p;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            /*TextView txt_username = viewHolder.itemView.findViewById(R.id.textView);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);
            txt_username.setText(p.getName());
            Picasso.get().load(p.getProfile_url()).into(imgPhoto);

        }

        @Override
        public int getLayout() {
            return R.layout.item_project;
        }
    }
    }*/

