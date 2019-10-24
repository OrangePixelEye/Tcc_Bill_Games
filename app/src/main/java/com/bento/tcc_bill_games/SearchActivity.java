package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;
import com.xwray.groupie.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class SearchActivity extends AppCompatActivity {

    String s;
    boolean is_u_bold = false;
    boolean is_p_bold = false;
    String[] verify;


    private Switch sw;
    private TextView proj;
    private TextView txt_user;
    private Button research;
    private GroupAdapter adapter;
    private EditText research_text;
    private RadioGroup rg;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //UI
        proj = findViewById(R.id.txt_search_projects);
        txt_user = findViewById(R.id.txt_search_users);
        sw = findViewById(R.id.sw_search);
        research = findViewById(R.id.btn_search_search);
        research_text = findViewById(R.id.edtxt_search_search);
        rg = findViewById(R.id.rg_search_user);
        Resources res = getResources();
        verify = res.getStringArray(R.array.games_array_areas);

        rg.setVisibility(View.GONE);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton r1 = findViewById(R.id.rb_search_user_by_category);

                if(r1.isChecked()){
                    fetchUsersByCategory();
                }
            }
        });

        configureSearch();

        RecyclerView rv = findViewById(R.id.rv_search);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));

        fetchProjects();
        proj.setTypeface(null,Typeface.BOLD);
        is_p_bold = true;

        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    adapter.clear();
                    if(is_p_bold) {
                        proj.setTypeface(null, Typeface.NORMAL);
                        is_p_bold = false;
                    }
                    rg.setVisibility(View.VISIBLE);
                    is_u_bold=true;
                    txt_user.setTypeface(null, Typeface.BOLD);

                    fetchUsers();

                }else{
                    adapter.clear();
                    if(is_u_bold) {
                        txt_user.setTypeface(null, Typeface.NORMAL);
                        is_u_bold = false;
                        rg.setVisibility(View.GONE);
                    }
                    proj.setTypeface(null,Typeface.BOLD);
                    is_p_bold = true;
                    fetchProjects();
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull Item item, @NonNull View view) {
                            SearchActivity.ProjectItem projItem = (SearchActivity.ProjectItem) item;
                            Intent intent = new Intent(SearchActivity.this, ProjectDescribedActivity.class);
                            intent.putExtra("projectSend", projItem.p);
                            intent.putExtra("logic",false);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    private void fetchUsers() {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("name", s).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final User user = doc.toObject(User.class);
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull Item item, @NonNull View view) {
                            SearchActivity.ProjectItem projItem = (SearchActivity.ProjectItem) item;
                            Intent intent = new Intent(SearchActivity.this, ProjectDescribedActivity.class);
                            intent.putExtra("projectSend", projItem.p);
                            intent.putExtra("logic",false);
                            startActivity(intent);
                        }
                    });
                    adapter.add(new SearchActivity.UserItem(user));
                }
            }
        });
    }

    private void fetchUsersByCategory() {
        FirebaseFirestore.getInstance().collection("users").whereEqualTo("line", s).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final User user = doc.toObject(User.class);
                    adapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull Item item, @NonNull View view) {
                            SearchActivity.ProjectItem projItem = (SearchActivity.ProjectItem) item;

                            Intent intent = new Intent(SearchActivity.this, ProjectDescribedActivity.class);

                            intent.putExtra("projectSend", projItem.p);
                            intent.putExtra("logic",false);

                            startActivity(intent);
                        }
                    });
                    adapter.add(new SearchActivity.UserItem(user));
                }
            }
        });
    }

    private void configureSearch() {
        Bundle extras = getIntent().getExtras();
        String a;
        if(extras!= null && extras.containsKey("search") ) {
            user = (User) extras.get("user");
            a = (String) extras.get("search");
            if(a.equals("")) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                s = a;
                research.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        s = research_text.getText().toString();
                        Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                        intent.putExtra("search", s);
                        startActivity(intent);
                    }
                });
            }
        }else{
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void fetchProjects() {

        FirebaseFirestore.getInstance().collection("projects").whereEqualTo("name", s).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final Project project = doc.toObject(Project.class);
                    adapter.add(new SearchActivity.ProjectItem(project));
                }
            }
        });
    }

    private class ProjectItem extends Item<ViewHolder> {
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
    private class UserItem extends Item<ViewHolder> {
        private User user;

        public UserItem(User user) {
            this.user = user;
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            TextView txt_username = viewHolder.itemView.findViewById(R.id.textView);
            ImageView imgPhoto = viewHolder.itemView.findViewById(R.id.imageView);
            txt_username.setText(user.getName());
            Picasso.get().load(user.getProfile_url()).into(imgPhoto);
        }

        @Override
        public int getLayout() {
            return R.layout.item_user;
        }
    }
}