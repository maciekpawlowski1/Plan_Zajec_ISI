package com.pawlowski.planzajweaiiib;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pawlowski.planzajweaiiib.database.DBHandler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class AddLessonActivity extends AppCompatActivity {


    @BindView(R.id.calendar_button_add_lesson)
    ImageButton calendarButton;

    @BindView(R.id.date_input_add_lesson)
    TextInputEditText dateInput;

    @BindView(R.id.day_of_week_input_layout_add_lesson)
    TextInputLayout dayOfWeekLayout;

    @BindView(R.id.date_input_layout_add_lesson)
    TextInputLayout dateLayout;

    @BindView(R.id.single_radio_add_lesson)
    RadioButton singleRadio;

    @BindView(R.id.repeated_radio_add_lesson)
    RadioButton repeatedRadio;

    @BindView(R.id.number_of_times_input_layout_add_lesson)
    TextInputLayout numberOfTimes;

    @BindView(R.id.tittle_input_add_lesson)
    TextInputEditText tittleInput;

    @BindView(R.id.teacher_input_add_lesson)
    TextInputEditText teacherInput;

    @BindView(R.id.room_input_add_lesson)
    TextInputEditText roomInput;

    @BindView(R.id.start_input_add_lesson)
    TextInputEditText startInput;

    @BindView(R.id.end_input_add_lesson)
    TextInputEditText endInput;

    @BindView(R.id.day_of_week_input_add_lesson)
    TextInputEditText dayOfWeekInput;

    @BindView(R.id.number_of_times_input_add_lesson)
    TextInputEditText numberOfTimesInput;

    @OnClick(R.id.calendar_button_add_lesson)
    void onCalendarClick()
    {
        openCalendar();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);


        ButterKnife.bind(this);

        singleRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleRadioClick();
            }
        });


        repeatedRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                repeatedRadioClick();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_lesson_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.confirm_add_lesson_menu:
                String tittle = tittleInput.getText().toString();
                String teacher = teacherInput.getText().toString();
                String room = roomInput.getText().toString();
                String start = startInput.getText().toString();
                String end = endInput.getText().toString();
                String dayOfWeekString = dayOfWeekInput.getText().toString();
                int dayOfWeek = 0;
                if(dayOfWeekString.length() != 0)
                    dayOfWeek = Integer.parseInt(dayOfWeekString);
                String timesString =numberOfTimesInput.getText().toString();
                int times = 0;
                if(timesString.length() != 0)
                    times = Integer.parseInt(timesString);
                String date = dateInput.getText().toString();

                //TODO: Validation

                if(tittle.length() < 3)
                {
                    showErrorSnackbar("Zbyt krótki tytuł", true);
                    return true;
                }

                final String HOUR_PATTERN = "([01]?[0-9]|2[0-3]):[0-5][0-9]";
                Pattern pattern = Pattern.compile(HOUR_PATTERN);
                if(start.length() == 0)
                {
                    showErrorSnackbar("Podaj początkową godzinę", true);
                    return true;
                }
                else if(!pattern.matcher(start).matches())
                {
                    showErrorSnackbar("Niewłaściwy format początkowej godziny", true);
                    return true;
                    //if(start.indexOf(':') == -1 || )

                }
                else
                {
                    if(start.length() == 4)
                    {
                        start = "0" + start;
                    }
                }

                if(end.length() == 0)
                {
                    showErrorSnackbar("Podaj końcową godzinę", true);
                    return true;
                }
                else if(!pattern.matcher(end).matches())
                {
                    showErrorSnackbar("Niewłaściwy format końcowej godziny", true);
                    return true;
                    //if(start.indexOf(':') == -1 || )

                }
                else
                {
                    if(end.length() == 4)
                    {
                        end = "0" + end;
                    }
                }

                if(!repeatedRadio.isChecked() && !singleRadio.isChecked())
                {
                    showErrorSnackbar("Zaznacz czy wydarzenie ma się powtarzać", true);
                    return true;
                }

                PlannedLessonModel p = new PlannedLessonModel(tittle, room, start + " - " + end, teacher);
                if(repeatedRadio.isChecked())
                {
                    if(dayOfWeekString.length() == 0)
                    {
                        showErrorSnackbar("Podaj dzień tygodnia", true);
                        return true;
                    }
                    else if(dayOfWeek < 1 || dayOfWeek > 5)
                    {
                        showErrorSnackbar("Podaj dzień tygodnia z zakresu 1 - 5 (pn - pt)", true);
                        return true;
                    }

                    if(timesString.length() == 0)
                    {
                        showErrorSnackbar("Podaj ilość powtórzeń", true);
                        return true;
                    }
                    p.setDayOfWeek(dayOfWeek);
                    p.setNumberOfTimes(times);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    p.setDay(sdf.format(Calendar.getInstance().getTime()));
                }
                else if(singleRadio.isChecked())
                {
                    if(date.length() == 0)
                    {
                        showErrorSnackbar("Podaj datę", true);
                        return true;
                    }

                    final String DATE_PATTERN = "^[0-9]{4}-(1[0-2]|0[1-9])-(3[01]|[12][0-9]|0[1-9])$";//"/^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$/";
                    Pattern pattern2 = Pattern.compile(DATE_PATTERN);
                    if(!pattern2.matcher(date).matches())
                    {
                        showErrorSnackbar("Nieprawidłowy format daty. Podaj datę w formacie RRRR-MM-DD", true);
                        return true;
                    }


                    p.setDay(date);

                }
                //Log.d("newNumberOfTimes", p.getNumberOfTimes()+"");
                DBHandler.getInstance(getApplicationContext()).insertAdditionalLesson(p);
                onBackPressed();
                return true;
            default:


        }
        return super.onOptionsItemSelected(item);
    }


    public void showErrorSnackbar(String text, boolean error)
    {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), text, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();

        if(error)
        {
            snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.red));
        }
        else
        {
            snackbarView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        }
        snackbar.show();
    }


    void singleRadioClick()
    {
        dateLayout.setVisibility(View.VISIBLE);
        calendarButton.setVisibility(View.VISIBLE);
        dayOfWeekLayout.setVisibility(View.GONE);
        numberOfTimes.setVisibility(View.GONE);
    }

    void repeatedRadioClick()
    {
        dayOfWeekLayout.setVisibility(View.VISIBLE);
        numberOfTimes.setVisibility(View.VISIBLE);
        dateLayout.setVisibility(View.GONE);
        calendarButton.setVisibility(View.GONE);
    }

    public void openCalendar()
    {
        int mYear, mMonth, mDay;//, mHour, mMinute;
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String newDate = year + "-" + (monthOfYear+1>9?monthOfYear+1:"0"+(monthOfYear+1)) + "-" + (dayOfMonth>9?dayOfMonth:"0"+dayOfMonth);
                        dateInput.setText(newDate);
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.show();
    }
}