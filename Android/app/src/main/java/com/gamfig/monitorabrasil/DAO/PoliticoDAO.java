package com.gamfig.monitorabrasil.DAO;

import com.gamfig.monitorabrasil.classes.Politico;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by geral_000 on 16/02/2015.
 */
public class PoliticoDAO extends BaseDaoImpl<Politico,Integer> {

    public PoliticoDAO(ConnectionSource connectionSource) throws SQLException {
        super(Politico.class);
        setConnectionSource(connectionSource);
        initialize();
    }

    public Politico getPolitico(int idCadastro) throws SQLException {
        Politico politico=null;
        Map<String,Object> values = new HashMap<String,Object>();
        values.put("idCadastro",idCadastro);
        List<Politico> politicos = queryForFieldValues(values);
        if(!politicos.isEmpty()){
            politico=politicos.get(0);
        }

        return politico;
    }
}
