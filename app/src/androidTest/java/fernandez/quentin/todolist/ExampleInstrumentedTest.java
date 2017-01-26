package fernandez.quentin.todolist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.orm.SugarContext;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import fernandez.quentin.todolist.model.ToDoObject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private final static String LoremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla consectetur erat sit amet diam commodo eleifend vitae vel ante. Nullam et risus ante. Fusce venenatis ligula non turpis cursus, in.";

    @Before
    public void EnterEntryInDatabase() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        SugarContext.init(appContext);

        for (int i = 0; i < 50; i++) {
            ToDoObject todo = ToDoObject.createTask();
            todo.setDate("09/04/1996");
            todo.setTime("12:00");
            todo.setState(0);
            todo.setTitle("Instrumented Test : " + (i + 1));
            todo.setDesc(LoremIpsum);
            todo.save();
        }

        SugarContext.terminate();
    }

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("fernandez.quentin.todolist", appContext.getPackageName());
    }

    @Test
    public void TestCreateTask() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        SugarContext.init(appContext);

        int nbr_elem_b = ToDoObject.getAllTask().size();

        ToDoObject todo = ToDoObject.createTask();
        todo.setDate("09/04/1996");
        todo.setTime("12:00");
        todo.setState(0);
        todo.setTitle("Exemple");
        todo.setDesc("Test Exemple");
        todo.save();

        int nbr_elem_a = ToDoObject.getAllTask().size();

        SugarContext.terminate();
        Log.d("DataBaseTest", "Old :" + nbr_elem_b);
        Log.d("DataBaseTest", "New :" + nbr_elem_a);
        assertEquals(nbr_elem_b + 1, nbr_elem_a);
    }

    @Test
    public void TestEditTask() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        SugarContext.init(appContext);
        String before = null;
        String after = null;

        List<ToDoObject> tol = ToDoObject.getAllTask();
        if (!tol.isEmpty()) {
            ToDoObject obj = tol.get(0);
            before = obj.getTitle();
            obj.setTitle("Test Edit");
            obj.save();
            tol = ToDoObject.getAllTask();
            obj = tol.get(0);
            after = obj.getTitle();
        }
        SugarContext.terminate();
        Log.d("DataBaseTest", "Old :" + before);
        Log.d("DataBaseTest", "New :" + after);
        if (!tol.isEmpty())
            assertFalse(before.equals(after));
    }

    @Test
    public void TestDeleteTask() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        SugarContext.init(appContext);

        List<ToDoObject> tol = ToDoObject.getAllTask();
        int nbr_elem_b = tol.size();
        if (!tol.isEmpty()) {
            tol.get(0).delete();
        }
        int nbr_elem_a = ToDoObject.getAllTask().size();
        SugarContext.terminate();
        Log.d("DataBaseTest", "Old :" + nbr_elem_b);
        Log.d("DataBaseTest", "New :" + nbr_elem_a);
        if (!tol.isEmpty())
            assertEquals(nbr_elem_b - 1, nbr_elem_a);
    }

    @After
    public void InitData() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        SugarContext.init(appContext);

        /**
         * Clear Test Data
         */

        for (ToDoObject obj : ToDoObject.getAllTask()) {
            obj.delete();
        }

        /**
         * Init Data for presentation
         */

        Bitmap bitmap = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.preview);

        for (int i = 0; i < 20; i++) {
            ToDoObject todo = ToDoObject.createTask();
            todo.setDate("09/04/1996");
            todo.setTime("12:00");
            todo.setState(i % 3);
            todo.setPicture(i % 5 == 0 ? bitmap : null);
            todo.setTitle("Generate Data : " + (i + 1));
            todo.setDesc(LoremIpsum);
            todo.save();
        }

        SugarContext.terminate();
    }

}
