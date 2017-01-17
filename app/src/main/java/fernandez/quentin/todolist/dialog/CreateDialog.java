package fernandez.quentin.todolist.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.activity.MainActivity;


/**
 * Created by quent on 08/01/2017.
 */

public class CreateDialog extends AlertDialog.Builder {
    private MainActivity _mainActivity = null;
    private EditText _Dialog_Txt_Title = null;
    private EditText _Dialog_Txt_Time = null;
    private EditText _Dialog_Txt_Date = null;
    private EditText _Dialog_Txt_Desc = null;
    private CheckBox _Dialog_Check_Photo = null;
    private AlertDialog _Dialog = null;
    private TimePickerDialog _TimePickerDialog = null;
    private DatePickerDialog _DatePickerDialog = null;
    private Calendar _cal = null;
    private View _Dialog_Layout = null;
    private SimpleDateFormat _date_format = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat _time_format = new SimpleDateFormat("HH:mm");


    public CreateDialog(Context context) {
        super(context);
        _mainActivity = (MainActivity) context;
        getAllView();
        setView(_Dialog_Layout);
        InitDateTimePicker();
        InitDialog();
        _Dialog = this.create();
    }

    private void getAllView() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(_mainActivity);
        _Dialog_Layout = layoutInflaterAndroid.inflate(R.layout.dialog, null);
        _Dialog_Txt_Title = (EditText) _Dialog_Layout.findViewById(R.id.Dialog_Txt_Title);
        _Dialog_Txt_Desc = (EditText) _Dialog_Layout.findViewById(R.id.Dialog_Txt_Desc);
        _Dialog_Txt_Date = (EditText) _Dialog_Layout.findViewById(R.id.Dialog_Txt_Date);
        _Dialog_Txt_Time = (EditText) _Dialog_Layout.findViewById(R.id.Dialog_Txt_Time);
        _Dialog_Check_Photo = (CheckBox) _Dialog_Layout.findViewById(R.id.Dialog_Check_Photo);
    }

    private void InitDateTimePicker() {
        _cal = Calendar.getInstance();
        _Dialog_Txt_Time.setFocusable(false);
        _Dialog_Txt_Date.setFocusable(false);
        _Dialog_Txt_Time.setText(_time_format.format(_cal.getTime()));
        _Dialog_Txt_Date.setText(_date_format.format(_cal.getTime()));
        _TimePickerDialog = new TimePickerDialog(_Dialog_Layout.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                _cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                _cal.set(Calendar.MINUTE, minute);
                _Dialog_Txt_Time.setText(_time_format.format(_cal.getTime()));
            }
        }, _cal.get(Calendar.HOUR_OF_DAY), _cal.get(Calendar.MINUTE), true);
        _DatePickerDialog = new DatePickerDialog(_Dialog_Layout.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _cal.set(Calendar.YEAR, year);
                _cal.set(Calendar.MONTH, month);
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                _Dialog_Txt_Date.setText(_date_format.format(_cal.getTime()));
            }
        }, _cal.get(Calendar.YEAR), _cal.get(Calendar.MONTH), _cal.get(Calendar.DAY_OF_MONTH));
        _Dialog_Txt_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _DatePickerDialog.show();
            }
        });
        _Dialog_Txt_Time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _TimePickerDialog.show();
            }
        });
    }

    private void InitDialog() {
        this.setCancelable(true);
        this.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("title", _Dialog_Txt_Title.getText().toString())
                            .put("desc", _Dialog_Txt_Desc.getText().toString())
                            .put("date", _Dialog_Txt_Date.getText().toString())
                            .put("time", _Dialog_Txt_Time.getText().toString())
                            .put("status", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (_Dialog_Check_Photo.isChecked()) {
                    _mainActivity.temp_obj = obj;
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    _mainActivity.startActivityForResult(i, MainActivity.RESULT_LOAD_IMAGE);
                } else {
                    _mainActivity.mAdapter.addElem(obj);
                    dialog.dismiss();
                }
            }
        });
        this.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    public void Show() {
        _Dialog.show();
    }
}
