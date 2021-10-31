package com.pawlowski.planzajweaiiib.day;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pawlowski.planzajweaiiib.MainActivity;
import com.pawlowski.planzajweaiiib.PlannedLessonModel;
import com.pawlowski.planzajweaiiib.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class DayFragment extends Fragment {

    List<LessonModel> lessons = new ArrayList<>();
    int dayNumber;
    String dayString = "";
    MainActivity activity;
    boolean offline = false;


    public DayFragment() {
        // Required empty public constructor

    }

    public DayFragment(int position, MainActivity activity) {
        this.activity = activity;
        this.dayNumber = position;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(activity.getMondayDate());
            Date d2 = new Date(d1.getTime() + (position * (24 * 60 * 60 * 1000)));
            String d2String = sdf.format(d2);
            dayString = d2String;
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @BindView(R.id.recycler_day_fragment)
    RecyclerView recyclerView;

    LessonsAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_day, container, false);

        ButterKnife.bind(this, view);

        adapter = new LessonsAdapter(activity, dayString);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getDownloadedLessonsFromMainActivity();


        return view;
    }

    public void refresh()
    {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date d1 = sdf.parse(activity.getMondayDate());
            Date d2 = new Date(d1.getTime() + (dayNumber * (24 * 60 * 60 * 1000)));
            String d2String = sdf.format(d2);
            dayString = d2String;
        } catch (ParseException e) {
            e.printStackTrace();
        }


        getDownloadedLessonsFromMainActivity();
    }



    public void getDownloadedLessonsFromMainActivity()
    {
        if(adapter == null || activity == null)
            return;
        lessons = activity.getLessons();
        if(lessons.size() == 0)
        {
            lessons = new ArrayList<>();
            lessons.addAll(activity.getSavedLessons());

            offline = true;
        }
        else
            offline = false;
        int myGroup = MainActivity.getSelectedGroup(activity);
        if(lessons != null)
        {
            lessons = lessons.stream().filter(new Predicate<LessonModel>() {
                @Override
                public boolean test(LessonModel lessonModel) {
                    return lessonModel.getDay().equals(dayString);//"2021-10-15");
                }
            }).filter(new Predicate<LessonModel>() {
                @Override
                public boolean test(LessonModel lessonModel) {
                    String group = lessonModel.getGroup();
                    switch (myGroup)
                    {
                        case 1:
                            return group.equals("wykład") || group.equals("1a") || group.equals("1");
                        case 2:
                            return group.equals("wykład") || group.equals("1b") || group.equals("1");
                        case 3:
                            return group.equals("wykład") || group.equals("2a") || group.equals("2");
                        case 4:
                            return group.equals("wykład") || group.equals("2b") || group.equals("2");
                        case 5:
                            return group.equals("wykład") || group.equals("3a") || (group.equals("3") && lessonModel.getTittle().contains("Analiza")) || (group.equals("3") && (lessonModel.getTittle().contains("Algebra") || lessonModel.getTittle().contains("Wstęp do informatyki")));
                        case 6:
                            return group.equals("wykład") || group.equals("3b") || (group.equals("3") && lessonModel.getTittle().contains("Analiza")) || (group.equals("4") && (lessonModel.getTittle().contains("Algebra") || lessonModel.getTittle().contains("Wstęp do informatyki")));
                        case 7:
                            return group.equals("wykład") || group.equals("4a") || (group.equals("4") && lessonModel.getTittle().contains("Analiza")) || (group.equals("5") && (lessonModel.getTittle().contains("Algebra") || lessonModel.getTittle().contains("Wstęp do informatyki")));

                        default:
                            return true;

                    }
                }
            }).distinct().collect(Collectors.toList());

            if(lessons.size() == 0)
            {
                offline = true;
                lessons = activity.getSavedLessons().stream().filter(new Predicate<LessonModel>() {
                    @Override
                    public boolean test(LessonModel lessonModel) {
                        return lessonModel.getDay().equals(dayString);//"2021-10-15");
                    }
                }).filter(new Predicate<LessonModel>() {
                    @Override
                    public boolean test(LessonModel lessonModel) {
                        String group = lessonModel.getGroup();
                        switch (myGroup)
                        {
                            case 1:
                                return group.equals("wykład") || group.equals("1a") || group.equals("1");
                            case 2:
                                return group.equals("wykład") || group.equals("1b") || group.equals("1");
                            case 3:
                                return group.equals("wykład") || group.equals("2a") || group.equals("2");
                            case 4:
                                return group.equals("wykład") || group.equals("2b") || group.equals("2");
                            case 5:
                                return group.equals("wykład") || group.equals("3a") || (group.equals("3") && lessonModel.getTittle().contains("Analiza")) || (group.equals("3") && (lessonModel.getTittle().contains("Algebra") || lessonModel.getTittle().contains("Wstęp do informatyki")));
                            case 6:
                                return group.equals("wykład") || group.equals("3b") || (group.equals("3") && lessonModel.getTittle().contains("Analiza")) || (group.equals("4") && (lessonModel.getTittle().contains("Algebra") || lessonModel.getTittle().contains("Wstęp do informatyki")));
                            case 7:
                                return group.equals("wykład") || group.equals("4a") || (group.equals("4") && lessonModel.getTittle().contains("Analiza")) || (group.equals("5") && (lessonModel.getTittle().contains("Algebra") || lessonModel.getTittle().contains("Wstęp do informatyki")));

                            default:
                                return true;

                        }
                    }
                }).distinct().collect(Collectors.toList());
            }

            lessons.addAll(activity.getAdditionalLessons().stream().filter(new Predicate<PlannedLessonModel>() {
                @Override
                public boolean test(PlannedLessonModel plannedLessonModel) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    if(plannedLessonModel.getNumberOfTimes() > 0)
                    {
                        if(plannedLessonModel.getDayOfWeek() == (dayNumber+1))
                        {
                            try {
                                Calendar c = Calendar.getInstance();
                                c.setTime(sdf.parse(plannedLessonModel.getDay()));
                                c.add(Calendar.DATE, (plannedLessonModel.getNumberOfTimes()-1) * 7 + 6);
                                Calendar c2 = Calendar.getInstance();
                                c2.setTime(sdf.parse(dayString));

                                if(c.getTime().getTime() >= c2.getTime().getTime())
                                {
                                    return sdf.parse(plannedLessonModel.getDay()).getTime() <= c2.getTime().getTime();
                                }
                                else
                                    return false;


                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            return false;
                    }
                    return plannedLessonModel.getDay().equals(dayString);// && plannedLessonModel.getNumberOfTimes() == 0) || (plannedLessonModel.getDayOfWeek() == (dayNumber+1) && (plannedLessonModel.getNumberOfTimes() > dayString));
                }
            }).collect(Collectors.toList()));

            lessons = lessons.stream().sorted(new Comparator<LessonModel>() {
                @Override
                public int compare(LessonModel l1, LessonModel l2) {
                    return l1.getTime().compareTo(l2.getTime());
                }
            }).collect(Collectors.toList());

            adapter.setLessons(lessons, dayString, offline);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        //countDownTimer.start();

    }

    @Override
    public void onStop() {
        super.onStop();
        //countDownTimer.cancel();
    }
}