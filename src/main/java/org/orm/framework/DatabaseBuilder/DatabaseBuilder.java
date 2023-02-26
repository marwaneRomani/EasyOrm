package org.orm.framework.DatabaseBuilder;

import org.orm.framework.DatabaseBuilder.Builders.CreateBuilder;
import org.orm.framework.DatabaseBuilder.Builders.CreateDropBuilder;
import org.orm.framework.DatabaseBuilder.Builders.StategyBuilder;
import org.orm.framework.DatabaseBuilder.Builders.UpdateBuilder;
import org.orm.framework.DatabaseBuilder.Dialects.Dialect;
import org.orm.framework.DatabaseBuilder.Dialects.H2Dialect;
import org.orm.framework.DatabaseBuilder.Dialects.MySQLDialect;
import org.orm.framework.DatabaseBuilder.Dialects.PostgreSqlDialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;

public class DatabaseBuilder {

    private final Map<String , Class<? extends StategyBuilder> > strategies = new HashMap<>(){{
        put("create", CreateBuilder.class);
        put("create-drop", CreateDropBuilder.class);
        put("update", UpdateBuilder.class);
    }};

    private final Map<String, Class<? extends Dialect>> dialects = new HashMap<>() {{
        put("mysql", MySQLDialect.class);
        put("h2", H2Dialect.class);
        put("postgresql", PostgreSqlDialect.class);
    }};

    public void build(String strategy, String dialect, Connection connection) throws Exception {
        Dialect dbDialect = dialects.get(dialect).newInstance();
        StategyBuilder dbStrategy = strategies.get(strategy.toLowerCase()).newInstance();

//        Connection connection =  DriverManager.getConnection("jdbc:mysql://localhost:3306/Team","root", "134194630");

        dbStrategy.setDialect(dbDialect);
        dbStrategy.setConnection(connection);

        dbStrategy.build();
    }
}
