package com.eloquentbit.taskee.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.eloquentbit.taskee.R;
import com.eloquentbit.taskee.adapters.CompletedTaskRecyclerViewAdapter;
import com.eloquentbit.taskee.adapters.DividerItemDecoration;
import com.eloquentbit.taskee.models.Task;

import io.realm.Realm;
import io.realm.Sort;

public class CompletedTasksActivity extends AppCompatActivity {

    private static final String TAG = CompletedTasksActivity.class.getCanonicalName();

    private Realm realm;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_completed_tasks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.completed_task_activity_title);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();

        recyclerView = (RecyclerView) findViewById(R.id.rv_completed_task);
        setupRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_completed_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miDeleteCompleted:
                deleteCompletedTasks();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    private void setupRecyclerView() {

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load active tasks
        final CompletedTaskRecyclerViewAdapter adapter = new CompletedTaskRecyclerViewAdapter(this,
                realm.where(Task.class)
                        .equalTo(Task.COMPLETED, true)
                        .findAllSorted(Task.COMPLETED, Sort.DESCENDING));

        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    private void deleteCompletedTasks() {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class)
                        .equalTo(Task.COMPLETED, true)
                        .findAll()
                        .deleteAllFromRealm();
            }
        });
    }

    public void markTaskIncomplete(final Integer taskId) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task mTask = realm.where(Task.class).equalTo(Task.ID, taskId).findFirst();

                mTask.setCompleted(false);
            }
        });
    }
}
