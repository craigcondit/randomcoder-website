package org.randomcoder.dao;

import org.randomcoder.func.UncheckedBlock;
import org.randomcoder.func.UncheckedConsumer;
import org.randomcoder.func.UncheckedFunction;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.Callable;

public class DaoUtils {

    public static Connection getConnection(DataSource ds) throws DataAccessException {
        return wrap(() -> ds.getConnection());
    }

    public static void withReadonlyConnection(DataSource ds, UncheckedConsumer<Connection> consumer) throws DataAccessException {
        withReadonlyConnection(ds, (con) -> {
            consumer.invoke(con);
            return null;
        });
    }

    public static <T> T withReadonlyConnection(DataSource ds, UncheckedFunction<Connection, T> func) throws DataAccessException {
        try (Connection con = ds.getConnection()) {
            con.setReadOnly(true);
            return func.call(con);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    public static void withConnection(DataSource ds, UncheckedConsumer<Connection> consumer) throws DataAccessException {
        withConnection(ds, (con) -> {
            consumer.invoke(con);
            return null;
        });
    }

    public static <T> T withConnection(DataSource ds, UncheckedFunction<Connection, T> func) throws DataAccessException {
        try (Connection con = ds.getConnection()) {
            return func.call(con);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

    public static void withTransaction(Connection con, UncheckedBlock code) throws DataAccessException {
        withTransaction(con, () -> {
            code.call();
            return null;
        });
    }

    public static <T> T withTransaction(Connection con, Callable<T> code) throws DataAccessException {
        wrap(() -> {
            con.setAutoCommit(false);
            con.setReadOnly(false);
        });
        boolean commit = true;
        try {
            return code.call();
        } catch (Exception e) {
            commit = false;
            wrap(() -> {
                con.rollback();
            });
            throw new DataAccessException("Error in transaction", e);
        } finally {
            if (commit) {
                wrap(con::commit);
            }
        }
    }

    public static void withTransaction(DataSource ds, UncheckedConsumer<Connection> consumer) throws DataAccessException {
        withConnection(ds, (con) -> {
            withTransaction(con, () -> {
                consumer.invoke(con);
            });
        });
    }

    public static <T> T withTransaction(DataSource ds, UncheckedFunction<Connection, T> func) throws DataAccessException {
        return withConnection(ds, (con) -> {
            return withTransaction(con, () -> {
                return func.call(con);
            });
        });
    }

    public static void wrap(UncheckedBlock code) throws DataAccessException {
        wrap(() -> {
            code.call();
            return null;
        });
    }

    public static <T> T wrap(Callable<T> code) throws DataAccessException {
        try {
            return code.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException(e);
        }
    }

}
