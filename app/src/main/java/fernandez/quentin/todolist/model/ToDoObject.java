package fernandez.quentin.todolist.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orm.SugarRecord;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by quent on 20/01/2017.
 */

public class ToDoObject extends SugarRecord {
    String _title;
    String _desc;
    String _date;
    String _time;
    byte[] _picture;
    int _state;

    public ToDoObject() {

    }

    public static ToDoObject getTask(long id) {
        String str = "" + id;
        return findById(ToDoObject.class, new String[]{str}).get(0);
    }

    public static List<ToDoObject> getAllTask() {
        List<ToDoObject> ret = null;
        try {
            ret = listAll(ToDoObject.class);
        } catch (android.database.sqlite.SQLiteException e) {
            ret = new ArrayList<>();
        }
        return ret;
    }

    public static ToDoObject createTask() {
        ToDoObject ret = new ToDoObject();
        ret._title = null;
        ret._desc = null;
        ret._date = null;
        ret._time = null;
        ret._picture = null;
        ret._state = 0;
        return ret;
    }

    public static void swapTask(ToDoObject from, ToDoObject to) {
        String _title = from._title;
        String _desc = from._desc;
        String _date = from._date;
        String _time = from._time;
        byte[] _picture = from._picture;
        int _state = from._state;

        from._title = to._title;
        from._desc = to._desc;
        from._date = to._date;
        from._time = to._time;
        from._picture = to._picture;
        from._state = to._state;

        to._title = _title;
        to._desc = _desc;
        to._date = _date;
        to._time = _time;
        to._picture = _picture;
        to._state = _state;
        to.save();
        from.save();
    }

    public String getTitle() {
        return _title;
    }

    public ToDoObject setTitle(String title) {
        _title = title;
        return this;
    }

    public ToDoObject setState(int state) {
        _state = state;
        return this;
    }

    public int getState() {
        return _state;
    }

    public String getDesc() {
        return _desc;
    }

    public ToDoObject setDesc(String desc) {
        _desc = desc;
        return this;
    }

    public String getDate() {
        return _date;
    }

    public ToDoObject setDate(String date) {
        _date = date;
        return this;
    }

    public String getTime() {
        return _time;
    }

    public ToDoObject setTime(String time) {
        _time = time;
        return this;
    }

    public Bitmap getPicture() {
        if (_picture == null)
            return null;
        return BitmapFactory.decodeByteArray(_picture, 0, _picture.length);
    }

    public ToDoObject setPicture(Bitmap picture) {
        if (picture == null)
            _picture = null;
        else {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            picture.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
            _picture = byteArrayOutputStream.toByteArray();
        }
        return this;
    }
}
