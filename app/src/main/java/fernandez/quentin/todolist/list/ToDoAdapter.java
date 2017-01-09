package fernandez.quentin.todolist.list;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fernandez.quentin.todolist.R;

/**
 * Created by quent on 08/01/2017.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private JSONArray _data;
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
        _data = data;
        _pref = pref;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final int pos = position;
        TextView ToDoDelete = (TextView) holder.Card.findViewById(R.id.ToDoDelete);
        ToDoDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteElem(pos);
            }
        });
        try {
            holder.setInfo(_data.getJSONObject(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addElem(JSONObject obj) {
        _data.put(obj);
        notifyItemInserted(_data.length() - 1);
        updateSharedPref();
    }

    private void updateSharedPref() {
        SharedPreferences.Editor edit = _pref.edit();
        edit.putString("data", _data.toString());
        edit.apply();
    }

    public void deleteElem(int pos) {
        _data.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, _data.length());
        updateSharedPref();
    }

    @Override
    public int getItemCount() {
        return _data.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View Card;

        public void setInfo(JSONObject obj) {
            TextView ToDotitle = (TextView)Card.findViewById(R.id.ToDotitle);
            TextView ToDoDesc = (TextView)Card.findViewById(R.id.ToDoDesc);
            try {
                ToDoDesc.setText(obj.getString("Desc"));
                ToDotitle.setText(obj.getString("Title"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        public ViewHolder(View v) {
            super(v);
            Card = v;
        }
    }
}

