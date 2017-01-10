package fernandez.quentin.todolist.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.activity.MainActivity;
import fernandez.quentin.todolist.R;


/**
 * Created by quent on 08/01/2017.
 */

public class CreateDialog extends AlertDialog.Builder {
    private MainActivity mainActivity = null;
    private EditText _TitleEdit = null;
    private CheckBox _HasPictures = null;
    private EditText _DescEdit = null;
    private AlertDialog _Dialog = null;


    public CreateDialog(Context context) {
        super(context);
        mainActivity = (MainActivity) context;
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog, null);
        setView(mView);
        _TitleEdit = (EditText) mView.findViewById(R.id.txt_title);
        _DescEdit = (EditText) mView.findViewById(R.id.txt_desc);
        _HasPictures = (CheckBox) mView.findViewById(R.id.HasPictures);
        this.setCancelable(true);
        this.setPositiveButton("Créer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("Title", _TitleEdit.getText().toString()).put("Desc", _DescEdit.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (_HasPictures.isChecked()) {
                    mainActivity.temp_obj = obj;
                    Intent i = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    mainActivity.startActivityForResult(i, MainActivity.RESULT_LOAD_IMAGE);
                } else {
                    mainActivity.mAdapter.addElem(obj);
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
        _Dialog = this.create();
    }

    public void Show() {
        _Dialog.show();
    }
}
