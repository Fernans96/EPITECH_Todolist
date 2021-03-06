package fernandez.quentin.todolist.activity;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.dialog.CreateDialog;
import fernandez.quentin.todolist.helper.SimpleItemTouchHelperCallback;
import fernandez.quentin.todolist.list.DividerItemDecoration;
import fernandez.quentin.todolist.list.ToDoAdapter;
import fernandez.quentin.todolist.model.ToDoObject;
import fernandez.quentin.todolist.tools.Share;

public class MainActivity extends AppCompatActivity {
    public static int RESULT_LOAD_IMAGE = 1;
    public static String Filter = "";
    public ToDoAdapter mAdapter = null;
    public ToDoObject tmp = null;
    private CreateDialog _cd = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        checkPermission();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initData();
        initView();
        initCreateBtn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            try {
                tmp.setPicture(BitmapFactory.decodeFile(picturePath));
                if (tmp.getPicture().getByteCount() > 10000000) {
                    throw new java.lang.OutOfMemoryError("Too large pictures");
                }
            } catch (java.lang.OutOfMemoryError e) {
                tmp.setPicture(null);
                Toast.makeText(this, getResources().getString(R.string.largepicturenotif), Toast.LENGTH_LONG).show();
            }
            tmp.save();
            mAdapter.addElem();
            tmp = null;
        }
        if (requestCode == 1000) {
            mAdapter.updateElems();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    finish();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        initSearch(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.NameSort:
                mAdapter.NameSort();
                break;
            case R.id.StateSort:
                mAdapter.StateSort();
                break;
            case R.id.DateSort:
                mAdapter.DateSort();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        }
    }

    private void initData() {
        mAdapter = new ToDoAdapter(this);
    }

    private void showEditDialog() {
        _cd = new CreateDialog(this);
        _cd.Show();
    }

    private void initSearch(Menu menu) {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem _search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(_search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Filter = newText;
                synchronized (mAdapter) {
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Filter = "";
                synchronized (mAdapter) {
                    mAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    }

    private void initView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(24));
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
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
