package com.gamfig.monitorabrasil.DAO;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Presenca;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by geral_000 on 16/02/2015.
 */
public class DataBaseHelper extends OrmLiteSqliteOpenHelper{
    private static final String dbName="monitora.db";
    private static final int dbVersion = 11;

    public DataBaseHelper(Context context){
        super(context,dbName,null,dbVersion );
    }
    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Politico.class);
            TableUtils.createTable(connectionSource, Presenca.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

        try {
            TableUtils.dropTable(connectionSource, Politico.class,true);
            TableUtils.dropTable(connectionSource, Presenca.class,true);
            onCreate(database,connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void clearTables(ConnectionSource connectionSource) throws SQLException {
        TableUtils.clearTable(connectionSource,Politico.class);
    }

    @Override
    public void close(){
        super.close();
    }
}
