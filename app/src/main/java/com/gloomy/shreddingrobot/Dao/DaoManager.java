package com.gloomy.shreddingrobot.Dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DaoManager {

    public final static int TYPE_READ = 0;
    public final static int TYPE_WRITE = 1;

    private DaoMaster.DevOpenHelper devOpenHelper;

    private static DaoManager mDaoManager;

    private DaoManager(Context context) {
        devOpenHelper = new DaoMaster.DevOpenHelper(context, "ShredBot", null);
    }

    private SQLiteDatabase getDB(int type) {
        SQLiteDatabase db = null;
        if(type == TYPE_READ) {
            db = devOpenHelper.getReadableDatabase();
        } else {
            db = devOpenHelper.getWritableDatabase();
        }
        return db;
    }

    public DBTrackDao getDBTrackDao(int type) {
        SQLiteDatabase db = getDB(type);
        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession daoSession = daoMaster.newSession();
        DBTrackDao dbTrackDao = daoSession.getDBTrackDao();
        return dbTrackDao;
    }


    public static synchronized DaoManager getInstance(Context context) {
        if(mDaoManager == null) {
            mDaoManager = new DaoManager(context);
        }
        return  mDaoManager;
    }
}