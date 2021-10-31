package com.pawlowski.planzajweaiiib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pawlowski.planzajweaiiib.api.ApiService;
import com.pawlowski.planzajweaiiib.api.LessonResponseModel;
import com.pawlowski.planzajweaiiib.api.RetrofitClient;
import com.pawlowski.planzajweaiiib.database.DBHandler;
import com.pawlowski.planzajweaiiib.day.LessonModel;
import com.pawlowski.planzajweaiiib.day.SavedLessonModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.card_view_main)
    CardView cardView;

    @BindView(R.id.viewPager)
    ViewPager2 viewPager2;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

    @BindView(R.id.week_text_main)
    TextView weekText;

    @BindView(R.id.next_week_main)
    ImageButton nextWeekButton;


    @BindView(R.id.previous_week_main)
    ImageButton previousWeekButton;


    List<LessonModel> lessons = new ArrayList<>();
    List<SavedLessonModel> savedLessons = new ArrayList<>();

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private String mondayDate;
    private String fridayDate;

    private List<WeekModel> downloadedWeeks = new ArrayList<>();

    private ApiService apiService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    ViewPageAdapter adapter;

    Dialog previewDialog;

    Unbinder unbinder;


    int todayDayIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Plan ISI");

        unbinder = ButterKnife.bind(this);
        getCurrentWeekDates();
        weekText.setText((mondayDate + " - " + fridayDate));
        adapter = new ViewPageAdapter(getSupportFragmentManager(), getLifecycle(), this);
        viewPager2.setAdapter(adapter);

        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2, true, true,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position)
                        {
                            case 0:
                                tab.setText("Pn");
                                break;
                            case 1:
                                tab.setText("Wt");
                                break;
                            case 2:
                                tab.setText("Śr");
                                break;
                            case 3:
                                tab.setText("Czw");
                                break;
                            case 4:
                                tab.setText("Pt");
                                break;

                        }
                    }
                });
        mediator.attach();

        todayDayIndex = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        if(todayDayIndex == 0)
        {
            todayDayIndex = 7;
        }
        if(todayDayIndex-1 <= 4)
            tabLayout.getTabAt(todayDayIndex - 1).select();
        else
            tabLayout.getTabAt(4).select();


        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                adapter.refreshFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });




        apiService = RetrofitClient.getClient(getApplicationContext())
                .create(ApiService.class);

        savedLessons = DBHandler.getInstance(getApplicationContext()).getSavedLessons();

        int index = downloadedWeeks.size();
        downloadedWeeks.add(new WeekModel(mondayDate, fridayDate, false));

        disposable.add(apiService.getLessonsFromServer(mondayDate, fridayDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<LessonResponseModel>>() {
                    @Override
                    public void onSuccess(@NonNull List<LessonResponseModel> lessons) {


                        List<LessonModel>parsedLessons = new ArrayList<>();
                        for(LessonResponseModel l:lessons)
                        {
                            parsedLessons.add(LessonModel.parseFromLessonResponseModel(l));
                        }


                        setWeekAsDownloaded(index);
                        addLessons(parsedLessons);
                        adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                        DBHandler.getInstance(getApplicationContext()).replaceWeekWithNewLessons(downloadedWeeks.get(index).getMonday(), downloadedWeeks.get(index).getFriday(), parsedLessons);

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                }));


        nextWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextWeekButton.setClickable(false);
                previousWeekButton.setClickable(false);
                nextWeekClick();
                nextWeekButton.setClickable(true);
                previousWeekButton.setClickable(true);
            }
        });


        previousWeekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousWeekButton.setClickable(false);
                nextWeekButton.setClickable(false);
                previousWeekClick();
                previousWeekButton.setClickable(true);
                nextWeekButton.setClickable(true);
            }
        });


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for(WeekModel w:downloadedWeeks)
                {
                    if(w.getMonday().equals(mondayDate) && w.getFriday().equals(fridayDate))
                    {
                        if(!w.isDownloaded())
                        {
                            disposable.add(apiService.getLessonsFromServer(mondayDate, fridayDate)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribeWith(new DisposableSingleObserver<List<LessonResponseModel>>() {
                                        @Override
                                        public void onSuccess(@NonNull List<LessonResponseModel> lessons) {

                                            if(!w.isDownloaded())
                                            {
                                                List<LessonModel>parsedLessons = new ArrayList<>();
                                                for(LessonResponseModel l:lessons)
                                                {
                                                    parsedLessons.add(LessonModel.parseFromLessonResponseModel(l));
                                                }
                                                w.setDownloaded(true);
                                                addLessons(parsedLessons);
                                                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                                                DBHandler.getInstance(getApplicationContext()).replaceWeekWithNewLessons(w.getMonday(), w.getFriday(), parsedLessons);

                                            }

                                            if(refreshLayout.isRefreshing())
                                                refreshLayout.setRefreshing(false);

                                        }

                                        @Override
                                        public void onError(@NonNull Throwable e) {
                                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                    }));
                        }
                        new CountDownTimer(1500, 1000)
                        {

                            @Override
                            public void onTick(long l) {

                            }

                            @Override
                            public void onFinish() {
                                if(refreshLayout.isRefreshing())
                                    refreshLayout.setRefreshing(false);
                            }
                        }.start();

                        break;
                    }
                }

            }
        });




    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.refreshFragment(tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onStop() {
        /*if(lessons.size() > 0)
        {
            DBHandler.getInstance(getApplicationContext()).deleteSavedLessonsWhichAreDownloaded(downloadedWeeks);
            DBHandler.getInstance(getApplicationContext()).insertSavedLessons(lessons);
        }*/
        super.onStop();



    }



    void previousWeekClick()
    {

        getPreviousWeekDates();
        weekText.setText((mondayDate + " - " + fridayDate));
        if(!isWeekDownloadedOrDownloading(mondayDate))
        {
            adapter.refreshFragment(tabLayout.getSelectedTabPosition());
            downloadWeek(mondayDate, fridayDate);
        }
        else
        {
            adapter.refreshFragment(tabLayout.getSelectedTabPosition());
        }
    }


    public void showPreviewDialog(String infoText, boolean isAdditional, boolean isHidden, View.OnClickListener hideListener, View.OnClickListener deleteListener)
    {
        previewDialog = new Dialog(this);
        previewDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        previewDialog.setContentView(R.layout.preview_dialog);
        ((TextView)previewDialog.findViewById(R.id.info_text_preview_dialog)).setText(infoText);

        if(!isAdditional)
        {
            previewDialog.findViewById(R.id.delete_button_preview).setVisibility(View.GONE);
        }
        else
        {
            previewDialog.findViewById(R.id.delete_button_preview).setOnClickListener(deleteListener);

        }

        Button hideButton = previewDialog.findViewById(R.id.hide_button_preview);
        hideButton.setOnClickListener(hideListener);

        if(isHidden)
        {
            hideButton.setText("Pokaż");
        }


        previewDialog.setCanceledOnTouchOutside(true);
        previewDialog.show();
    }



    void nextWeekClick()
    {
        getNextWeekDates();
        weekText.setText((String)(mondayDate + " - " + fridayDate));
        if(!isWeekDownloadedOrDownloading(mondayDate))
        {
            adapter.refreshFragment(tabLayout.getSelectedTabPosition());
            downloadWeek(mondayDate, fridayDate);
        }
        else
        {
            adapter.refreshFragment(tabLayout.getSelectedTabPosition());
        }
    }

    public void downloadWeek(String mondayDate, String fridayDate)
    {
        int index = downloadedWeeks.size();
        downloadedWeeks.add(new WeekModel(mondayDate, fridayDate, false));

        disposable.add(apiService.getLessonsFromServer(mondayDate, fridayDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<LessonResponseModel>>() {
                    @Override
                    public void onSuccess(@NonNull List<LessonResponseModel> lessons) {
                        if(!downloadedWeeks.get(index).isDownloaded())
                        {
                            List<LessonModel>parsedLessons = new ArrayList<>();
                            for(LessonResponseModel l:lessons)
                            {
                                parsedLessons.add(LessonModel.parseFromLessonResponseModel(l));
                            }
                            setWeekAsDownloaded(index);
                            addLessons(parsedLessons);
                            adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                            DBHandler.getInstance(getApplicationContext()).replaceWeekWithNewLessons(mondayDate, fridayDate, parsedLessons);

                        }

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                    }
                }));


    }

    public void getCurrentWeekDates()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = calendar.getTime();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayOfWeek == 0)
            dayOfWeek = 7;

        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add(Calendar.DATE, -(dayOfWeek - 1));
        Date d1 = c.getTime();//new Date(today.getTime() - ((dayOfWeek - 1) * (24 * 60 * 60 * 1000)));
        c.add(Calendar.DATE, 4);
        Date d2 = c.getTime();//new Date(d1.getTime() + (4 * (24 * 60 * 60 * 1000)));
        mondayDate = sdf.format(d1);
        fridayDate = sdf.format(d2);

    }

    public void getNextWeekDates()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date lastFriday = sdf.parse(fridayDate);
            Log.d("lastFriday", fridayDate);
            Calendar c = Calendar.getInstance();
            c.setTime(lastFriday);
            c.add(Calendar.DATE, 3);


            Date d1 = c.getTime();//new Date(lastFriday.getTime() + (3 * (24 * 60 * 60 * 1000)));
            c.add(Calendar.DATE, 4);
            Date d2 = c.getTime();//new Date(d1.getTime() + (4 * (24 * 60 * 60 * 1000)));
            mondayDate = sdf.format(d1);
            fridayDate = sdf.format(d2);
            Log.d("nextWeek", mondayDate + " - " + fridayDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getPreviousWeekDates()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date lastMonday = sdf.parse(mondayDate);
            Calendar c = Calendar.getInstance();
            c.setTime(lastMonday);
            c.add(Calendar.DATE, -3);
            Date d2 = c.getTime();//new Date(lastMonday.getTime() - (3 * (24 * 60 * 60 * 1000)));
            c.add(Calendar.DATE, -4);
            Date d1 = c.getTime();//new Date(d2.getTime() - (4 * (24 * 60 * 60 * 1000)));
            mondayDate = sdf.format(d1);
            fridayDate = sdf.format(d2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getWeekByDate(String dateString) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date today = sdf.parse(dateString);
        calendar.setTime(today);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayOfWeek == 0)
            dayOfWeek = 7;

        Date d1 = new Date(today.getTime() - ((dayOfWeek - 1) * (24 * 60 * 60 * 1000)));
        Date d2 = new Date(d1.getTime() + (4 * (24 * 60 * 60 * 1000)));
        mondayDate = sdf.format(d1);
        fridayDate = sdf.format(d2);
    }

    public void setLessons(List<LessonModel>lessons)
    {
        this.lessons = lessons;
    }

    public void addLessons(List<LessonModel>lessons)
    {
        this.lessons.addAll(lessons);
    }

    public List<LessonModel> getLessons()
    {
        return lessons;
    }

    public List<SavedLessonModel> getSavedLessons()
    {
        return savedLessons;
    }

    public List<PlannedLessonModel> getAdditionalLessons()
    {
        return DBHandler.getInstance(getApplicationContext()).getAdditionalLessons();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
        unbinder.unbind();
    }

    public String getMondayDate() {
        return mondayDate;
    }

    public String getFridayDate() {
        return fridayDate;
    }

    public void setWeekAsDownloaded(String mondayDate)
    {
        for(WeekModel w:downloadedWeeks)
        {
            if(w.getMonday().equals(mondayDate))
            {
                w.setDownloaded(true);
                break;
            }
        }
    }

    public void setWeekAsDownloaded(int index)
    {
        WeekModel w = downloadedWeeks.get(index);
        w.setDownloaded(true);
        downloadedWeeks.set(index, w);
    }

    public boolean isWeekDownloadedOrDownloading(String mondayDate)
    {
        for(WeekModel w:downloadedWeeks)
        {
            if(w.getMonday().equals(mondayDate))
                return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        int selectedGroup = getSelectedGroup(this);
        //Log.d("selected", selectedGroup + "");

        menu.getItem(selectedGroup+3).setChecked(true);
        return super.onCreateOptionsMenu(menu);
    }



    public void openCalendar()
    {
        int mYear, mMonth, mDay, mHour, mMinute;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                       String newDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        try {
                            getWeekByDate(newDate);
                            weekText.setText((String)(mondayDate + " - " + fridayDate));
                            if(!isWeekDownloadedOrDownloading(mondayDate))
                            {
                                downloadWeek(mondayDate, fridayDate);
                            }
                            else
                            {
                                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date d = sdf.parse(newDate);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(d);
                            int dIndex = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                            if(dIndex == 0)
                                dIndex = 7;

                            if(dIndex <= 5)
                                tabLayout.getTabAt(dIndex-1).select();

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.add_lesson_button_menu:
                Intent i = new Intent(MainActivity.this, AddLessonActivity.class);
                startActivity(i);




                return true;
            case R.id.today_button_menu:
                getCurrentWeekDates();
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                weekText.setText((mondayDate + " - " + fridayDate));
                if(todayDayIndex <= 4)
                    tabLayout.getTabAt(todayDayIndex - 1).select();
                else
                {
                    tabLayout.getTabAt(4).select();
                }
                return true;
            case R.id.calendar_menu:
                openCalendar();
                return true;
            case R.id.all_groups_menu:
            {
                selectGroup(0);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g1_menu:
            {
                selectGroup(1);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g2_menu:
            {
                selectGroup(2);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g3_menu:
            {
                selectGroup(3);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g4_menu:
            {
                selectGroup(4);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g5_menu:
            {
                selectGroup(5);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g6_menu:
            {
                selectGroup(6);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
            case R.id.g7_menu:
            {
                selectGroup(7);
                adapter.refreshFragment(tabLayout.getSelectedTabPosition());
                item.setChecked(true);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    public void selectGroup(int number)
    {
        SharedPreferences sharedPreferences = this.getSharedPreferences("timetablePreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("myGroup", number);
        editor.commit();
    }


    public static int getSelectedGroup(Activity activity)
    {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("timetablePreferences", MODE_PRIVATE);
        return sharedPreferences.getInt("myGroup", 0);


    }

    public void hidePreviewDialog()
    {
        if(previewDialog != null && previewDialog.isShowing())
        {
            previewDialog.dismiss();
            adapter.refreshFragment(tabLayout.getSelectedTabPosition());
        }
    }

    public void openDeleteDialog(View view, DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Napewno chcesz usunąć podaną lekcję? Jeśli wydarzenie jest "
                + "cykliczne, wszystkie jego powtórzenia zostaną usunięte");
                alertDialogBuilder.setPositiveButton("Tak",
                        yesListener);

        alertDialogBuilder.setNegativeButton("Nie",noListener);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }




}