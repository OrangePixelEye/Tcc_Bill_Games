package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class AddProjectActivity extends AppCompatActivity {

    private Button addImage;
    private ImageView projImage;
    private Button createProj;
    private EditText Name;
    private EditText Description;
    private Uri mSelectedUri;
    private Spinner sp;
    private TextView txt;
    private Button delete;
    private Button back;
    private RecyclerView rv;
    private GroupAdapter groupAdapter;
    private Button add_need;

    boolean is_new_photo = false;
    boolean is_updating = false;
    boolean logic = false;
    User user;
    Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);
        LoginUser();

        final Resources res = getResources();
        projImage = findViewById(R.id.imgAddProject);
        addImage = findViewById(R.id.btn_add_project_image);
        createProj = findViewById(R.id.btn_create_project);
        Name = findViewById(R.id.etAddProjectName);
        Description = findViewById(R.id.etProjectsDescription);
        sp = findViewById(R.id.sp_add_projects);
        txt = findViewById(R.id.txtErrorAddProject);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });
        back = findViewById(R.id.btn_add_project_back);
        delete = findViewById(R.id.btn_delete_project);
        delete.setVisibility(View.GONE);
        rv = findViewById(R.id.rv_add_project);
        groupAdapter = new GroupAdapter();
        rv.setAdapter(groupAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        add_need = findViewById(R.id.btn_add_project_need);

        add_need.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupAdapter.add(new MultipleItem());
            }
        });

        verifyUpdate();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.games_styles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                createProj.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                final Resources r = getResources();
                txt.setText(r.getString(R.string.add_projects_add_category));

            }
        });


        createProj.setVisibility(View.INVISIBLE);
        createProj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProj.setVisibility(View.GONE);
                if(is_updating){
                    UpdateProject();
                    txt.setText(res.getString(R.string.error_register_loading));}
                else {
                    createProject();
                    txt.setText(res.getString(R.string.error_register_loading));
                }
            }
        });



    }

    private void verifyUpdate() {
        Bundle extras = getIntent().getExtras();
        if(extras!= null && extras.containsKey("logic")) {
            logic = (Boolean) extras.get("logic");
            if(extras.containsKey("projectSend")) {
                project = (Project) extras.get("projectSend");
                if (project.getName().equals("")) {
                    Intent intent = new Intent(AddProjectActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    is_updating = true;
                    Name.setText(project.getName());
                    Description.setText(project.getDescription());
                    Picasso.get().load(project.getProject_url()).into(projImage);
                    addImage.setVisibility(View.GONE);
                    delete.setVisibility(View.VISIBLE);
                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FirebaseFirestore.getInstance().collection("projects").document(project.getProject_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(AddProjectActivity.this,MainActivity.class);
                                    intent.putExtra("logic",logic);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                    projImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectPhoto();
                        }
                    });
                }
            }

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(logic){
                        Intent intent = new Intent(AddProjectActivity.this, ProjectDescribedActivity.class);
                        intent.putExtra("projectSend", project);
                        intent.putExtra("user",user);
                        intent.putExtra("logic",logic);
                        startActivity(intent);
                    }else{
                        Intent intent = new Intent(AddProjectActivity.this, ProjectActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("logic",logic);
                        startActivity(intent);
                    }
                }
            });

        }else{
            Intent intent = new Intent(AddProjectActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    private void UpdateProject(){
        final String date = project.getDate_added();
        if(is_new_photo){
            String filename = UUID.randomUUID().toString();
            final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
            ref.putFile(mSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String project_id = project.getProject_id();
                            String uuid = FirebaseAuth.getInstance().getUid();
                            String name = Name.getText().toString();
                            String description = Description.getText().toString();

                            TextView Line = (TextView)sp.getSelectedView();
                            String gd = Line.getText().toString();
                            String profile_url = uri.toString();

                            final Project proj = new Project(gd, project_id, uuid, name, description, profile_url,date);
                            FirebaseFirestore.getInstance().collection("projects").document(project.getProject_id()).delete();
                            FirebaseFirestore.getInstance().collection("projects").document(proj.getProject_id()).set(proj).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent  = new Intent(AddProjectActivity.this,ProjectDescribedActivity.class);
                                    intent.putExtra("projectSend",proj);
                                    intent.putExtra("logic",logic);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(AddProjectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
                }
            });
        }
        else {
            String project_id = project.getProject_id();
            String uuid = FirebaseAuth.getInstance().getUid();
            String name = Name.getText().toString();
            String description = Description.getText().toString();

            TextView Line = (TextView)sp.getSelectedView();
            String gd = Line.getText().toString();
            String profile_url = project.getProject_url();


            final Project proj = new Project(gd, project_id, uuid, name, description, profile_url,date);
            FirebaseFirestore.getInstance().collection("projects").document(project.getProject_id()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    FirebaseFirestore.getInstance().collection("projects").document(proj.getProject_id()).set(proj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent  = new Intent(AddProjectActivity.this,ProjectDescribedActivity.class);
                            intent.putExtra("projectSend",proj);
                            intent.putExtra("logic",logic);
                            startActivity(intent);
                        }
                    });
                }
            });

        }
    }

    private void createProject() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/images/" + filename);
        ref.putFile(mSelectedUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String project_id = UUID.randomUUID().toString();
                        String uuid = FirebaseAuth.getInstance().getUid();
                        String name = Name.getText().toString();
                        String description = Description.getText().toString();

                        TextView Line = (TextView)sp.getSelectedView();
                        String gd = Line.getText().toString();
                        String profile_url = uri.toString();

                        Date currentTime = Calendar.getInstance().getTime();
                        String cDate = "-" + currentTime.toString();
                        Project proj = new Project(gd, project_id, uuid, name, description, profile_url,cDate);

                        FirebaseFirestore.getInstance().collection("projects").document(proj.getProject_id()).set(proj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Intent intent = new Intent(AddProjectActivity.this, ProjectActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddProjectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();;
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
            if (resultCode == RESULT_OK) {
                mSelectedUri = data.getData();
                Bitmap bitmap = null;
                try {
                    if(is_updating){
                        is_new_photo = true;
                    }
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mSelectedUri);
                    Picasso.get().load(mSelectedUri).into(projImage);
                    projImage.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
                    addImage.getBackground().setAlpha(0);
                } catch (FileNotFoundException e) {
                    Toast.makeText(AddProjectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(AddProjectActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
        if(resultCode == RESULT_CANCELED){
            recreate();
        }
    }

    private void LoginUser() {
        String doc = FirebaseAuth.getInstance().getUid();
        if (doc != null) {
            FirebaseFirestore.getInstance().collection("users").document(doc).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot != null) {
                        user = documentSnapshot.toObject(User.class);
                    } else {
                        Intent intent = new Intent(AddProjectActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            });
        }
    }

    private class MultipleItem extends Item<ViewHolder> {
        private Spinner sp_area_new;
        private Spinner sp_line_new;

        public MultipleItem() {
        }

        @Override
        public void bind(@NonNull ViewHolder viewHolder, int position) {
            sp_area_new = viewHolder.itemView.findViewById(R.id.sp_item_register_area);
            sp_line_new = viewHolder.itemView.findViewById(R.id.sp_item_register_line);

            ConfigureArray(sp_area_new, sp_line_new);
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
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.games_array_areas, android.R.layout.simple_spinner_dropdown_item);

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