package com.eloquentbit.taskee.adapters;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eloquentbit.taskee.R;
import com.eloquentbit.taskee.activities.CompletedTasksActivity;
import com.eloquentbit.taskee.models.Task;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class CompletedTaskRecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Task, CompletedTaskRecyclerViewAdapter.TaskViewHolder> {

    private final static String TAG = CompletedTaskRecyclerViewAdapter.class.getCanonicalName();

    private final CompletedTasksActivity activity;

    public CompletedTaskRecyclerViewAdapter(CompletedTasksActivity activity, OrderedRealmCollection<Task> data) {
        super(activity, data, true);
        this.activity = activity;
    }

    @Override
    public CompletedTaskRecyclerViewAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.completed_task_row, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CompletedTaskRecyclerViewAdapter.TaskViewHolder holder, int position) {
        final Task task = getData().get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.tvDescription.setText(task.getDescription());

        if (task.getDueDate() != null && !task.getDueDate().isEmpty()) {
            String formattedDate = formatDateView(task.getDueDate());
            holder.tvDueDate.setVisibility(View.VISIBLE);
            holder.tvDueDate.setText(formattedDate);
        } else {
            holder.tvDueDate.setVisibility(View.GONE);
        }

        holder.tvDueDate.setPaintFlags(holder.tvDueDate.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        switch (task.getPriority()) {
            case 0:
                holder.imgPriority.setImageResource(R.drawable.ic_action_flag_low_priority);
                break;
            case 1:
                holder.imgPriority.setImageResource(R.drawable.ic_action_flag_medium_priority);
                break;
            case 2:
                holder.imgPriority.setImageResource(R.drawable.ic_action_flag_high_priority);
        }

        holder.btnUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.markTaskIncomplete(task.getId());
            }
        });
    }

    private String formatDateView(String date) {
        if (date != null && !date.isEmpty()) {
            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-M-d");
            LocalDate dueDate = LocalDate.parse(date, fmt);
            return dueDate.toString("d MMM");
        } else {
            return "";
        }
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvDescription;
        ImageView imgPriority;
        TextView tvDueDate;
        ImageButton btnUndo;

        TaskViewHolder(final View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.txt_title);
            tvDescription = (TextView) itemView.findViewById(R.id.txt_description);
            imgPriority = (ImageView) itemView.findViewById(R.id.img_priority);
            tvDueDate = (TextView) itemView.findViewById(R.id.txt_due_date);
            btnUndo = (ImageButton) itemView.findViewById(R.id.btn_undo);
        }
    }
}
