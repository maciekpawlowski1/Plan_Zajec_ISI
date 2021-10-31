package com.pawlowski.planzajweaiiib.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pawlowski.planzajweaiiib.PlannedLessonModel;
import com.pawlowski.planzajweaiiib.WeekModel;
import com.pawlowski.planzajweaiiib.day.LessonModel;
import com.pawlowski.planzajweaiiib.day.SavedLessonModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.annotation.Nullable;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TimetableDatabase";
    private static final int DATABASE_VERSION = 4;

    private DBHandler(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static DBHandler handler;

    public static DBHandler getInstance(Context context)
    {
        if(handler == null)
        {
            handler = new DBHandler(context.getApplicationContext());
        }
        return handler;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String creatingAdditionalLessons = "CREATE TABLE AdditionalLessons(id INTEGER PRIMARY KEY, tittle TEXT, teacher TEXT, room TEXT, time TEXT, day_of_week INTEGER, number_of_times INTEGER, date TEXT)";
        String creatingHiddenLessons = "CREATE TABLE HiddenLessons(id INTEGER, tittle TEXT, time TEXT, date TEXT)";
        String creatingSavedLessons = "CREATE TABLE SavedLessons(id INTEGER PRIMARY KEY, tittle TEXT, teacher TEXT, room TEXT, time TEXT, date TEXT, gr TEXT, full_info TEXT)";

        db.execSQL(creatingAdditionalLessons);
        db.execSQL(creatingHiddenLessons);
        db.execSQL(creatingSavedLessons);

    }

    public List<SavedLessonModel> getSavedLessons()
    {
        String selectLessons = "SELECT A.tittle, A.teacher, A.room, A.time, A.date, A.full_info, A.gr, A.id FROM SavedLessons A";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectLessons, null);
// looping through all rows and adding to list
        List<SavedLessonModel> savedLessons = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SavedLessonModel p = new SavedLessonModel(cursor.getString(0), cursor.getString(2), cursor.getString(3), cursor.getString(1));
                p.setDay(cursor.getString(4));
                p.setFullInfo(cursor.getString(5));
                p.setGroup(cursor.getString(6));

                p.setId(cursor.getInt(7));

                savedLessons.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();



        return savedLessons;
    }

    public void deleteSavedLessons()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM SavedLessons");
        db.close();
    }

    public void deleteSavedLessonsWhichAreDownloaded(List<WeekModel> downloadedWeeks)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> downloadedDays = new ArrayList<>();

        for(WeekModel w:downloadedWeeks)
        {
            downloadedDays.add(w.getMonday());
            downloadedDays.add(w.getFriday());
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(w.getMonday()));
                for(int i=1;i<4;i++)
                {
                    c.add(Calendar.DATE, 1);
                    downloadedDays.add(sdf.format(c.getTime()));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        SQLiteDatabase db = this.getWritableDatabase();
        List<SavedLessonModel> saved = getSavedLessons();
        for(SavedLessonModel l:saved)
        {
            boolean d = false;
            for(String s:downloadedDays)
            {
                if(l.getDay().equals(s))
                {
                    d = true;
                    break;
                }

            }
            if(d)
            {
                db.execSQL("DELETE FROM SavedLessons WHERE SavedLessons.id = " + l.getId());
            }
        }
        db.close();
    }

    public void insertSavedLessons(List<LessonModel> lessons)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        for(LessonModel l:lessons)
        {
            ContentValues values = new ContentValues();
            values.put("tittle", l.getTittle());
            values.put("teacher", l.getTeacher());
            values.put("room", l.getRoom());
            values.put("time", l.getTime());
            values.put("full_info", l.getFullInfo());
            values.put("gr", l.getGroup());
            values.put("date", l.getDay());
            db.insert("SavedLessons", null, values);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS AdditionalLessons");
        db.execSQL("DROP TABLE IF EXISTS HiddenLessons");
        db.execSQL("DROP TABLE IF EXISTS SavedLessons");

        onCreate(db);
    }


    public List<PlannedLessonModel> getAdditionalLessons()
    {
        String selectLessons = "SELECT A.tittle, A.teacher, A.room, A.time, A.day_of_week, A.date, A.number_of_times, A.id FROM AdditionalLessons A";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectLessons, null);
// looping through all rows and adding to list
        List<PlannedLessonModel> plannedLessons = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                PlannedLessonModel p = new PlannedLessonModel(cursor.getString(0), cursor.getString(2), cursor.getString(3), cursor.getString(1));
                int day = cursor.getInt(4);
                String date = cursor.getString(5);
                p.setDay("");
                p.setDayOfWeek(0);
                p.setNumberOfTimes(0);
                p.setNumberOfTimes(cursor.getInt(6));
                p.setDay(date);
                p.setId(cursor.getInt(7));
                if(p.getNumberOfTimes() != 0)
                {
                    p.setDayOfWeek(day);

                }



                p.setFullInfo(p.getTittle()+"\n"+p.getTeacher()+"\n"+p.getRoom()+"\n"+p.getTime());

                plannedLessons.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();


        return plannedLessons;
    }

    public void insertAdditionalLesson(PlannedLessonModel plannedLesson)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tittle", plannedLesson.getTittle());
        values.put("teacher", plannedLesson.getTeacher());
        values.put("room", plannedLesson.getRoom());
        values.put("time", plannedLesson.getTime());
        values.put("day_of_week", plannedLesson.getDayOfWeek());
        values.put("number_of_times", plannedLesson.getNumberOfTimes());
        values.put("date", plannedLesson.getDay());


        db.insert("AdditionalLessons", null, values);

    }

    public void hideLesson(String tittle, String time, String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tittle", tittle);
        values.put("time", time);
        values.put("date", date);

        db.insert("HiddenLessons", null, values);

    }

    public void hideLesson(String tittle, String time, String date, int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tittle", tittle);
        values.put("time", time);
        values.put("date", date);
        values.put("id", id);

        db.insert("HiddenLessons", null, values);

    }

    public void showLesson(String tittle, String time, String date)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM HiddenLessons WHERE HiddenLessons.tittle LIKE '" + tittle + "' AND HiddenLessons.time LIKE '"
        + time + "' AND HiddenLessons.date LIKE '" + date + "'");
        db.close();




    }

    public void showLesson(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM HiddenLessons WHERE HiddenLessons.id = " + id);
        db.close();




    }


    public void deleteLesson(int id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM AdditionalLessons WHERE AdditionalLessons.id = " + id);
        db.close();

        //Jeśli hidden, to usunąć też stamtąd
        showLesson(id);



    }

    public boolean isLessonHidden(String tittle, String time, String date)
    {
        String selectHiddenLessons = "SELECT 1 FROM HiddenLessons H WHERE H.tittle LIKE '" + tittle + "' AND H.time LIKE '" + time + "' AND H.date LIKE '" + date + "'";
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(selectHiddenLessons, null);
        if(cursor.moveToFirst())
        {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }
}
