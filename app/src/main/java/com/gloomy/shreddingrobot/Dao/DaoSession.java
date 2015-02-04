package com.gloomy.shreddingrobot.Dao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

/**
 * {@inheritDoc}
 *
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig dBTrackDaoConfig;

    private final DBTrackDao dBTrackDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        dBTrackDaoConfig = daoConfigMap.get(DBTrackDao.class).clone();
        dBTrackDaoConfig.initIdentityScope(type);

        dBTrackDao = new DBTrackDao(dBTrackDaoConfig, this);

        registerDao(DBTrack.class, dBTrackDao);
    }

    public void clear() {
        dBTrackDaoConfig.getIdentityScope().clear();
    }

    public DBTrackDao getDBTrackDao() {
        return dBTrackDao;
    }

}
