package com.eloquentbit.taskee.activities;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.eloquentbit.taskee.R;
import com.eloquentbit.taskee.adapters.DividerItemDecoration;
import com.eloquentbit.taskee.adapters.TaskRecyclerViewAdapter;
import com.eloquentbit.taskee.models.Task;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getCanonicalName();

    private Realm realm;

    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private View positiveAction;
    private EditText edtTitle;
    private EditText edtDescription;

    private int taskId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);

        realm = Realm.getDefaultInstance();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        setupRecyclerView();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab_add_task);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog addTaskDialog = new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.add_dialog_title)
                        .customView(R.layout.fragment_add_task, true)
                        .positiveText(R.string.btn_add_task)
                        .negativeText(android.R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Task mTask = new Task();

                                mTask.setTitle(edtTitle.getText().toString().trim());
                                mTask.setDescription(edtDescription.getText().toString().trim());

                                addOrUpdateTask(mTask);
                            }
                        }).build();

                positiveAction = addTaskDialog.getActionButton(DialogAction.POSITIVE);

                edtTitle = (EditText) addTaskDialog.getCustomView().findViewById(R.id.edt_task_title);
                edtTitle.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                edtDescription = (EditText) addTaskDialog.getCustomView().findViewById(R.id.edt_task_description);

                addTaskDialog.show();
                positiveAction.setEnabled(false);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load active tasks
        TaskRecyclerViewAdapter adapter = new TaskRecyclerViewAdapter(this,
                realm.where(Task.class)
                        .equalTo("isCompleted", false)
                        .findAllSortedAsync("_id"));

        // Listener for RecyclerView's item in order to edit a task
        adapter.setOnItemClickListener(new TaskRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                final String title = ((TextView) itemView.findViewById(R.id.txt_title)).getText().toString();

                // Build the Edit dialog
                MaterialDialog.Builder buildDialog = buildDialog();
                // Configure callback
                buildDialog.onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which.name()) {
                            case "POSITIVE":
                                Task mTask = new Task();

                                mTask.setId(taskId);
                                mTask.setTitle(edtTitle.getText().toString().trim());
                                mTask.setDescription(edtDescription.getText().toString().trim());

                                addOrUpdateTask(mTask);
                                break;
                        }
                    }
                });

                // Finalize the Edit Dialog
                final MaterialDialog editDialog = buildDialog.build();

                // Load task to edit and populate Edit Dialog
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Task mTask = realm.where(Task.class).equalTo(Task.TITLE, title)
                                .findFirst();
                        taskId = mTask.getId();
                        edtTitle = (EditText) editDialog.getCustomView().findViewById(R.id.edt_task_title);
                        edtDescription = (EditText) editDialog.getCustomView().findViewById(R.id.edt_task_description);

                        edtTitle.setText(mTask.getTitle());
                        edtDescription.setText(mTask.getDescription());
                    }
                });

                editDialog.show();

                //editTask(title);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    public void toggleCompleted(final View itemView, final String title, final boolean isCompleted) {
        final TextView tvTitle = (TextView) itemView;
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo(Task.TITLE, title)
                        .findFirst().setCompleted(isCompleted);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                if (isCompleted) {
                    tvTitle.setPaintFlags(tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    Snackbar.make(coordinatorLayout, R.string.message_task_completed, Snackbar.LENGTH_SHORT).show();
                } else {
                    tvTitle.setPaintFlags(Paint.LINEAR_TEXT_FLAG);
                }
            }
        });
    }

    public void addOrUpdateTask(final Task task) {
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(task);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Snackbar.make(coordinatorLayout, R.string.success_message_task, Snackbar.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Snackbar.make(coordinatorLayout, R.string.error_message_task, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

//    public void editTask(String title) {
//
//        final Task editTask = realm.where(Task.class).equalTo(Task.TITLE, title)
//                .findFirst();
//
//        MaterialDialog editTaskDialog = new MaterialDialog.Builder(MainActivity.this)
//                .title(R.string.edit_dialog_title)
//                .customView(R.layout.fragment_add_task, true)
//                .positiveText(R.string.btn_save_task)
//                .negativeText(R.string.btn_delete_task)
//                .neutralText(android.R.string.cancel)
//                .onAny(new MaterialDialog.SingleButtonCallback() {
//
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        switch (which.name()) {
//                            case "NEGATIVE":
//                                deleteTask(editTask.getTitle());
//                                break;
//                            case "POSITIVE":
//
//                                break;
//                            case "NEUTRAL":
//                                Log.d(TAG, "NEUTRAL clicked");
//                                break;
//                        }
//                    }
//                })
//                .build();
//
//        edtTitle = (EditText) editTaskDialog.getCustomView().findViewById(R.id.edt_task_title);
//        edtDescription = (EditText) editTaskDialog.getCustomView().findViewById(R.id.edt_task_description);
//        edtTitle.setText(editTask.getTitle());
//        edtDescription.setText(editTask.getDescription());
//
//        editTaskDialog.show();
//    }

    public void deleteTask(final String title) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo(Task.TITLE, title)
                        .findAll()
                        .deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Snackbar.make(coordinatorLayout, "Task deleted", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private MaterialDialog.Builder buildDialog() {

        return new MaterialDialog.Builder(this)
                .title(R.string.edit_dialog_title)
                .customView(R.layout.fragment_add_task, true)
                .positiveText(R.string.btn_save_task)
                .negativeText(android.R.string.cancel);
    }
}
