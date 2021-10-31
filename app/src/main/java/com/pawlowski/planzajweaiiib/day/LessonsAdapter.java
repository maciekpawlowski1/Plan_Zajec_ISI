package com.pawlowski.planzajweaiiib.day;

import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pawlowski.planzajweaiiib.MainActivity;
import com.pawlowski.planzajweaiiib.PlannedLessonModel;
import com.pawlowski.planzajweaiiib.R;
import com.pawlowski.planzajweaiiib.database.DBHandler;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.LessonHolder> {

    List<LessonModel> lessons = new ArrayList<>();
    MainActivity activity;
    String dayDate;
    boolean offline = false;

    LessonsAdapter(MainActivity activity, String dayDate)
    {
        this.activity = activity;
        this.dayDate = dayDate;
    }

    @NonNull
    @Override
    public LessonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LessonHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lesson_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LessonHolder holder, int position) {
        LessonModel currentLesson = lessons.get(position);
        holder.lessonTittleText.setText(currentLesson.getTittle());
        holder.teacherText.setText(currentLesson.getTeacher());
        holder.roomText.setText(currentLesson.getRoom());
        holder.timeText.setText(currentLesson.getTime());
        if(currentLesson.getGroup() != null)
            holder.groupText.setText("gr. " + currentLesson.getGroup());
        else
            holder.groupText.setText(" ");

        boolean isHidden = DBHandler.getInstance(activity.getApplicationContext()).isLessonHidden(currentLesson.getTittle(), currentLesson.getTime(), dayDate);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isAdditional = currentLesson.getGroup() == null;
                String text = isAdditional ? currentLesson.getFullInfo() : currentLesson.getFullInfo() + "\n" + currentLesson.getTime();
                activity.showPreviewDialog(text, isAdditional, isHidden, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //hide button click listener
                        if(!isHidden)
                        {
                            if(isAdditional)
                            {
                                DBHandler.getInstance(activity.getApplicationContext()).hideLesson(currentLesson.getTittle(), currentLesson.getTime(), dayDate, ((PlannedLessonModel)currentLesson).getId());

                            }
                            else
                            {
                                DBHandler.getInstance(activity.getApplicationContext()).hideLesson(currentLesson.getTittle(), currentLesson.getTime(), dayDate);

                            }

                        }
                        else
                        {
                            DBHandler.getInstance(activity.getApplicationContext()).showLesson(currentLesson.getTittle(), currentLesson.getTime(), dayDate);

                        }
                        activity.hidePreviewDialog();
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //delete button click listener
                        activity.openDeleteDialog(view, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Yes listener
                                DBHandler.getInstance(activity.getApplicationContext()).deleteLesson(((PlannedLessonModel)currentLesson).getId());
                                dialogInterface.dismiss();
                                activity.hidePreviewDialog();

                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //No listener
                                dialogInterface.dismiss();
                            }
                        });
                    }
                });
            }
        });

        if(currentLesson.getGroup() == null)
        {

            holder.cardView.getBackground().setTint(activity.getResources().getColor(R.color.additional_lessons_color));
        }
        else
        {
            if(!offline)
            {
                holder.cardView.getBackground().setTint(activity.getResources().getColor(R.color.main_color));

            }
            else
            {
                holder.cardView.getBackground().setTint(activity.getResources().getColor(R.color.offline_color));

            }
        }

        if(isHidden)
        {
            holder.cardView.setAlpha(0.35f);
        }
        else
        {
            holder.cardView.setAlpha(1.0f);

        }
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void setLessons(List<LessonModel>lessons, String dayString, boolean offline)
    {
        this.lessons = lessons;
        this.dayDate = dayString;
        this.offline = offline;
        notifyDataSetChanged();
    }

    public class LessonHolder extends RecyclerView.ViewHolder
    {
        @BindView(R.id.tittle_lesson_card)
        TextView lessonTittleText;

        @BindView(R.id.room_lesson_card)
        TextView roomText;

        @BindView(R.id.teacher_lesson_card)
        TextView teacherText;

        @BindView(R.id.time_lesson_card)
        TextView timeText;

        @BindView(R.id.group_lesson_card)
        TextView groupText;

        @BindView(R.id.card_view_lesson_card)
        CardView cardView;


        public LessonHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
