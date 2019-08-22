package com.bento.tcc_bill_games;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

public class RegisterActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Name;
    private EditText Email;
    private EditText Password;
    private Button btn_register;
    private Button selected_photo;
    private Uri mSelectedUri;
    private ImageView mImagePhoto;
    private TextView txtError;
    private Spinner sp_area;
    private Spinner sp_line;
    private EditText City;
    private EditText PhoneNumber;
    private Boolean isSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        final Resources res = getResources();
        Email = findViewById(R.id.edit_email_register);
        Username = findViewById(R.id.edittext_username_register);
        Name = findViewById(R.id.edittext_name_register);
        Password = findViewById(R.id.edit_password_register);
        btn_register = findViewById(R.id.btn_register);
        selected_photo = findViewById(R.id.btn_selected_photo);
        mImagePhoto = findViewById(R.id.img_photo);
        txtError = findViewById(R.id.txtErrorRegister);
        City = findViewById(R.id.edtxtRegisterNameCity);
        PhoneNumber = findViewById(R.id.edtxtRegisterPhoneNumber);
        sp_area = findViewById(R.id.sp_register_area);
        sp_line = findViewById(R.id.sp_register_line);


        //Arrays adapters
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.games_array_areas, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapterP = ArrayAdapter.createFromResource(this,
                R.array.games_array_programmer, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapterD = ArrayAdapter.createFromResource(this,
                R.array.games_array_designer, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapterA = ArrayAdapter.createFromResource(this,
                R.array.games_array_artist, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        final ArrayAdapter<CharSequence> adapterSA = ArrayAdapter.createFromResource(this,
                R.array.games_array_sound_master, android.R.layout.simple_spinner_item);
        adapterP.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);



        sp_area.setAdapter(adapter);
        sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        sp_line.setVisibility(View.GONE);
                        break;
                    case 1:

                        sp_line.setAdapter(adapterP);
                        sp_line.setVisibility(View.VISIBLE);

                        break;
                    case 2:

                        sp_line.setAdapter(adapterD);
                        sp_line.setVisibility(View.VISIBLE);

                        break;
                    case 3:

                        sp_line.setAdapter(adapterA);
                        sp_line.setVisibility(View.VISIBLE);

                        break;
                    case 4:

                        sp_line.setAdapter(adapterSA);
                        sp_line.setVisibility(View.VISIBLE);

                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sp_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        selected_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateUser();
                txtError.setText(res.getString(R.string.error_register_loading));
            }
        });

    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent,0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            Log.i("Teste", "até aqui bl");
            mSelectedUri = data.getData();
            Bitmap bitmap = null;
            try {

               bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
               Log.i("Teste", "pegou a foto");
               Picasso.get().load(mSelectedUri).into(mImagePhoto);
               mImagePhoto.setImageDrawable(new BitmapDrawable(getResources(),bitmap));
               selected_photo.getBackground().setAlpha(0);;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void CreateUser() {
        final String email = Email.getText().toString();
        final String password = Password.getText().toString();
        final String name = Name.getText().toString();
        final String username = Username.getText().toString();

        if(username.isEmpty() || username == null || name.isEmpty() || name == null || email.isEmpty() || email == null || password.isEmpty() || password == null){
            Toast.makeText(this, "O email e senha devem ser preenchidos", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Log.i("Teste", task.getResult().getUser().getUid());
                            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        SaveUserInFirebase();
                                    }

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i("Teste", e.getMessage());
                                }
                            });

                        }

                }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("Teste", e.getMessage());
                    }
                });
    }

    private void SaveUserInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(mSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.i("Teste", uri.toString());

                        String uid = FirebaseAuth.getInstance().getUid();
                        String username = Username.getText().toString();
                        String profile_url = uri.toString();
                        String name = Name.getText().toString();

                        User user = new User(uid, username, profile_url, name);

                        FirebaseFirestore.getInstance().collection("users").document(uid).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i("Teste","registou");
                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("Teste", e.getMessage(),e );
                final Resources res = getResources();
                txtError.setText(res.getString(R.string.error_register_img_put));
            }
        });
    }
}
