package fernandez.quentin.todolist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import fernandez.quentin.todolist.helper.SimpleItemTouchHelperCallback;
import fernandez.quentin.todolist.dialog.CreateDialog;
import fernandez.quentin.todolist.list.DividerItemDecoration;
import fernandez.quentin.todolist.list.ToDoAdapter;
import fernandez.quentin.todolist.tools.PictureTools;

public class MainActivity extends AppCompatActivity {
    public static int RESULT_LOAD_IMAGE = 1;
    public ToDoAdapter mAdapter = null;
    public JSONObject temp_obj = null;

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
        cd.Show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                temp_obj.put("picture", PictureTools.bitmapToBase64(BitmapFactory.decodeFile(picturePath)));
                mAdapter.addElem(new JSONObject(temp_obj.toString()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            temp_obj = null;
        }
    }


    private void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(24));
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback =
                new SimpleItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);
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
