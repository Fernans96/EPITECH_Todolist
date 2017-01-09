package fernandez.quentin.todolist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.MainActivity;
import fernandez.quentin.todolist.R;


/**
 * Created by quent on 08/01/2017.
 */

public class CreateDialog extends Dialog {
    private MainActivity mainActivity = null;
    private EditText _TitleEdit = null;
    private EditText _DescEdit = null;


    public CreateDialog(Context context) {
        super(context);
        mainActivity = (MainActivity) context;
        setContentView(R.layout.dialog);
        Button addBtn = (Button) findViewById(R.id.BtnADD);
        _TitleEdit = (EditText) findViewById(R.id.txt_title);
        _DescEdit = (EditText) findViewById(R.id.txt_desc);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("Title", _TitleEdit.getText().toString()).put("Desc", _DescEdit.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mainActivity.mAdapter.addElem(obj);
                dismiss();
            }
        });
    }


}
