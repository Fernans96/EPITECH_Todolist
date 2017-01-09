package fernandez.quentin.todolist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.dialog.CreateDialog;
import fernandez.quentin.todolist.list.DividerItemDecoration;
import fernandez.quentin.todolist.list.ToDoAdapter;

public class MainActivity extends AppCompatActivity {
    public ToDoAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        InitData();
        initView();
        initCreateBtn();
    }

    private void InitData() {
        SharedPreferences s = getSharedPreferences("Todo", 0);
        try {
            mAdapter = new ToDoAdapter(new JSONArray(s.getString("data", "[]")), s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showEditDialog() {
        CreateDialog cd = new CreateDialog(this);
        cd.show();
    }


    private void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(24));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initCreateBtn() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditDialog();
            }
        });
    }
}
