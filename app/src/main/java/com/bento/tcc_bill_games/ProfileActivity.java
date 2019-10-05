package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;


public class ProfileActivity extends AppCompatActivity {

    //UI references
    private TextView username;
    private  TextView name;
    private TextView email;
    private TextView phone;
    private ImageView imgUser;
    private Button update;
    private Button back;
    private GroupAdapter adapter;
    private RecyclerView recyclerView;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        username = findViewById(R.id.txt_profile_username);
        name = findViewById(R.id.txt_profile_name);
        email = findViewById(R.id.txt_profile_email);
        phone = findViewById(R.id.txt_profile_phone);
        imgUser = findViewById(R.id.imageviewUser);
        update = findViewById(R.id.btn_update_profile);
        back = findViewById(R.id.btn_useractivity_back);
        recyclerView = findViewById(R.id.rv_profile);

        adapter = new GroupAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,RegisterActivity.class);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });

        configureScreen();
    }

    private void configureScreen() {
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
            if(user.getName().equals("")) {
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                name.setText(user.getName());
                username.setText(user.getUsername());
                email.setText(user.getEmail());
                phone.setText(user.getPhone());
                final int size = user.getAreaM().size();
                for (int i = 0; i < size; i++)
                {
                    String area = user.getAreaM().get(i);
                    String line = user.getLineM().get(i);
                    adapter.add(new ProfileItem(area,line));
                }
                Picasso.get().load(user.getProfile_url()).into(imgUser);


            }
        }else{
            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private class ProfileItem extends Item<ViewHolder>{
        String name,line;
        public ProfileItem(String a,String b){
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
    /*
    AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Deletar conta?");
        builder.setMessage("Deseja deletar sua conta permanentemente?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Usuario deletado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        });
        builder.setNegativeButton("Não Mano", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                recreate();
            }
        });
        delete_account = builder.create();
         delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_account.show();
            }
        });
 */
}