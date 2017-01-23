package fernandez.quentin.todolist.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.model.ToDoObject;

import static fernandez.quentin.todolist.activity.MainActivity.RESULT_LOAD_IMAGE;
import static fernandez.quentin.todolist.tools.PictureTools.convertDpToPx;

public class EditActivity extends AppCompatActivity {
    private ToDoObject _task = null;
    private EditText _Edit_Text_Title = null;
    private EditText _Edit_Text_Desc = null;
    private EditText _Edit_Text_Time = null;
    private EditText _Edit_Text_Date = null;
    private CheckBox _Edit_Check_Pic = null;
    private ImageView _Edit_Pic = null;
    private FloatingActionButton _Edit_Button_Save = null;
    private SimpleDateFormat _date_format = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat _time_format = new SimpleDateFormat("HH:mm");
    private TimePickerDialog _time = null;
    private DatePickerDialog _date = null;
    private Calendar _cal = null;
    private LinearLayout _Edit_View_LinearLayout = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        initObject();
        initSaveButton();
        initDateTimePicker();
        initCheckBoxEvent();
        changePicture();
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
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
                if (bitmap.getByteCount() > 10000000) {
                    throw new java.lang.OutOfMemoryError("Too large pictures");
                }
                _task.setPicture(bitmap);
                _Edit_Pic.setImageBitmap(bitmap);
                _Edit_Pic.setVisibility(ImageView.VISIBLE);
                CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                DisplayMetrics dm = _Edit_View_LinearLayout.getResources().getDisplayMetrics();
                lp.setMargins(convertDpToPx(0, dm), convertDpToPx(75, dm), convertDpToPx(0, dm), convertDpToPx(0, dm));
                _Edit_View_LinearLayout.setLayoutParams(lp);
            } catch (java.lang.OutOfMemoryError | java.lang.RuntimeException e) {
                if (_task.getPicture() == null)
                    _Edit_Check_Pic.setChecked(false);
                Toast.makeText(this, "Too large picture", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void initObject() {
        Intent intent = getIntent();
        long id = intent.getLongExtra("id_task", 0);
        if (id == 0) {
            finish();
        }
        _task = ToDoObject.getTask(id);
        _Edit_Text_Title = (EditText) findViewById(R.id.Edit_Text_Title);
        _Edit_Text_Desc = (EditText) findViewById(R.id.Edit_Text_Desc);
        _Edit_Text_Time = (EditText) findViewById(R.id.Edit_Text_Time);
        _Edit_Text_Date = (EditText) findViewById(R.id.Edit_Text_Date);
        _Edit_Check_Pic = (CheckBox) findViewById(R.id.Edit_Check_Pic);
        _Edit_Pic = (ImageView) findViewById(R.id.Edit_Pic);
        _Edit_Button_Save = (FloatingActionButton) findViewById(R.id.Edit_Button_Save);
        _Edit_View_LinearLayout = (LinearLayout) findViewById(R.id.Edit_View_LinearLayout);

        if (_task.getPicture() != null) {
            _Edit_Check_Pic.setChecked(true);
            _Edit_Pic.setVisibility(ImageView.VISIBLE);
            CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            DisplayMetrics dm = _Edit_View_LinearLayout.getResources().getDisplayMetrics();
            lp.setMargins(convertDpToPx(0, dm), convertDpToPx(75, dm), convertDpToPx(0, dm), convertDpToPx(0, dm));
            _Edit_View_LinearLayout.setLayoutParams(lp);
            _Edit_Pic.setImageBitmap(_task.getPicture());
        }
        _Edit_Text_Title.setText(_task.getTitle());
        _Edit_Text_Desc.setText(_task.getDesc());
        _Edit_Text_Time.setText(_task.getTime());
        _Edit_Text_Date.setText(_task.getDate());
    }

    private void initSaveButton() {
        _Edit_Button_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_Edit_Check_Pic.isChecked() && _task.getPicture() != null)
                    _task.setPicture(null);
                _task.setTitle(_Edit_Text_Title.getText().toString())
                        .setDesc(_Edit_Text_Desc.getText().toString())
                        .setDate(_Edit_Text_Date.getText().toString())
                        .setTime(_Edit_Text_Time.getText().toString());
                _task.save();
                finish();
            }
        });
    }

    private void changePicture() {
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

    private void initCheckBoxEvent() {
        _Edit_Check_Pic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (_task.getPicture() == null && isChecked) {
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

    private void initDateTimePicker() {
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
