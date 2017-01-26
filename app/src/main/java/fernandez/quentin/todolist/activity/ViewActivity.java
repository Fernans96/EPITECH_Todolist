package fernandez.quentin.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.model.ToDoObject;
import fernandez.quentin.todolist.tools.Share;

import static fernandez.quentin.todolist.tools.PictureTools.convertDpToPx;

public class ViewActivity extends AppCompatActivity {
    private ToDoObject _task = null;
    private ImageView _Info_View_Picture = null;
    private TextView _Info_View_Title = null;
    private TextView _Info_View_Desc = null;
    private TextView _Info_View_Date = null;
    private TextView _Info_View_Time = null;
    private TextView _Info_View_State = null;
    private FloatingActionButton _Info_View_State_Btn = null;
    private LinearLayout _Info_View_LinearLayout = null;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.ShareBtn) {
            Share.ShareTask(_task, this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        try {
            init_var();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initButton();
    }

    private void initButton() {
        _Info_View_State_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _task.setState((_task.getState() + 1) % 2);
                setState(_task.getState());
                _task.save();
            }
        });
    }

    private void setState(int state) {
        switch (state) {
            case 0:
                _Info_View_State.setText(getResources().getText(R.string.stateundone));
                _Info_View_State_Btn.setBackgroundTintList(getResources().getColorStateList(R.color.undonestate));
                break;
            case 1:
                _Info_View_State.setText(getResources().getText(R.string.statedone));
                _Info_View_State_Btn.setBackgroundTintList(getResources().getColorStateList(R.color.donestate));
                break;
            case 2:
                _Info_View_State.setText(getResources().getText(R.string.stateelasped));
                _Info_View_State_Btn.setBackgroundTintList(getResources().getColorStateList(R.color.elapsedstate));
                break;
        }
    }

    private void init_var() throws JSONException {
        Intent intent = getIntent();

        long id = intent.getLongExtra("id_task", 0);
        _task = ToDoObject.getTask(id);

        _Info_View_Title = (TextView) findViewById(R.id.Info_View_Title);
        _Info_View_Desc = (TextView) findViewById(R.id.Info_View_Desc);
        _Info_View_Date = (TextView) findViewById(R.id.Info_View_Date);
        _Info_View_Time = (TextView) findViewById(R.id.Info_View_Time);
        _Info_View_State = (TextView) findViewById(R.id.Info_View_State);
        _Info_View_State_Btn = (FloatingActionButton) findViewById(R.id.Info_View_State_Btn);
        _Info_View_Picture = (ImageView) findViewById(R.id.Info_View_Picture);
        _Info_View_LinearLayout = (LinearLayout) findViewById(R.id.Info_View_LinearLayout);

        _Info_View_Title.setText(_task.getTitle());
        _Info_View_Desc.setText(_task.getDesc());
        _Info_View_Date.setText(_task.getDate());
        _Info_View_Time.setText(_task.getTime());
        if (_task.getPicture() != null) {
            _Info_View_Picture.setVisibility(ImageView.VISIBLE);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            DisplayMetrics dm = _Info_View_LinearLayout.getResources().getDisplayMetrics();
            lp.setMargins(convertDpToPx(0, dm), convertDpToPx(75, dm), convertDpToPx(0, dm), convertDpToPx(0, dm));
            _Info_View_LinearLayout.setLayoutParams(lp);
            _Info_View_Picture.setImageBitmap(_task.getPicture());
        }
        setState(_task.getState());
    }
}
