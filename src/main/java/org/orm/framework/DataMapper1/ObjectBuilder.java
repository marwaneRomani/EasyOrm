package org.orm.framework.DataMapper1;

import org.orm.framework.ApplicationState.ApplicationState;
import org.orm.framework.ConnectionsPool.ConnectionPool;
import org.orm.framework.DataMapper1.JdbcTemplate.JdbcTemplateImpl;
import org.orm.framework.DataMapper1.Utils.FindAttribute;
import org.orm.framework.DataMapper1.Utils.FindAttributeRelation;
import org.orm.framework.DataMapper1.methods.find.Find;
import org.orm.framework.DataMapper1.methods.save.Save;
import org.orm.framework.EntitiesDataSource.EntitiesDataSource;
import org.orm.framework.EntitiesDataSource.Entity;
import org.orm.framework.ModelsMapper.FieldsMapper.Attribute.Attribute;
import org.orm.framework.ModelsMapper.FieldsMapper.Attribute.AttributeList;
import org.orm.framework.ModelsMapper.FieldsMapper.Relation.OneToMany;
import org.orm.framework.ModelsMapper.FieldsMapper.Relation.Relation;
import org.orm.framework.OrmApplication;
import org.orm.framework.TransactionsManager.Transaction;
import org.orm.framework.customException.ORMException;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder<T> {
    private Class<?> model;
    private Entity entity;
    private ApplicationState state;

    private T object;
    private List<T> objects;

    public ObjectBuilder(Class<?> model) {
        this.model = model;
        this.state = ApplicationState.getState();
        try {
            object = (T) Class.forName(model.getName()).newInstance();
            entity = EntitiesDataSource.getModelsSchemas().get(model.getSimpleName());

            if (entity == null) {
                throw new Exception("entity not found.");
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }



    /**
     * persistence methods
     *
     **/

    public T save(T object) {
        try {
            ConnectionPool pool = ConnectionPool.getInstance(state.getUrl(),state.getUsername(),state.getPassword(), state.getConnectionPoolMaxSize());
            Connection connection = pool.getConnection();

            Save<T> saveObject = new Save<>(new JdbcTemplateImpl(connection));


            Method saveMethod = Save.class.getMethod("save", Entity.class, Object.class);
            Transaction.wrapMethodInTransaction(connection, saveObject, saveMethod, entity, object);

            pool.releaseConnection(connection);

            return object;

        } catch (SQLException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectBuilder<T> findAll() {
        try {
            ConnectionPool pool = ConnectionPool.getInstance(state.getUrl(),state.getUsername(),state.getPassword(), state.getConnectionPoolMaxSize());
            Connection connection = pool.getConnection();

            Find<T> findObject = new Find<>(new JdbcTemplateImpl(connection));

            this.objects = findObject.findAll(entity);

            pool.releaseConnection(connection);

            return this;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectBuilder<T> findById(Object id) {
        try {
            ConnectionPool pool = ConnectionPool.getInstance(state.getUrl(),state.getUsername(),state.getPassword(), state.getConnectionPoolMaxSize());
            Connection connection = pool.getConnection();

            Find<T> findObject = new Find<>(new JdbcTemplateImpl(connection));

            object = findObject.findById(entity, id);

            pool.releaseConnection(connection);

            return this;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectBuilder<T> findOne(Object... searchParams) {

        // TODO Support also search by foreign keys
        try {
            if (searchParams.length == 0 )
                throw new ORMException("Search params are required as key-operation-value");

            if (searchParams.length % 3 != 0)
                throw new ORMException("Search params should be a valid key-operation-value");


            List<String> keys = new ArrayList<>();
            List<String> conditionTypes = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            for (int i = 0; i < searchParams.length; i += 3) {
                String field = (String) searchParams[i];
                String condition = (String) searchParams[i + 1];
                Object value = searchParams[i + 2];

                if (field.equals("") || value.equals(""))
                    throw new ORMException("Search params are not allowed to be empty");


                if (field.equals("limit")) {
                    keys.add(field);
                    conditionTypes.add("=");
                    values.add(value);
                }
                else if (entity.getPrimaryKey().getName().equals(field)) {
                    keys.add(field);
                    conditionTypes.add(condition);
                    values.add(value);
                }
                else {
                    Attribute attribute = entity
                            .getNormalAttributes()
                            .stream()
                            .filter(att -> att.getName().equals(field)).findFirst().orElse(null);

                    if (attribute == null)
                        throw new ORMException("No attribute with the name " + field + " in this class !");

                    keys.add(field);
                    conditionTypes.add(condition);
                    values.add(value);
                }

            }

            ConnectionPool pool = ConnectionPool.getInstance(state.getUrl(),state.getUsername(),state.getPassword(), state.getConnectionPoolMaxSize());
            Connection connection = pool.getConnection();


            Find<T> findObject = new Find<>(new JdbcTemplateImpl(connection));

            object = findObject.findOne(entity, keys, conditionTypes ,values);

            pool.releaseConnection(connection);

            return this;
        }
        catch (ORMException | SQLException  e) {
            throw new RuntimeException(e);
        }
    }

    public ObjectBuilder<T> findMany(Object... searchParams) {

        try {
            if (searchParams.length == 0 )
                throw new ORMException("Search params are required as key-operation-value");

            if (searchParams.length % 3 != 0)
                throw new ORMException("Search params should be a valid key-operation-value");


            List<String> keys = new ArrayList<>();
            List<String> conditionTypes = new ArrayList<>();
            List<Object> values = new ArrayList<>();

            for (int i = 0; i < searchParams.length; i += 3) {
                String field = (String) searchParams[i];
                String condition = (String) searchParams[i + 1];
                Object value = searchParams[i + 2];

                if (field.equals("") || value.equals(""))
                    throw new ORMException("Search params are not allowed to be empty");


                if (field.equals("limit")) {
                    keys.add(field);
                    conditionTypes.add("=");
                    values.add(value);
                }
                else if (entity.getPrimaryKey().getName().equals(field)) {
                    keys.add(field);
                    conditionTypes.add(condition);
                    values.add(value);
                }
                else {
                    Attribute attribute = entity
                            .getNormalAttributes()
                            .stream()
                            .filter(att -> att.getName().equals(field)).findFirst().orElse(null);

                    if (attribute == null)
                        throw new ORMException("No attribute with the name " + field + " in this class !");

                    keys.add(field);
                    conditionTypes.add(condition);
                    values.add(value);
                }

            }

            ConnectionPool pool = ConnectionPool.getInstance(state.getUrl(),state.getUsername(),state.getPassword(), state.getConnectionPoolMaxSize());
            Connection connection = pool.getConnection();


            Find<T> findObject = new Find<>(new JdbcTemplateImpl(connection));

            objects = findObject.findMany(entity, keys,conditionTypes, values);

            pool.releaseConnection(connection);

            return this;
        }
        catch (ORMException | SQLException  e) {
            throw new RuntimeException(e);
        }
    }


    public ObjectBuilder<T> get(String relationName) {
        try {
            ConnectionPool pool = ConnectionPool.getInstance(state.getUrl(),state.getUsername(),state.getPassword(), state.getConnectionPoolMaxSize());
            Connection connection = pool.getConnection();

            Attribute attribute = FindAttribute.find(entity, relationName);
            Relation relation = FindAttributeRelation.find(attribute, entity);

            if (attribute instanceof AttributeList) {
                if (relation instanceof OneToMany) {

                }
                else  {

                }
            }
            else {
                if (relation instanceof OneToMany) {
                    OrmApplication
                            .buildObject(attribute.getClazz())
                            .findOne();
                }
                else  {

                }
            }

            pool.releaseConnection(connection);

            return this;
        } catch (ORMException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public T buildObject() {
        return object;
    }

    public List<T> buildObjects() {
        return objects;
    }

}
