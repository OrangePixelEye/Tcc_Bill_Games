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
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    //UI variables
    private Button btn_search;
    private Button btn_user;
    private Button btn_projects;
    private Button btn_logout;
    private GroupAdapter adapter;
    private EditText search_bar;
    private Button back;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginUser();

        btn_search = findViewById(R.id.btn_search);
        btn_user = findViewById(R.id.btn_user_profile);
        btn_projects = findViewById(R.id.btn_projects);
        btn_projects.getBackground().setAlpha(0);
        btn_logout = findViewById(R.id.btn_logout);
        search_bar = findViewById(R.id.SearchBar);
        RecyclerView rv = findViewById(R.id.rv_main_activity);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                MainActivity.ProjectItem projItem = (MainActivity.ProjectItem) item;

                Intent intent = new Intent(MainActivity.this, ProjectDescribedActivity.class);

                intent.putExtra("projectSend", projItem.p);
                intent.putExtra("logic",true);

                startActivity(intent);
            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                final String search = search_bar.getText().toString();
                intent.putExtra("search",search);
                startActivity(intent);
            }
        });

        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, UserActivity.class);
                intent.putExtra("user",  user);
                startActivity(intent);
            }
        });

        btn_projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                intent.putExtra("user",  user);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                verifyAuthentication();
            }
        });

        fetchProjects();
    }

    private void verifyAuthentication() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    private void LoginUser(){

        String doc = FirebaseAuth.getInstance().getUid();
        if(doc != null) {
            FirebaseFirestore.getInstance().collection("users").document(doc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot != null) {
                        user = documentSnapshot.toObject(User.class);
                    }
                    else{
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }else{
            verifyAuthentication();

        }
    }

    private void fetchProjects() {
        FirebaseFirestore.getInstance().collection("projects").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("Teste", e.getMessage());
                    return;
                }
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final Project project = doc.toObject(Project.class);
                    adapter.add(new MainActivity.ProjectItem(project));


                }
            }
        });
    }

    private class ProjectItem extends Item<ViewHolder>{
        private Project p;

        public ProjectItem(Project p) {
            this.p = p;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txt_username = viewHolder.itemView.findViewById(R.id.txtItemProjectName);
            TextView txt_description = viewHolder.itemView.findViewById(R.id.txtItemProjectDescription);
            txt_username.setText(p.getName());
            txt_description.setText(p.getDescription());
        }


        @Override
        public int getLayout() {
            return R.layout.item_project;
        }

    }

}