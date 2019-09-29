package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

public class UserActivity extends AppCompatActivity {

    private TextView txtAreaUser;
    private TextView txtTelefoneUser;
    private TextView txtLine;
    private TextView txtEmailUser;
    private EditText edtxtNameUser;
    private ImageView imgUser;
    private Uri mSelectedUri;
    private EditText edtxtUserameUser;
    private Button btn_change_photo;
    private Button update_profile;
    private Button back;
    private Button delete;
    private AlertDialog delete_account;
    User user;
    boolean is_new_photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //UI references
        txtAreaUser = findViewById(R.id.txtAreaUser);
        txtTelefoneUser = findViewById(R.id.txtTelefoneUser);
        txtEmailUser = findViewById(R.id.txtEmailUser);
        edtxtNameUser = findViewById(R.id.edtxtNameUser);
        imgUser = findViewById(R.id.imageviewUser);
        txtLine = findViewById(R.id.txtLineUser);
        edtxtUserameUser = findViewById(R.id.edtxtUsernameUser);
        btn_change_photo = findViewById(R.id.btn_change_photo);
        update_profile = findViewById(R.id.btn_update_profile);
        update_profile.setVisibility(View.GONE);
        btn_change_photo.setVisibility(View.INVISIBLE);
        back = findViewById(R.id.btn_useractivity_back);
        delete = findViewById(R.id.btn_delete_profile);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        configureScreen();

        //Button events
        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePhoto();

            }
        });

        update_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserProfile();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Deletar conta?");
        builder.setMessage("Deseja deletar sua conta permanentemente?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(UserActivity.this, "Usuario deletado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
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

        edtxtNameUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_profile.setVisibility(View.VISIBLE);
            }
        });

        edtxtUserameUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update_profile.setVisibility(View.VISIBLE);
            }
        });


        delete_account = builder.create();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_account.show();
            }
        });
    }

    private void UpdateUserProfile() {
        if(is_new_photo){
            String filename = UUID.randomUUID().toString();
            final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
            ref.putFile(mSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String uuid = user.getUuid();
                            String username = edtxtUserameUser.getText().toString();
                            String url = uri.toString();
                            String name = edtxtNameUser.getText().toString();
                            //String area = user.getArea();
                            //String line = user.getLine();
                            String phone = user.getPhone();
                            String email = user.getEmail();

                            /*final User usu = new User(uuid,username,url,name,area,line,phone,email);
                            FirebaseFirestore.getInstance().collection("users").document(usu.getUuid()).delete();
                            FirebaseFirestore.getInstance().collection("users").document(usu.getUuid()).set(usu).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent  = new Intent(UserActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }
                            });*/
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
                }
            });
        }
        else {
                String uuid = user.getUuid();
                String username = edtxtUserameUser.getText().toString();
                String url = user.getProfile_url();
                String name = edtxtNameUser.getText().toString();
                //String area = user.getArea();
                //String line = user.getLine();
                String phone = user.getPhone();
                String email = user.getEmail();

                /*final User usu = new User(uuid,username,url,name,area,line,phone,email);
                FirebaseFirestore.getInstance().collection("users").document(usu.getUuid()).delete();
                FirebaseFirestore.getInstance().collection("users").document(usu.getUuid()).set(usu).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent  = new Intent(UserActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
               });*/
            }
        }


    private void updatePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0) {
            //this is the case that user selected a photo
            if (resultCode == RESULT_OK) {
                mSelectedUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                    Picasso.get().load(mSelectedUri).into(imgUser);
                    imgUser.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                    update_profile.setVisibility(View.VISIBLE);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }if(resultCode == RESULT_CANCELED){
                recreate();
            }
        }
    }

    private void configureScreen() {
        Bundle extras = getIntent().getExtras();

        if(extras!= null && extras.containsKey("user") ) {
            user = (User) extras.get("user");
            if(user.getName().equals("")) {
                Intent intent = new Intent(UserActivity.this, MainActivity.class);
                startActivity(intent);
            }else{
                edtxtNameUser.setText(user.getName());
                edtxtUserameUser.setText(user.getUsername());
                //txtAreaUser.setText(user.getArea());
                txtEmailUser.setText(user.getEmail());
                //txtAreaUser.setText(user.getArea());
                txtTelefoneUser.setText(user.getPhone());
                //txtLine.setText(user.getLine());

                Picasso.get().load(user.getProfile_url()).into(imgUser);
            }
        }else{
            Intent intent = new Intent(UserActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}