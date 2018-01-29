package br.brunodea.nevertoolate.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NeverTooLateDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "NeverTooLateDB";
    private static final int DB_VERSION = 1;


    NeverTooLateDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Favorites.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Favorites.TABLE_NAME);
        onCreate(db);
    }
    static final class Favorites {
        static final String TABLE_NAME = "favorites";

        static final String _ID = "_id";
        static final String URL = "url";
        static final String PERMALINK = "permalink";
        static final String TITLE = "title";
        static final String REDDIT_ID = "reddit_id";
        static final String[] PROJECTION_ALL =
                {URL, PERMALINK, TITLE, REDDIT_ID};
        static final String SQL_CREATE =
                "CREATE TABLE " + TABLE_NAME +
                        " (" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        URL + " TEXT NOT NULL," +
                        PERMALINK + " TEXT NOT NULL," +
                        TITLE + " TEXT NOT NULL," +
                        REDDIT_ID + " TEXT NOT NULL);";
    }
}
