package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;
import com.xwray.groupie.OnItemClickListener;

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
    private Button btn_notifications;
    private ProgressBar loading_screen;
    RecyclerView rv;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginUser();

        loading_screen = findViewById(R.id.progress_main_screen);
        btn_search = findViewById(R.id.btn_search);
        btn_notifications = findViewById(R.id.btn_main_notification);
        btn_user = findViewById(R.id.btn_user_profile);
        btn_projects = findViewById(R.id.btn_projects);
        btn_projects.getBackground().setAlpha(0);
        btn_logout = findViewById(R.id.btn_logout);
        search_bar = findViewById(R.id.SearchBar);
        rv = findViewById(R.id.rv_main_activity);

        search_bar.setFocusableInTouchMode(true);
        search_bar.requestFocus();

        hideEverything();

        btn_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,InterestActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        adapter = new GroupAdapter();

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),
                DividerItemDecoration.VERTICAL));

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Item item, @NonNull View view) {

                MainActivity.ProjectItem projItem = (MainActivity.ProjectItem) item;

                    Intent intent = new Intent(MainActivity.this, ProjectDescribedActivity.class);

                    intent.putExtra("projectSend", projItem.p);
                    intent.putExtra("logic", true);
                    intent.putExtra("user", user);

                    startActivity(intent);

            }
        });


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                final String search = search_bar.getText().toString();
                intent.putExtra("search",search);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        btn_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
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

        btn_notifications.getBackground().setAlpha(0);
        btn_logout.getBackground().setAlpha(0);
        btn_search.getBackground().setAlpha(0);
        fetchProjects();

    }

    private void showEverything() {
        btn_search.setVisibility(View.VISIBLE);
        btn_notifications.setVisibility(View.VISIBLE);
        btn_user.setVisibility(View.VISIBLE);
        btn_projects.setVisibility(View.VISIBLE);
        btn_logout.setVisibility(View.VISIBLE);
        search_bar.setVisibility(View.VISIBLE);
        rv.setVisibility(View.VISIBLE);
        loading_screen.setVisibility(View.GONE);
    }

    private void hideEverything() {
        btn_search.setVisibility(View.GONE);
        btn_notifications.setVisibility(View.GONE);
        btn_user.setVisibility(View.GONE);;
        btn_projects.setVisibility(View.GONE);
        btn_logout.setVisibility(View.GONE);
        search_bar.setVisibility(View.GONE);
        rv.setVisibility(View.GONE);
    }

    private void verifyAuthentication() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void LoginUser(){
        String doc = FirebaseAuth.getInstance().getUid();
        if(doc != null) {
            FirebaseFirestore.getInstance().collection("users").document(doc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot != null) {
                    user = documentSnapshot.toObject(User.class);
                    if(user == null){
                        verifyAuthentication();
                    }
                }
                else{
                    verifyAuthentication();
                }
                }
            });
        }else{
            verifyAuthentication();
        }
    }

    private void fetchProjects() {
        FirebaseFirestore.getInstance().collection("projects").orderBy("date_added", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){

                    return;
                }
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final Project project = doc.toObject(Project.class);
                    adapter.add(new MainActivity.ProjectItem(project));
                }
                showEverything();
            }
        });

    }

    private class ProjectItem extends Item<GroupieViewHolder>{
        private Project p;

        public ProjectItem(Project p) {
            this.p = p;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {
            TextView txt_username = viewHolder.itemView.findViewById(R.id.textView2);
            ImageView img = viewHolder.itemView.findViewById(R.id.imageView3);
            txt_username.setText(p.getName());
            txt_username.setTypeface(Typeface.DEFAULT_BOLD);
            Picasso.get().load(p.getProject_url()).into(img);
        }


        @Override
        public int getLayout() {
            return R.layout.item_main;
        }

    }

}