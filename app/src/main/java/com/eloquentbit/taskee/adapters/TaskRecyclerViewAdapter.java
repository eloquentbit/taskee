package com.eloquentbit.taskee.adapters;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.eloquentbit.taskee.R;
import com.eloquentbit.taskee.activities.MainActivity;
import com.eloquentbit.taskee.models.Task;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

public class TaskRecyclerViewAdapter extends
        RealmRecyclerViewAdapter<Task, TaskRecyclerViewAdapter.TaskViewHolder> {

    private final static String TAG = TaskRecyclerViewAdapter.class.getCanonicalName();

    private final MainActivity activity;
    private OnItemClickListener listener;
    private OnItemLongClickListener longClickListener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
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

        String formattedDate = formatDateView(task.getDueDate());
        holder.tvDueDate.setText(formattedDate);

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

        holder.cbCompleted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.toggleCompleted(task.getId());
            }
        });

        holder.btnDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.showCalendar(task.getId());
            }
        });

        holder.tvDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.removeDueDate(task.getId());
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
        CheckBox cbCompleted;
        ImageView imgPriority;
        ImageButton btnDueDate;
        TextView tvDueDate;

        TaskViewHolder(final View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.txt_title);
            cbCompleted = (CheckBox) itemView.findViewById(R.id.cb_completed);
            imgPriority = (ImageView) itemView.findViewById(R.id.img_priority);
            btnDueDate = (ImageButton) itemView.findViewById(R.id.btn_due_date);
            tvDueDate = (TextView) itemView.findViewById(R.id.txt_due_date);


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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (longClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            longClickListener.onItemLongClick(itemView, position);
                        }
                    }
                    return true;
                }
            });
        }
    }
}
