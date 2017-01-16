package fernandez.quentin.todolist.list;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.json.JSONException;

import fernandez.quentin.todolist.activity.MainActivity;

/**
 * Created by quent on 08/01/2017.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private final int mSpaceHeight;

    public DividerItemDecoration(int mSpaceHeight) {
        this.mSpaceHeight = mSpaceHeight;
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                               RecyclerView.State state) {
        outRect.bottom = mSpaceHeight;
        outRect.top = mSpaceHeight;
        outRect.left = mSpaceHeight;
        outRect.right = mSpaceHeight;
        final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int position = params.getViewAdapterPosition();
        if (position >= MainActivity.mAdapter.getItemCount() || position < 0)
            return;
        try {
            if (!MainActivity.mAdapter.getItem(position).getString("title").toLowerCase().contains(MainActivity.Filter.toLowerCase())) {
                outRect.setEmpty();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            outRect.setEmpty();
        }
    }
}
