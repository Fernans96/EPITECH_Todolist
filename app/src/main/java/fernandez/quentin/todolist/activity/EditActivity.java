package fernandez.quentin.todolist.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.tools.PictureTools;

import static fernandez.quentin.todolist.activity.MainActivity.RESULT_LOAD_IMAGE;

public class EditActivity extends AppCompatActivity {
    private int _position = 0;
    private JSONObject _jObj = null;
    private TextView _EditTitle = null;
    private TextView _EditDesc = null;
    private CheckBox _EditCheck = null;
    private ImageView _EditImage = null;
    private FloatingActionButton _saveChange_edit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        String serializedJSON = intent.getStringExtra("jsonobj");
        _position = intent.getIntExtra("position", 0);
        _EditTitle = (TextView) findViewById(R.id.edit_Title);
        _EditDesc = (TextView) findViewById(R.id.EditDesc);
        _EditCheck = (CheckBox) findViewById(R.id.edit_checkbox);
        _EditImage = (ImageView) findViewById(R.id.edit_imageview);
        _saveChange_edit = (FloatingActionButton) findViewById(R.id.saveChange_edit);
        InitSaveBtn();
        InitCheckBox();
        ChangePictures();
        try {
            _jObj = new JSONObject(serializedJSON);
            if (_jObj.has("picture")) {
                _EditCheck.setChecked(true);
                _EditImage.setVisibility(ImageView.VISIBLE);
                _EditImage.setImageBitmap(PictureTools.base64ToBitmap(_jObj.getString("picture")));
            }
            _EditTitle.setText(_jObj.getString("Title"));
            _EditDesc.setText(_jObj.getString("Desc"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                try {
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                    _jObj.put("picture", PictureTools.bitmapToBase64(bitmap));
                    _EditImage.setImageBitmap(bitmap);
                } catch (java.lang.OutOfMemoryError e) {
                    Toast t = Toast.makeText(this,"Image trop grande", Toast.LENGTH_LONG);
                    t.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void InitSaveBtn() {
        _saveChange_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_EditCheck.isChecked() && _jObj.has("picture"))
                    _jObj.remove("picture");
                try {
                    _jObj.put("Title", _EditTitle.getText().toString()).put("Desc", _EditDesc.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.mAdapter.updateElem(_position, _jObj);
                finish();
            }
        });
    }

    private void ChangePictures() {
        _EditImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, MainActivity.RESULT_LOAD_IMAGE);
            }
        });
    }

    private void InitCheckBox() {
        _EditCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!_jObj.has("picture") && isChecked) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, MainActivity.RESULT_LOAD_IMAGE);
                } else {
                    _EditImage.setVisibility((isChecked) ? ImageView.VISIBLE : ImageView.GONE);
                }
            }
        });
    }

}
