package fernandez.quentin.todolist.dialog;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.activity.MainActivity;
import fernandez.quentin.todolist.R;


/**
 * Created by quent on 08/01/2017.
 */

public class CreateDialog extends AlertDialog.Builder {
    private MainActivity _mainActivity = null;
    private EditText _TitleEdit = null;
    private CheckBox _HasPictures = null;
    private EditText _DescEdit = null;
    private AlertDialog _Dialog = null;
    private TimePickerDialog _time = null;
    private DatePickerDialog _date = null;
    private EditText _txthours = null;
    private EditText _txt_date = null;
    private Calendar _cal = null;
    private View _View = null;
    private SimpleDateFormat _date_format = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat _time_format = new SimpleDateFormat("HH:mm");


    public CreateDialog(Context context) {
        super(context);
        _mainActivity = (MainActivity) context;
        getAllView();
        setView(_View);
        InitDateTimePicker();
        InitDialog();
        _Dialog = this.create();
    }

    private void getAllView() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(_mainActivity);
        _View = layoutInflaterAndroid.inflate(R.layout.dialog, null);
        _TitleEdit = (EditText) _View.findViewById(R.id.txt_title);
        _DescEdit = (EditText) _View.findViewById(R.id.txt_desc);
        _HasPictures = (CheckBox) _View.findViewById(R.id.HasPictures);
        _txthours = (EditText) _View.findViewById(R.id.txthours);
        _txt_date = (EditText) _View.findViewById(R.id.txt_date);
    }

    private void InitDateTimePicker() {
        _txthours.setFocusable(false);
        _txt_date.setFocusable(false);
        _cal = Calendar.getInstance();
        _txthours.setText(_time_format.format(_cal.getTime()));
        _txt_date.setText(_date_format.format(_cal.getTime()));
        _time = new TimePickerDialog(_View.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                _cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                _cal.set(Calendar.MINUTE, minute);
                _txthours.setText(_time_format.format(_cal.getTime()));
            }
        }, _cal.get(Calendar.HOUR_OF_DAY), _cal.get(Calendar.MINUTE), true);
        _date = new DatePickerDialog(_View.getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                _cal.set(Calendar.YEAR, year);
                _cal.set(Calendar.MONTH, month);
                _cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                _txt_date.setText(_date_format.format(_cal.getTime()));
            }
        }, _cal.get(Calendar.YEAR), _cal.get(Calendar.MONTH), _cal.get(Calendar.DAY_OF_MONTH));
        _txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _date.show();
            }
        });
        _txthours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _time.show();
            }
        });
    }

    private void InitDialog() {
        this.setCancelable(true);
        this.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("title", _TitleEdit.getText().toString())
                            .put("desc", _DescEdit.getText().toString())
                            .put("date", _txt_date.getText().toString())
                            .put("time", _txthours.getText().toString())
                            .put("status", 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (_HasPictures.isChecked()) {
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
        this.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
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
