package org.devproof.mubble.androidclient.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.apache.commons.lang.UnhandledException;

import java.sql.SQLException;

/**
 * Generalized DbHelper to create the database.
 * Creates database for the OrmLite Entities
 *
 * ATTENTION: Does no incremental updates, just drops and creates
 *
 * @author Carsten Hufe
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
    private static final String TAG = DbHelper.class.getSimpleName();
    private Class<?>[] clazzes;

    public DbHelper(Context context, String dbName, int dbVersion, Class<?>... clazzes) {
        super(context, dbName, null, dbVersion);
        this.clazzes = clazzes;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            for (Class<?> clazz : clazzes) {
                TableUtils.createTable(connectionSource, clazz);
                Log.d(TAG, "Create table for " + clazz);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Could not create database", e);
            throw new UnhandledException(e);
        }
        Log.i(TAG, "Created DB");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            for (Class<?> clazz : clazzes) {
                TableUtils.dropTable(connectionSource, clazz, true);
                Log.d(TAG, "Deleted table for " + clazz);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Could not delete database", e);
            throw new UnhandledException(e);
        }
        onCreate(database, connectionSource);
        Log.i(TAG, "Upgraded DB from " + oldVersion + " to " + newVersion);
    }
}
