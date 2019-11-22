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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

import java.util.List;

import javax.annotation.Nullable;

public class InterestActivity extends AppCompatActivity {

    private Button back;
    private RecyclerView rv;
    private GroupAdapter adapter;
    private ProgressBar progressBar;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);

        LoginUser();
        back = findViewById(R.id.btn_interest_back);
        rv = findViewById(R.id.rv_interest);
        progressBar = findViewById(R.id.progress_interest_screen);

        adapter = new GroupAdapter();

        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(rv.getContext(),
                DividerItemDecoration.VERTICAL));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InterestActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        fetchInterest();
    }

    private void LoginUser() {
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
        }else {
            Intent intent = new Intent(InterestActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void fetchInterest() {
        FirebaseFirestore.getInstance().collection("users").document(user.getUuid()).collection("interest").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    Log.e("Teste", e.getMessage());
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                for(DocumentSnapshot doc:docs){
                    final Interest interest = doc.toObject(Interest.class);
                    adapter.add(new InterestActivity.InterestItem(interest));
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private class InterestItem extends Item<GroupieViewHolder> {
        private Interest interest;
        public InterestItem(Interest interest) {
            this.interest = interest;
        }

        @Override
        public void bind(@NonNull final GroupieViewHolder viewHolder, int position) {
            TextView txt_username = viewHolder.itemView.findViewById(R.id.txt_item_interest);
            TextView txt_project = viewHolder.itemView.findViewById(R.id.txt_item_interest_project);

            Button accept = viewHolder.itemView.findViewById(R.id.btn_item_interest_acccept);
            Button decline = viewHolder.itemView.findViewById(R.id.btn_item_interest_decline);

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore.getInstance().collection("projects").document(interest.getProj_id()).collection("help").add(interest).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(InterestActivity.this,"OK", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });

            txt_username.setText(interest.getName());
            txt_username.setTypeface(Typeface.DEFAULT_BOLD);
            txt_username.setText(interest.getProj_name());
        }


        @Override
        public int getLayout() {
            return R.layout.item_interest;
        }

    }

}

