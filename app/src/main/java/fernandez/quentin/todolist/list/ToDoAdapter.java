package fernandez.quentin.todolist.list;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.activity.EditActivity;
import fernandez.quentin.todolist.tools.PictureTools;

import static android.support.v4.content.ContextCompat.startActivity;
import static fernandez.quentin.todolist.tools.PictureTools.convertDpToPx;

/**
 * Created by quent on 08/01/2017.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private List<JSONObject> _data;
    private SharedPreferences _pref;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todoview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public ToDoAdapter(JSONArray data, SharedPreferences pref) {
        _data = new ArrayList<JSONObject>();
        for (int i = 0; i < data.length(); i++) {
            try {
                _data.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        _pref = pref;
    }

    public boolean onItemMove(int from, int to) {
        notifyItemMoved(from, to);
        Collections.swap(_data, from, to);
        updateSharedPref();
        return true;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setInfo(_data.get(position));
    }

    public void addElem(JSONObject obj) {
        _data.add(obj);
        notifyItemInserted(_data.size() - 1);
        updateSharedPref();
    }

    private void updateSharedPref() {
        SharedPreferences.Editor edit = _pref.edit();
        JSONArray js = new JSONArray(_data);
        edit.putString("data", js.toString());
        edit.apply();
    }

    public void deleteElem(int pos) {
        _data.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, _data.size());
        updateSharedPref();
    }

    public void updateElem(int pos, JSONObject obj) {
        _data.remove(pos);
        _data.add(pos, obj);
        notifyItemChanged(pos);
        updateSharedPref();
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View Card;

        public void setInfo(JSONObject obj) {
            TextView ToDotitle = (TextView) Card.findViewById(R.id.ToDotitle);
            TextView ToDoDesc = (TextView) Card.findViewById(R.id.ToDoDesc);

            try {
                ToDoDesc.setText(obj.getString("Desc"));
                ToDotitle.setText(obj.getString("Title"));
                InitEditBtn(obj);
                initBanner(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void initBanner(JSONObject obj) throws JSONException {
            ImageView BannerPicture = (ImageView) Card.findViewById(R.id.BannerPicture);
            LinearLayout CardLinearLayout = (LinearLayout) Card.findViewById(R.id.CardLinearLayout);
            if (obj.has("picture")) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                DisplayMetrics dm = Card.getResources().getDisplayMetrics();
                lp.setMargins(convertDpToPx(16, dm), convertDpToPx(16 + 75, dm), convertDpToPx(16, dm), convertDpToPx(24, dm));
                CardLinearLayout.setLayoutParams(lp);
                BannerPicture.setImageBitmap(PictureTools.base64ToBitmap(obj.getString("picture")));
            }
        }

        private void InitEditBtn(final JSONObject obj) {
            FloatingActionButton EditBTN = (FloatingActionButton) Card.findViewById(R.id.EditBTN);
            EditBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_pos = getAdapterPosition();
                    Intent intent;
                    intent = new Intent(v.getContext(), EditActivity.class);
                    intent.putExtra("position", current_pos);
                    intent.putExtra("jsonobj", obj.toString());
                    startActivity(v.getContext(), intent, null);
                }
            });
        }

        public void onItemSelected() {

        }

        public void onItemClear() {

        }

        public ViewHolder(View v) {
            super(v);
            Card = v;
        }
    }
}

