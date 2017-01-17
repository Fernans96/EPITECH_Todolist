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
    public static void ShareNote(JSONObject note, Context ctx) {
        SimpleDateFormat _date_format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date d = _date_format.parse(note.getString("date") + " " + note.getString("time"));
            Intent intent = new Intent(Intent.ACTION_INSERT)
                    .setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, d.getTime())
                    .putExtra(CalendarContract.Events.TITLE, note.getString("title"))
                    .putExtra(CalendarContract.Events.DESCRIPTION, note.getString("desc"));
            ctx.startActivity(intent);
        } catch (ParseException | JSONException e) {
            e.printStackTrace();
        }
    }
}