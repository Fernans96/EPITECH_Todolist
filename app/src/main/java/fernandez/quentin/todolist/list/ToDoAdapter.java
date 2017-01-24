package fernandez.quentin.todolist.list;

import android.content.Intent;
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

import org.json.JSONException;

import java.util.List;

import fernandez.quentin.todolist.R;
import fernandez.quentin.todolist.activity.EditActivity;
import fernandez.quentin.todolist.activity.MainActivity;
import fernandez.quentin.todolist.activity.ViewActivity;
import fernandez.quentin.todolist.model.ToDoObject;

import static android.support.v4.content.ContextCompat.startActivity;
import static fernandez.quentin.todolist.tools.PictureTools.convertDpToPx;

/**
 * Created by quent on 08/01/2017.
 */

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    List<ToDoObject> _data = null;
    MainActivity mainActivity = null;

    public ToDoAdapter(MainActivity ctx) {
        mainActivity = ctx;
        _data = ToDoObject.getAllTask();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.todoview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * @param position in the adapter
     * @return
     */

    public ToDoObject getItem(int position) {
        return _data.get(position);
    }

    /**
     * @param from old position in the adapter
     * @param to   new position in the adapter
     * @return true
     */

    public boolean onItemMove(int from, int to) {
        ToDoObject.swapTask(_data.get(from), _data.get(to));
        notifyItemMoved(from, to);
        return true;
    }

    /**
     * @param holder   card view you want to set or update
     * @param position card view position in the adapter
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ToDoObject task = _data.get(position);
        if (task.getTitle().toLowerCase().contains(MainActivity.Filter.toLowerCase())) {
            holder.setInfo(task);
            holder.Card.setVisibility(View.VISIBLE);
        } else {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) holder.Card.getLayoutParams();
            param.height = 0;
            param.width = 0;
            holder.Card.setLayoutParams(param);
            holder.Card.setVisibility(View.GONE);
        }
    }

    public void NameSort(){
        for (int i = 0; i + 1 < _data.size(); i++) {
            ToDoObject todo1 = _data.get(i);
            ToDoObject todo2 = _data.get(i + 1);
            if (todo1.getTitle().compareTo(todo2.getTitle()) > 0) {
                ToDoObject.swapTask(todo1, todo2);
                notifyItemMoved(i, i+1);
                i = -1;
            }
        }
    }

    public void StateSort() {
        for (int i = 0; i + 1 < _data.size(); i++) {
            ToDoObject todo1 = _data.get(i);
            ToDoObject todo2 = _data.get(i + 1);
            if (todo1.getState() >  todo2.getState()) {
                ToDoObject.swapTask(todo1, todo2);
                notifyItemMoved(i, i+1);
                i = -1;
            }
        }
    }

    public void DateSort() {
        for (int i = 0; i + 1 < _data.size(); i++) {
            ToDoObject todo1 = _data.get(i);
            ToDoObject todo2 = _data.get(i + 1);
            if (todo1.getTimestamp() >  todo2.getTimestamp()) {
                ToDoObject.swapTask(todo1, todo2);
                notifyItemMoved(i, i+1);
                i = -1;
            }
        }
    }

    public void addElem() {
        _data = ToDoObject.getAllTask();
        notifyItemInserted(_data.size() - 1);
    }

    /**
     * @param pos Position of the task in the adapter
     */

    public void deleteElem(int pos) {
        _data.get(pos).delete();
        _data.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, _data.size());
    }

    /**
     * @param pos Position of the task in the adapter
     */

    public void updateElem(int pos) {
        notifyItemChanged(pos);
    }

    public void updateElems() {
        _data = ToDoObject.getAllTask();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View Card;

        public ViewHolder(View v) {
            super(v);
            Card = v;
        }

        private void setLayoutParam() {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) Card.getLayoutParams();
            param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            Card.setLayoutParams(param);
        }

        private void initBanner(ToDoObject obj) throws JSONException {
            ImageView Card_Banner = (ImageView) Card.findViewById(R.id.Card_Banner);
            LinearLayout Card_Layout_Linear = (LinearLayout) Card.findViewById(R.id.Card_Layout_Linear);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (obj.getPicture() != null) {
                DisplayMetrics dm = Card.getResources().getDisplayMetrics();
                lp.setMargins(0, convertDpToPx(75, dm), 0, 0);
                Card_Layout_Linear.setLayoutParams(lp);
                Card_Banner.setImageBitmap(obj.getPicture());
                Card_Banner.setVisibility(ImageView.VISIBLE);
            } else {
                lp.setMargins(0,0,0,0);
                Card_Layout_Linear.setLayoutParams(lp);
                Card_Banner.setVisibility(ImageView.GONE);
            }
        }

        private void initEditBtn(final ToDoObject obj) {
            FloatingActionButton Card_Button_Edit = (FloatingActionButton) Card.findViewById(R.id.Card_Button_Edit);
            Card_Button_Edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(v.getContext(), EditActivity.class);
                    intent.putExtra("id_task", obj.getId());
                    mainActivity.startActivityForResult(intent, 1000);
                }
            });
        }

        /**
         * @param Card_Button_State FloatingActionButton
         * @param obj               task format as JSONObject
         */

        private void initDoneBtn(FloatingActionButton Card_Button_State, final ToDoObject obj)  {
            switch (obj.getState()) {
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
                    obj.setState((obj.getState() + 1) % 2);
                    obj.save();
                    mainActivity.mAdapter.updateElems();
                }
            });
        }

        /**
         * @param obj task format as JSONObject
         */
        private void initCardClick(final ToDoObject obj) {
            Card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    intent = new Intent(v.getContext(), ViewActivity.class);
                    intent.putExtra("id_task", obj.getId());
                    mainActivity.startActivityForResult(intent, 1000);
                }
            });

        }

        public void setInfo(ToDoObject task) {
            setLayoutParam();
            TextView Card_Text_Title = (TextView) Card.findViewById(R.id.Card_Text_Title);
            TextView Card_Text_Desc = (TextView) Card.findViewById(R.id.Card_Text_Desc);
            FloatingActionButton Card_Button_State = (FloatingActionButton) Card.findViewById(R.id.Card_Button_State);
            try {
                Card_Text_Desc.setText(task.getDesc());
                Card_Text_Title.setText(task.getTitle());
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

