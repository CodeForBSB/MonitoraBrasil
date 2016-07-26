package com.gamfig.monitorabrasil.DAO;

import com.gamfig.monitorabrasil.classes.Politico;
import com.gamfig.monitorabrasil.classes.Presenca;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

/**
 * Created by geral_000 on 16/02/2015.
 */
public class PresencaDAO extends BaseDaoImpl<Presenca,Integer> {

    protected PresencaDAO(ConnectionSource connectionSource, Class<Presenca> dataClass) throws SQLException {
        super(connectionSource, dataClass);
        initialize();
    }
}