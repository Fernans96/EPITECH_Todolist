package fernandez.quentin.todolist.list;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
    }
}
