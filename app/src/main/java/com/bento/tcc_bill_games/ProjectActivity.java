package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        //UI references
        btn_new_project = findViewById(R.id.btn_new_project);
        RecyclerView rv = findViewById(R.id.rv_projects);
        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        /*fetchProjects();

        btn_new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/




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
}
