package fernandez.quentin.todolist.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by quent on 17/01/2017.
 */

public class Share {

    /**
     * Share task in compatible Calendar app
     * @param task task you want to share
     * @param ctx Context of your current activity
     */
    public static void ShareTask(JSONObject task, Context ctx) {
        SimpleDateFormat _date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date d = _date_format.parse(task.getString("date") + " " + task.getString("time"));
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, d.getTime())
                    .putExtra(CalendarContract.Events.TITLE, task.getString("title"))
                    .putExtra(CalendarContract.Events.DESCRIPTION, task.getString("desc"));
            ctx.startActivity(intent);
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }
    }
}