package com.bento.tcc_bill_games;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.Item;
import com.xwray.groupie.ViewHolder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    //UI variables
    private EditText Username;
    private EditText Name;
    private EditText Email;
    private EditText Password;
    private Button btn_register;
    private Button selected_photo;
    private Uri mSelectedUri;
    private ImageView mImagePhoto;
    private Spinner sp_area;
    private Spinner sp_line;
    private EditText PhoneNumber;
    private Button add;
    private Button delete;
    private RecyclerView rv;
    private GroupAdapter groupAdapter;

    private List<String> areaM,lineM;
    private Boolean is_ok;
    private Boolean is_updating;
    private Boolean is_selected = false;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //find view by id
        delete = findViewById(R.id.btn_register_delete);
        Email = findViewById(R.id.edit_email_register);
        Username = findViewById(R.id.edittext_username_register);
        Name = findViewById(R.id.edittext_name_register);
        Password = findViewById(R.id.edit_password_register);
        btn_register = findViewById(R.id.btn_register);
        selected_photo = findViewById(R.id.btn_selected_photo);
        mImagePhoto = findViewById(R.id.img_photo);
        PhoneNumber = findViewById(R.id.edtxtRegisterPhoneNumber);
        sp_area = findViewById(R.id.sp_register_area);
        sp_line = findViewById(R.id.sp_register_line);
        add = findViewById(R.id.btn_register_add_new);
        rv = findViewById(R.id.rv_register);

        groupAdapter = new GroupAdapter();
        rv.setAdapter(groupAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setVisibility(View.GONE);
        add.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv.setVisibility(View.VISIBLE);
                groupAdapter.add(new MultipleItem());
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final String Vemail = Email.getText().toString();
            final String Vpassword = Password.getText().toString();
            final String Vname = Name.getText().toString();
            final String Vusername = Username.getText().toString();
            //user's data verification
            is_ok = !Vusername.isEmpty() && !Vname.isEmpty() && !Vemail.isEmpty() && !Vpassword.isEmpty();
            if(is_ok){
                CreateUser();
                btn_register.setVisibility(View.GONE);
            }
            }
        });
        VerifyUpdate();
        ArrayConfig();
        //event for photo selection
        selected_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
    }

    private void VerifyUpdate() {
        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("user")){
            is_updating = true;
            user = (User) extras.get("user");
            Name.setText(user.getName());
            Username.setText(user.getUsername());
            PhoneNumber.setText(user.getPhone());
            Email.setText(user.getEmail());
            Picasso.get().load(user.getProfile_url()).into(mImagePhoto);
            btn_register.setText(R.string.project_described_update);
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DeleteUser();
                }

                private void DeleteUser() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);

                    builder.setTitle("Deletar conta?");
                    builder.setMessage("Deseja deletar sua conta permanentemente?");
                    builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Usuario deletado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
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
                    final AlertDialog delete_account = builder.create();
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delete_account.show();
                        }
                    });
                }
            });
           String area = user.getAreaM().get(0);
           String generalArea ="games_array_"+ area;

        }
    }

    private void ArrayConfig() {
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

        //arrays for area selection
        sp_area.setAdapter(adapter);
        //spinner for each area
        sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        sp_line.setVisibility(View.INVISIBLE);
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
        //spinner for each line of each area
        sp_line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                is_selected = true;
                add.setVisibility(View.VISIBLE);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/"); //this is the data type of the intent return
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
                    //reference to the user's image in a bitmap
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                    //load the selected user's picture and put it in a image view
                    Picasso.get().load(mSelectedUri).into(mImagePhoto);
                    mImagePhoto.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                    //set the button an invisible aspect
                    selected_photo.getBackground().setAlpha(0);
                   //catch for errors
                } catch (FileNotFoundException e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            //this is the case for a canceled activity result
            if(resultCode == RESULT_CANCELED){
                recreate();
            }
        }
    }

    private void CreateUser() {
        //get the edit text content
        final String email = Email.getText().toString();
        final String password = Password.getText().toString();

        //if everything is ok the it create a login email
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
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
                                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }

                }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void SaveUserInDatabase(User user){
        //create a doc reference with the firebaseAuth's id
        FirebaseFirestore.getInstance().collection("users").document(user.getUuid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void SaveUserInFirebase() {
        //create a name for the user's image
        String filename = UUID.randomUUID().toString();

        //a reference to the firestore database
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);

        ref.putFile(mSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        //user's information that are going to the user's class
                        String uid = FirebaseAuth.getInstance().getUid();
                        String username = Username.getText().toString();
                        String profile_url = uri.toString();
                        String name = Name.getText().toString();

                        String phone = PhoneNumber.getText().toString();
                        String email = Email.getText().toString();

                            configureArrayForSave();
                            User user = new User(uid, username, profile_url, name, phone,email,areaM, lineM);
                            SaveUserInDatabase(user);

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            //failure for the photo's upload
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureArrayForSave() {
        areaM = new ArrayList<String>();
        lineM = new ArrayList<String>();
        TextView Area = (TextView)sp_area.getSelectedView();
        String area = Area.getText().toString();

        TextView Line = (TextView)sp_line.getSelectedView();
        String line = Line.getText().toString();
        areaM.add(area) ;
        lineM.add(line);
            for (int i = 0; i < rv.getChildCount(); i++) {
                View view2 = rv.getLayoutManager().findViewByPosition(i);
                Spinner SpinnerArea = view2.findViewById(R.id.sp_item_register_area);
                Spinner SpinnerLine = view2.findViewById(R.id.sp_item_register_line);
                TextView AreaSpinner = (TextView)SpinnerArea.getSelectedView();
                String StringAreaSpinner = AreaSpinner.getText().toString();

                TextView LineSpinner = (TextView)SpinnerLine.getSelectedView();
                String StringLineSpinner = LineSpinner.getText().toString();
                if(!(areaM.contains(StringAreaSpinner) && lineM.contains(StringLineSpinner))){
                    areaM.add(StringAreaSpinner);
                    lineM.add(StringLineSpinner);
                }
            }
    }

    private class MultipleItem extends Item<ViewHolder> {
        private Spinner sp_area_new;
        private Spinner sp_line_new;

        public MultipleItem() {}

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            sp_area_new = viewHolder.itemView.findViewById(R.id.sp_item_register_area);
            sp_line_new = viewHolder.itemView.findViewById(R.id.sp_item_register_line);

            ConfigureArray(sp_area_new,sp_line_new);
        }

        @Override
        public int getLayout() {
            return R.layout.item_register_multiple;
        }

    }

    private void ConfigureArray(Spinner sp_area_new, Spinner sp_line_new) {
        Spinner area = sp_area_new;
        final Spinner line = sp_line_new;
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

        //arrays for area selection
        area.setAdapter(adapter);
        //spinner for each area
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        line.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        line.setAdapter(adapterP);
                        line.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        line.setAdapter(adapterD);
                        line.setVisibility(View.VISIBLE);
                        break;
                    case 3:
                        line.setAdapter(adapterA);
                        line.setVisibility(View.VISIBLE);
                        break;
                    case 4:
                        line.setAdapter(adapterSA);
                        line.setVisibility(View.VISIBLE);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //spinner for each line of each area
        line.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}