package com.bento.tcc_bill_games;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.GroupieViewHolder;
import com.xwray.groupie.Item;

public class RegisterActivityMultiple extends AppCompatActivity {
    private Button confirm;
    private Button add;
    private Boolean is_equal;
    private Boolean is_ok = false;
    private String[] areaM;
    private String[] lineM;
    private GroupAdapter adapter;
    private String[] area;
    private String[] line;
    Project a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_multiple);
        add = findViewById(R.id.btn_register_multiple_add_category);
        confirm = findViewById(R.id.btn_register_multiple_confirm);
        RecyclerView rv = findViewById(R.id.rv_register_multiple);

        adapter = new GroupAdapter();
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.add(new RegisterActivityMultiple.ProjectItem());
            }
        });
        //receiveValues();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validate();
            }
        });
    }

    private void Validate() {
        if(is_ok){
            send();
        }
    }

    private void send() {

    }

    private void receiveValues() {
        Bundle extras = getIntent().getExtras();
        if(extras.containsKey("MultipleArea")) {
            areaM = (String[]) extras.get("MultipleArea");
        }
    }
    private class ProjectItem extends Item<GroupieViewHolder> {
        private Project p;

        public ProjectItem() {
            this.p = p;
        }

        @Override
        public void bind(@NonNull GroupieViewHolder viewHolder, int position) {

        }


        @Override
        public int getLayout() {
            return R.layout.item_to_message;
        }

    }
}
