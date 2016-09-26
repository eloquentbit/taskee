package com.eloquentbit.taskee.adapters;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.eloquentbit.taskee.R;
import com.eloquentbit.taskee.activities.MainActivity;
import com.eloquentbit.taskee.models.Task;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class TaskRecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Task, TaskRecyclerViewAdapter.TaskViewHolder> {

    private final static String TAG = TaskRecyclerViewAdapter.class.getCanonicalName();

    private final MainActivity activity;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public TaskRecyclerViewAdapter(MainActivity activity, OrderedRealmCollection<Task> data) {
        super(activity, data, true);
        this.activity = activity;
    }

    @Override
    public TaskRecyclerViewAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.task_row, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final TaskRecyclerViewAdapter.TaskViewHolder holder, int position) {
        final Task task = getData().get(position);

        holder.tvTitle.setText(task.getTitle());
        holder.cbCompleted.setChecked(task.isCompleted());
        holder.cbCompleted.setOnCheckedChangeListener(null);

        if (task.isCompleted()) {
            holder.tvTitle.setPaintFlags(holder.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        holder.cbCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                activity.toggleCompleted(holder.tvTitle, task.getTitle(), isChecked);
            }
        });
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        CheckBox cbCompleted;

        TaskViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.txt_title);
            cbCompleted = (CheckBox) itemView.findViewById(R.id.cb_completed);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {

                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION && !cbCompleted.isSelected()) {
                            listener.onItemClick(itemView, position);
                        }
                    }
                }
            });
        }
    }
}
