package com.eloquentbit.taskee.activities;

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
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.eloquentbit.taskee.R;
import com.eloquentbit.taskee.adapters.DividerItemDecoration;
import com.eloquentbit.taskee.adapters.TaskRecyclerViewAdapter;
import com.eloquentbit.taskee.models.Task;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.joda.time.LocalDate;

import io.realm.Realm;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity
        implements DatePickerDialog.OnDateSetListener {
    private final String TAG = MainActivity.class.getCanonicalName();

    private Realm realm;

    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private View positiveAction;
    private EditText edtTitle;
    private EditText edtDescription;
    private Spinner spnPriority;

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

        FloatingActionButton fabButton = (FloatingActionButton) findViewById(R.id.fab);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog.Builder addDialogBuilder =
                        buildCustomDialog(R.string.add_dialog_title,
                                R.layout.fragment_task,
                                R.string.btn_add_task,
                                android.R.string.cancel);

                addDialogBuilder.onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which.name()) {
                            case "POSITIVE":
                                Task mTask = new Task();

                                mTask.setId(Task.getNextId());
                                mTask.setTitle(edtTitle.getText().toString().trim());
                                mTask.setDescription(edtDescription.getText().toString().trim());
                                mTask.setPriority(spnPriority.getSelectedItemPosition());

                                storeOrUpdateTask(mTask, R.string.success_message_add_task);
                        }
                    }
                });

                MaterialDialog addTaskDialog = addDialogBuilder.build();

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

                spnPriority = (Spinner) addTaskDialog.getCustomView().findViewById(R.id.spinner_priority);

                addTaskDialog.show();
                positiveAction.setEnabled(false);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load active tasks
        final TaskRecyclerViewAdapter adapter = new TaskRecyclerViewAdapter(this,
                realm.where(Task.class)
                        .equalTo(Task.COMPLETED, false)
                        .findAllSortedAsync(Task.COMPLETED, Sort.DESCENDING));

        // Listener for RecyclerView's item in order to edit a task
        adapter.setOnItemClickListener(new TaskRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, final int position) {
                final String title = ((TextView) itemView.findViewById(R.id.txt_title)).getText().toString();

                // Build the Edit dialog
                final MaterialDialog.Builder editDialogBuilder =
                        buildCustomDialog(R.string.edit_dialog_title, R.layout.fragment_task,
                                R.string.btn_save_task, android.R.string.cancel);

                // Configure callback when user press Save button
                editDialogBuilder.onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which.name()) {
                            case "POSITIVE":
                                // Load task to edit
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Task mTask = realm.where(Task.class).equalTo(Task.ID, taskId).findFirst();

                                        mTask.setTitle(edtTitle.getText().toString().trim());
                                        mTask.setDescription(edtDescription.getText().toString().trim());
                                        mTask.setPriority(spnPriority.getSelectedItemPosition());
                                    }
                                }, new Realm.Transaction.OnSuccess() {
                                    @Override
                                    public void onSuccess() {
                                        Snackbar.make(coordinatorLayout, R.string.success_message_edit_task, Snackbar.LENGTH_SHORT).show();
                                    }
                                }, new Realm.Transaction.OnError() {
                                    @Override
                                    public void onError(Throwable error) {
                                        Snackbar.make(coordinatorLayout, R.string.error_message_task, Snackbar.LENGTH_SHORT).show();
                                    }
                                });

                                break;
                        }
                    }
                });

                // Finalize the Edit Dialog
                final MaterialDialog editTaskDialog = editDialogBuilder.build();

                // Load task to edit and populate Edit Dialog
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Task mTask = realm.where(Task.class).equalTo(Task.TITLE, title)
                                .findFirst();

                        taskId = mTask.getId();
                        edtTitle = (EditText) editTaskDialog.getCustomView().findViewById(R.id.edt_task_title);
                        edtDescription = (EditText) editTaskDialog.getCustomView().findViewById(R.id.edt_task_description);
                        spnPriority = (Spinner) editTaskDialog.getCustomView().findViewById(R.id.spinner_priority);

                        edtTitle.setText(mTask.getTitle());
                        edtDescription.setText(mTask.getDescription());
                        spnPriority.setSelection(mTask.getPriority());
                    }
                });

                editTaskDialog.show();
            }
        });

        adapter.setOnItemLongClickListener(new TaskRecyclerViewAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View itemView, int position) {
                //final String title = ((TextView) itemView.findViewById(R.id.txt_title)).getText().toString();
                final Task mTask = adapter.getData().get(position);

                // Build confirmation dialog
                new MaterialDialog.Builder(MainActivity.this)
                        .title(R.string.delete_dialog_title)
                        .content(R.string.message_delete_task)
                        .positiveText(R.string.btn_delete_task)
                        .negativeText(R.string.btn_cancel_operation)
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                switch (which.name()) {
                                    case "POSITIVE":
                                        deleteTask(mTask.getId(), R.string.success_message_delete_task);
                                        break;
                                }
                            }
                        })
                        .show();
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

    @Override
    public void onResume() {
        super.onResume();
        DatePickerDialog dpd = (DatePickerDialog) getFragmentManager().findFragmentByTag("Datepickerdialog");

        if (dpd != null) dpd.setOnDateSetListener(this);
    }

    @Override
    public void onDateSet(DatePickerDialog view, final int year, final int monthOfYear, final int dayOfMonth) {

        final int month = monthOfYear + 1;

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task mTask = realm.where(Task.class).equalTo(Task.ID, taskId).findFirst();

                LocalDate dueDate = new LocalDate(year, month, dayOfMonth);

                mTask.setDueDate(dueDate.toString());
            }
        });
    }

    public void toggleCompleted(final Integer taskId) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Task mTask = realm.where(Task.class).equalTo(Task.ID, taskId)
                        .findFirst();

                mTask.setCompleted(!mTask.isCompleted());
                realm.copyToRealmOrUpdate(mTask);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Snackbar.make(coordinatorLayout, R.string.message_task_completed, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void storeOrUpdateTask(final Task task, final int successMessageResource) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(task);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Snackbar.make(coordinatorLayout, successMessageResource, Snackbar.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Snackbar.make(coordinatorLayout, R.string.error_message_task, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteTask(final Integer id, final int successMessageResource) {

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Task.class).equalTo(Task.ID, id)
                        .findAll()
                        .deleteAllFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Snackbar.make(coordinatorLayout, successMessageResource, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void showCalendar(Integer taskId) {
        this.taskId = taskId;
        LocalDate now = LocalDate.now();

        DatePickerDialog dpd = DatePickerDialog.newInstance(
                MainActivity.this,
                now.getYear(),
                now.getMonthOfYear() - 1,
                now.getDayOfMonth()
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    public void removeDueDate(final Integer taskId) {

        new MaterialDialog.Builder(MainActivity.this)
                .content(R.string.message_remove_due_date)
                .positiveText(R.string.btn_clear_due_date)
                .negativeText(R.string.btn_cancel_operation)
                .onAny(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        switch (which.name()) {
                            case "POSITIVE":
                                realm.executeTransactionAsync(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        Task mTak = realm.where(Task.class).equalTo(Task.ID, taskId).findFirst();
                                        mTak.setDueDate("");
                                    }
                                });
                        }
                    }
                })
                .show();
    }

    private MaterialDialog.Builder buildCustomDialog(int titleResource,
                                                     int customViewResource,
                                                     int positiveTextResource,
                                                     int negativeTextResource) {

        return new MaterialDialog.Builder(this)
                .title(titleResource)
                .customView(customViewResource, true)
                .positiveText(positiveTextResource)
                .negativeText(negativeTextResource);
    }

}
