package fernandez.quentin.todolist.list;

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
import fernandez.quentin.todolist.activity.MainActivity;
import fernandez.quentin.todolist.activity.ViewActivity;
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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todoview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * Create a new adapter for recyclerView
     * @param data list of task
     * @param pref object for the persistence of data
     */

    public ToDoAdapter(JSONArray data, SharedPreferences pref) {
        _data = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            try {
                _data.add(data.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        _pref = pref;
    }

    /**
     *
     * @param position in the adapter
     * @return
     */

    public JSONObject getItem(int position) {
        return _data.get(position);
    }

    /**
     * @param from old position in the adapter
     * @param to new position in the adapter
     * @return true
     */

    public boolean onItemMove(int from, int to) {
        notifyItemMoved(from, to);
        Collections.swap(_data, from, to);
        updateSharedPref();
        return true;
    }

    /**
     * @param holder card view you want to set or update
     * @param position card view position in the adapter
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        JSONObject task = _data.get(position);
        try {
            if (task.getString("title").toLowerCase().contains(MainActivity.Filter.toLowerCase())) {
                holder.setInfo(task);
                holder.Card.setVisibility(View.VISIBLE);
            } else {
                RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)holder.Card.getLayoutParams();
                param.height = 0;
                param.width = 0;
                holder.Card.setLayoutParams(param);
                holder.Card.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param obj task format in JSONObject
     */

    public void addElem(JSONObject obj) {
        _data.add(obj);
        notifyItemInserted(_data.size() - 1);
        updateSharedPref();
    }

    /**
     * This function will update the persistence of the app
     */

    private void updateSharedPref() {
        SharedPreferences.Editor edit = _pref.edit();
        JSONArray js = new JSONArray(_data);
        edit.putString("data", js.toString());
        edit.apply();
    }

    /**
     * @param pos Position of the task in the adapter
     */

    public void deleteElem(int pos) {
        _data.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, _data.size());
        updateSharedPref();
    }

    /**
     * @param pos Position of the task in the adapter
     * @param obj task format in JSONObject
     */

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

        private void setLayoutParam() {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams)Card.getLayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            Card.setLayoutParams(param);
        }

        private void initBanner(JSONObject obj) throws JSONException {
            ImageView Card_Banner = (ImageView) Card.findViewById(R.id.Card_Banner);
            LinearLayout Card_Layout_Linear = (LinearLayout) Card.findViewById(R.id.Card_Layout_Linear);
            if (obj.has("picture")) {
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                DisplayMetrics dm = Card.getResources().getDisplayMetrics();
                lp.setMargins(convertDpToPx(16, dm), convertDpToPx(16 + 75, dm), convertDpToPx(16, dm), convertDpToPx(24, dm));
                Card_Layout_Linear.setLayoutParams(lp);
                Card_Banner.setImageBitmap(PictureTools.base64ToBitmap(obj.getString("picture")));
            }
        }

        private void initEditBtn(final JSONObject obj) {
            FloatingActionButton Card_Button_Edit = (FloatingActionButton) Card.findViewById(R.id.Card_Button_Edit);
            Card_Button_Edit.setOnClickListener(new View.OnClickListener() {
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

        /**
         * @param Card_Button_State FloatingActionButton
         * @param obj task format as JSONObject
         * @throws JSONException if needed information can't be solved
         */

        private void initDoneBtn(FloatingActionButton Card_Button_State, final JSONObject obj) throws JSONException {
            switch (obj.getInt("status")) {
                case 0:
                    Card_Button_State.setBackgroundTintList(Card.getResources().getColorStateList(R.color.undonestate));
                    break;
                case 1:
                    Card_Button_State.setBackgroundTintList(Card.getResources().getColorStateList(R.color.donestate));
                    break;
                case 2:
                    Card_Button_State.setBackgroundTintList(Card.getResources().getColorStateList(R.color.elapsedstate));
                    break;
            }
            Card_Button_State.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        int state = obj.getInt("status");
                        int current_pos = getAdapterPosition();
                        state = (state + 1) % 2;
                        obj.put("status", state);
                        MainActivity.mAdapter.updateElem(current_pos, obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * @param obj task format as JSONObject
         */
        private void initCardClick(final JSONObject obj) {
            Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_pos = getAdapterPosition();
                    Intent intent;
                    intent = new Intent(v.getContext(), ViewActivity.class);
                    intent.putExtra("position", current_pos);
                    intent.putExtra("jsonobj", obj.toString());
                    startActivity(v.getContext(), intent, null);
                }
            });

        }

        public ViewHolder(View v) {
            super(v);
            Card = v;
        }

        public void setInfo(JSONObject task) {
            setLayoutParam();
            TextView Card_Text_Title = (TextView) Card.findViewById(R.id.Card_Text_Title);
            TextView Card_Text_Desc = (TextView) Card.findViewById(R.id.Card_Text_Desc);
            FloatingActionButton Card_Button_State = (FloatingActionButton) Card.findViewById(R.id.Card_Button_State);
            try {
                Card_Text_Desc.setText(task.getString("desc"));
                Card_Text_Title.setText(task.getString("title"));
                initDoneBtn(Card_Button_State, task);
                initEditBtn(task);
                initBanner(task);
                initCardClick(task);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}

