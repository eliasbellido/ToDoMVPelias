package com.beyondthecode.todomvpelias.agregarEditTask;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.beyondthecode.todomvpelias.R;
import com.beyondthecode.todomvpelias.TestUtils;
import com.beyondthecode.todomvpelias.data.FalsoTasksRemoteDataSource;
import com.beyondthecode.todomvpelias.data.Task;
import com.beyondthecode.todomvpelias.data.source.TasksRepository;
import com.beyondthecode.todomvpelias.util.EspressoIdlingResource;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class AgregarEditTaskScreenTest {


    private static final String TASK_ID = "1";

    /**
     * {@link IntentsTestRule} is an {@link ActivityTestRule} which inits and releases Espresso
     * Intents before and after each test run.
     *
     * <p>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<AgregarEditTaskActivity> mActivityTestRule =
            new ActivityTestRule<>(AgregarEditTaskActivity.class, false, false);

    /**
     * Prepare your test fixture for this test. In this case we register an IdlingResources with
     * Espresso. IdlingResource resource is a great way to tell Espresso when your app is in an
     * idle state. This helps Espresso to synchronize your test actions, which makes tests
     * significantly more reliable.
     */
    @Before
    public void registerIdlingResource() {
        Espresso.registerIdlingResources(EspressoIdlingResource.obtenerIdlingRecurso());
    }

    /**
     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
     */
    @After
    public void unregisterIdlingResource() {
        Espresso.unregisterIdlingResources(EspressoIdlingResource.obtenerIdlingRecurso());
    }

    @Test
    public void emptyTask_isNotSaved() {
        // Launch activity to add a new task
        launchNewTaskActivity(null);

        // Add invalid title and description combination
        onView(withId(R.id.add_task_title)).perform(clearText());
        onView(withId(R.id.add_task_description)).perform(clearText());
        // Try to save the task
        onView(withId(R.id.fab_edit_task_done)).perform(click());

        // Verify that the activity is still displayed (a correct task would close it).
        onView(withId(R.id.add_task_title)).check(matches(isDisplayed()));
    }

    @Test
    public void toolbarTitle_newTask_persistsRotation() {
        // Launch activity to add a new task
        launchNewTaskActivity(null);

        // Check that the toolbar shows the correct title
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_task)));

        // Rotate activity
        TestUtils.rotateOrientation(mActivityTestRule.getActivity());

        // Check that the toolbar title is persisted
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.add_task)));
    }

    @Test
    public void toolbarTitle_editTask_persistsRotation() {
        // Put a task in the repository and start the activity to edit it
        TasksRepository.destruirInstancia();
        FalsoTasksRemoteDataSource.obtenerInstancia().agregarTasks(new Task("Title1", "", TASK_ID, false));
        launchNewTaskActivity(TASK_ID);

        // Check that the toolbar shows the correct title
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_task)));

        // Rotate activity
        TestUtils.rotateOrientation(mActivityTestRule.getActivity());

        // check that the toolbar title is persisted
        onView(withId(R.id.toolbar)).check(matches(withToolbarTitle(R.string.edit_task)));
    }

    /**
     * @param taskId is null if used to add a new task, otherwise it edits the task.
     */
    private void launchNewTaskActivity(@Nullable String taskId) {
        Intent intent = new Intent(InstrumentationRegistry.getInstrumentation()
                .getTargetContext(), AgregarEditTaskActivity.class);

        intent.putExtra(AgregarEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        mActivityTestRule.launchActivity(intent);
    }

    /**
     * Matches the toolbar title with a specific string resource.
     *
     * @param resourceId the ID of the string resource to match
     */
    public static Matcher<View> withToolbarTitle(final int resourceId) {
        return new BoundedMatcher<View, Toolbar>(Toolbar.class) {

            @Override
            public void describeTo(Description description) {
                description.appendText("with toolbar title from resource id: ");
                description.appendValue(resourceId);
            }

            @Override
            protected boolean matchesSafely(Toolbar toolbar) {
                CharSequence expectedText = "";
                try {
                    expectedText = toolbar.getResources().getString(resourceId);
                } catch (Resources.NotFoundException ignored) {
                    /* view could be from a context unaware of the resource id. */
                }
                CharSequence actualText = toolbar.getTitle();
                return expectedText.equals(actualText);
            }
        };
    }
}
