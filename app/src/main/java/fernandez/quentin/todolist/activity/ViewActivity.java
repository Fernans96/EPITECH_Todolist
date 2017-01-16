package fernandez.quentin.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.tools.PictureTools;

public class ViewActivity extends AppCompatActivity {
    private int _position = 0;
    private JSONObject _jObj = null;
    private TextView TitleInfo = null;
    private TextView DescInfo = null;
    private TextView DateInfo = null;
    private TextView HoursInfo = null;
    private TextView State = null;
    private FloatingActionButton StateBtn = null;
    private ImageView ImageInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        Intent intent = getIntent();
        String serializedJSON = intent.getStringExtra("jsonobj");
        _position = intent.getIntExtra("position", 0);
        try {
            _jObj = new JSONObject(serializedJSON);
            init_var();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InitBtn();
    }

    private void InitBtn() {
        StateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int state = _jObj.getInt("status");
                    state = (state + 1) % 2;
                    setState(state);
                    _jObj.put("status", state);
                    MainActivity.mAdapter.updateElem(_position, _jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setState(int state) {
        switch (state) {
            case 0:
                State.setText("Non fait");
                StateBtn.setBackgroundTintList(getResources().getColorStateList(R.color.undonestate));
                break;
            case 1:
                State.setText("Fait");
                StateBtn.setBackgroundTintList(getResources().getColorStateList(R.color.donestate));
                break;
            case 2:
                State.setText("Note passée");
                StateBtn.setBackgroundTintList(getResources().getColorStateList(R.color.elapsedstate));
                break;
        }
    }

    private void init_var() throws JSONException {
        TitleInfo = (TextView) findViewById(R.id.TitleInfo);
        DescInfo = (TextView) findViewById(R.id.DescInfo);
        DateInfo = (TextView) findViewById(R.id.DateInfo);
        HoursInfo = (TextView) findViewById(R.id.HoursInfo);
        State = (TextView) findViewById(R.id.State);
        StateBtn = (FloatingActionButton) findViewById(R.id.StateBTN);
        ImageInfo = (ImageView) findViewById(R.id.ImageInfo);

        TitleInfo.setText(_jObj.getString("title"));
        DescInfo.setText(_jObj.getString("desc"));
        DateInfo.setText(_jObj.getString("date"));
        HoursInfo.setText(_jObj.getString("time"));
        if (_jObj.has("picture")) {
            ImageInfo.setVisibility(ImageView.VISIBLE);
            ImageInfo.setImageBitmap(PictureTools.base64ToBitmap(_jObj.getString("picture")));
        }
        setState(_jObj.getInt("status"));
    }
}
