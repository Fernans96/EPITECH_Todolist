package fernandez.quentin.todolist.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.tools.PictureTools;

import static fernandez.quentin.todolist.activity.MainActivity.RESULT_LOAD_IMAGE;

public class EditActivity extends AppCompatActivity {
    private int _position = 0;
    private JSONObject _note = null;
    private EditText _Edit_Text_Title = null;
    private EditText _Edit_Text_Desc = null;
    private CheckBox _Edit_Check_Pic = null;
    private ImageView _Edit_Pic = null;
    private FloatingActionButton _Edit_Button_Save = null;
    private SimpleDateFormat _date_format = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat _time_format = new SimpleDateFormat("HH:mm");
    private TimePickerDialog _time = null;
    private DatePickerDialog _date = null;
    private EditText _Edit_Text_Time = null;
    private EditText _Edit_Text_Date = null;
    private Calendar _cal = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        String serializedJSON = intent.getStringExtra("jsonobj");
        _position = intent.getIntExtra("position", 0);
        _Edit_Text_Title = (EditText) findViewById(R.id.Edit_Text_Title);
        _Edit_Text_Desc = (EditText) findViewById(R.id.Edit_Text_Desc);
        _Edit_Check_Pic = (CheckBox) findViewById(R.id.Edit_Check_Pic);
        _Edit_Pic = (ImageView) findViewById(R.id.Edit_Pic);
        _Edit_Text_Time = (EditText) findViewById(R.id.Edit_Text_Time);
        _Edit_Text_Date = (EditText) findViewById(R.id.Edit_Text_Date);
        _Edit_Button_Save = (FloatingActionButton) findViewById(R.id.Edit_Button_Save);
        InitSaveBtn();
        InitDateTimePicker();
        InitCheckBox();
        ChangePictures();
        try {
            _note = new JSONObject(serializedJSON);
            if (_note.has("picture")) {
                _Edit_Check_Pic.setChecked(true);
                _Edit_Pic.setVisibility(ImageView.VISIBLE);
                _Edit_Pic.setImageBitmap(PictureTools.base64ToBitmap(_note.getString("picture")));
            }
            _Edit_Text_Title.setText(_note.getString("title"));
            _Edit_Text_Desc.setText(_note.getString("desc"));
            _Edit_Text_Time.setText(_note.getString("time"));
            _Edit_Text_Date.setText(_note.getString("date"));
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
                    _note.put("picture", PictureTools.bitmapToBase64(bitmap));
                    _Edit_Pic.setImageBitmap(bitmap);
                    _Edit_Pic.setVisibility(ImageView.VISIBLE);
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
        _Edit_Button_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_Edit_Check_Pic.isChecked() && _note.has("picture"))
                    _note.remove("picture");
                try {
                    _note.put("title", _Edit_Text_Title.getText().toString())
                            .put("desc", _Edit_Text_Desc.getText().toString())
                            .put("date", _Edit_Text_Date.getText().toString())
                            .put("time", _Edit_Text_Time.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MainActivity.mAdapter.updateElem(_position, _note);
                finish();
            }
        });
    }

    private void ChangePictures() {
        _Edit_Pic.setOnClickListener(new View.OnClickListener() {
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
        _Edit_Check_Pic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!_note.has("picture") && isChecked) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, MainActivity.RESULT_LOAD_IMAGE);
                } else {
                    _Edit_Pic.setVisibility((isChecked) ? ImageView.VISIBLE : ImageView.GONE);
                }
            }
        });
    }

    private void InitDateTimePicker() {
        _Edit_Text_Time.setFocusable(false);
        _Edit_Text_Date.setFocusable(false);
        _cal = Calendar.getInstance();
        _Edit_Text_Time.setText(_time_format.format(_cal.getTime()));
        _Edit_Text_Date.setText(_date_format.format(_cal.getTime()));
        _time = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                _cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                _cal.set(Calendar.MINUTE, minute);
                _Edit_Text_Time.setText(_time_format.format(_cal.getTime()));
            }
        }, _cal.get(Calendar.HOUR_OF_DAY), _cal.get(Calendar.MINUTE), true);
        _date = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _cal.set(Calendar.YEAR, year);
                _cal.set(Calendar.MONTH, month);
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                _Edit_Text_Date.setText(_date_format.format(_cal.getTime()));
            }
        }, _cal.get(Calendar.YEAR), _cal.get(Calendar.MONTH), _cal.get(Calendar.DAY_OF_MONTH));
        _Edit_Text_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _date.show();
            }
        });
        _Edit_Text_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _time.show();
            }
        });
    }


}
