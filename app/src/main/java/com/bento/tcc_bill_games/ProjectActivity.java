package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.List;


public class ProjectActivity extends AppCompatActivity {

    private Button btn_new_project;
    private GroupAdapter adapter;
    private Button back;
    private ProgressBar progressBar;
    private RecyclerView rv;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        LoginUser();
        //UI references
        back = findViewById(R.id.btn_project_back);
        btn_new_project = findViewById(R.id.btn_new_project);
        rv = findViewById(R.id.rv_projects);
        rv.setVisibility(View.INVISIBLE);

        progressBar = findViewById(R.id.progress_project_screen);


        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),
                DividerItemDecoration.VERTICAL));
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {
                ProjectItem projItem = (ProjectItem) item;

                Intent intent = new Intent(ProjectActivity.this, ProjectDescribedActivity.class);

                intent.putExtra("projectSend", projItem.p);
                intent.putExtra("logic",false);

                startActivity(intent);
            }
        });

        fetchProjects();

        btn_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProjectActivity.this, AddProjectActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("logic",false);
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
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
        }
    }


    private void fetchProjects() {
        FirebaseFirestore.getInstance().collection("projects").whereEqualTo("uuid",FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
            List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
            for(DocumentSnapshot doc:docs){
                final Project project = doc.toObject(Project.class);
                adapter.add(new ProjectItem(project));
            }
            }
        });
        rv.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

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
            progressBar.setVisibility(View.GONE);
        }


        @Override
        public int getLayout() {
            return R.layout.item_project;
        }

    }
}