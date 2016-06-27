/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

final class DbUtils {
    private final static Logger logger = LoggerFactory.getLogger("DB_UTILS");
    private final Connection conn;

    DbUtils(Connection conn) {
        this.conn = conn;
    }

    public static BatchOperator newBatchOperator(Connection conn) {
        return new BatchOperator(conn);
    }

    /**
     * 执行insert update delete SQl
     *
     * @param sql    SQL语句
     * @param params 参数列表
     * @return       影响的行数
     */
    public int executeSQL(String sql, Object... params) {
        this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
        PreparedStatement ps = null;
        int rows = 0;
        try {
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rows = ps.executeUpdate();
        } catch (SQLException e) {
            this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params) + " Error Code : " + e.getErrorCode(), e);
        } finally {
            close(null,ps,conn);
        }
        return rows;
    }

    /**
     * 执行insert update delete SQl
     *
     * @param sql               SQL语句
     * @param generatedKeys     当执行insert操作的时候，返回字段生产的key
     * @param params            参数列表
     * @param <T>               类型
     * @return
     */
    public <T> T executeSQL(String sql, IRowMap<T> generatedKeys, Object... params) {
        this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            ps.executeUpdate();
            // 检索由于执行此 Statement 对象而创建的所有自动生成的键
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return generatedKeys.mapRow(rs);
            }
        } catch (SQLException e) {
            this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params) + " Error Code : " + e.getErrorCode(), e);
        } finally {
            close(rs,ps,conn);
        }
        return null;
    }

    /**
     * 根据Select查询产生Object对象
     *
     * @param sql
     * @param map
     * @param params
     * @return
     */
    public <T> T queryForObject(String sql, IRowMap<T> map, Object... params) {
        this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
        T obj = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                obj = map.mapRow(rs);
            }
        } catch (SQLException e) {
            this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params) + " Error Code : " + e.getErrorCode(), e);
        } finally {
            close(rs,ps,conn);
        }
        return obj;
    }

    /**
     * 根据SQL查询 返回int类型结果
     *
     * @param sql
     * @param params
     * @return
     */
    public int queryForInt(String sql, Object... params) {
        this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
        int obj = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            if (rs.next()) {
                obj = rs.getInt(1);
            }
        } catch (SQLException e) {
            this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params) + " Error Code : " + e.getErrorCode(), e);
        } finally {
            close(rs,ps,conn);
        }
        return obj;
    }

    /**
     * 根据Select查询产生List集合
     *
     * @param sql
     * @param map
     * @param params
     * @return
     */
    public <T> List<T> queryForList(String sql, IRowMap<T> map, Object... params) {
        this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
        List<T> list = new ArrayList<T>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);

            if (params != null && params.length > 0) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, params[i]);
                }
            }
            rs = ps.executeQuery();
            while (rs.next()) {
                T obj = map.mapRow(rs);
                list.add(obj);
            }
        } catch (SQLException e) {
            this.Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params) + " Error Code : " + e.getErrorCode(), e);
        } finally {
            close(rs,ps,conn);
        }
        return list;
    }

    /**
     * 关闭操作
     *
     * @param rs        结果集
     * @param ps        处理
     * @param conn      连接
     */
    private static void close(ResultSet rs,PreparedStatement ps, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                Debug("Close Rs Error! Code : " + e.getErrorCode(), e);
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                Debug("Close Ps Error! Code : " + e.getErrorCode(), e);
            }
        }
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Debug("Close Conn Error! Code : " + e.getErrorCode(), e);
            }
        }
    }

    private static void Debug(String msg) {
        if(logger.isDebugEnabled()) {
            logger.debug(msg);
        }
    }

    private static void Debug(String msg,Throwable e) {
        if(logger.isDebugEnabled()) {
            logger.debug(msg,e);
        }
    }

    static class BatchOperator {
        private final boolean origAutoCommit;
        private final Connection connection;

        public BatchOperator(Connection connection) {
            this.connection = connection;
            boolean tmp;
            try {
                tmp = this.connection.getAutoCommit();
            } catch (SQLException e) {
                Debug("Get Connection Commit State Err!",e);
                tmp = true;
            }
            origAutoCommit = tmp;
        }

        public BatchOperator begin() throws SQLException {
            connection.setAutoCommit(false);
            return this;
        }

        public BatchOperator commit() throws SQLException {
            connection.commit();
            return this;
        }

        public BatchOperator roll() throws SQLException {
            connection.rollback();
            return this;
        }

        public BatchOperator finnal() throws SQLException {
            connection.setAutoCommit(origAutoCommit);
            close(null, null, connection);
            return this;
        }

        /**
         * 执行insert update delete SQl
         *
         * @param sql    SQL语句
         * @param params 参数列表
         * @return       影响的行数
         */
        public int insert(String sql, Object... params) throws SQLException {
            Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
            int rows = 0;
            PreparedStatement ps = null;
            try {
                ps = connection.prepareStatement(sql);
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                }
                rows = ps.executeUpdate();
            } finally {
                close(null,ps,null);
            }
            return rows;
        }

        /**
         * 执行insert update delete SQl
         *
         * @param sql               SQL语句
         * @param generatedKeys     当执行insert操作的时候，返回字段生产的key
         * @param params            参数列表
         * @param <T>               类型
         * @return
         */
        public <T> T insert(String sql, IRowMap<T> generatedKeys, Object... params) throws SQLException {
            Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                }
                ps.executeUpdate();
                // 检索由于执行此 Statement 对象而创建的所有自动生成的键
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return generatedKeys.mapRow(rs);
                }
            } finally {
                close(rs,ps,null);
            }
            return null;
        }

        public <T> T queryForObject(String sql, IRowMap<T> map, Object... params) throws SQLException {
            Debug(String.format("executeSQL:" + sql.replace("?", "%s"), params));
            T obj = null;
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = connection.prepareStatement(sql);
                if (params != null && params.length > 0) {
                    for (int i = 0; i < params.length; i++) {
                        ps.setObject(i + 1, params[i]);
                    }
                }
                rs = ps.executeQuery();
                if (rs.next()) {
                    obj = map.mapRow(rs);
                }
            } finally {
                close(rs,ps,null);
            }
            return obj;
        }

    }
}

interface IRowMap<T> {
    T mapRow(ResultSet rs) throws SQLException;
}


