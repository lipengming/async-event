/**
 * Copyright (c) 2015, www.cubbery.com. All rights reserved.
 */
package com.cubbery.event.storage;

import com.cubbery.event.exception.EventStorageException;
import com.cubbery.event.EventStorage;
import com.cubbery.event.event.EventState;
import com.cubbery.event.event.Offline;
import com.cubbery.event.event.RetryEvent;
import com.cubbery.event.event.SimpleEvent;
import com.cubbery.event.retry.Lease;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class JdbcEventStorage implements EventStorage {
    private final DataSource dataSource;
    private final DataSourceType dataSourceType;

    public JdbcEventStorage(DataSource dataSource) {
        this(dataSource,DataSourceType.ORACLE);
    }

    public JdbcEventStorage(DataSource dataSource,DataSourceType dataSourceType) {
        this.dataSource = dataSource;
        this.dataSourceType = dataSourceType;
        initSql();
    }

    public String EVENT_KEY_ORAC ;
    public String EVENT_INSERT_ORACLE;
    public String EVENT_DEAD ;
    public String EVENT_SUCCESS ;
    public String EVENT_RETRY ;
    public String EVENT_BATCH_RETRY ;
    public String EVENT_BATCH_DEAD;
    public String EVENT_ALL_RETRY ;
    public String LEASE_ALL ;
    public String LEASE_ALTER ;
    public String LEASE_ADD ;
    public String OFFLINE_ADD_ORACLE ;
    public String OFFLINE_KEY_ORACLE ;
    public String OFFLINE_SEL ;

    private void initSql() {
        /**------------------------------**/
        /**------------事件--------------**/
        /**------------------------------**/

        /** 事件持久化主键自增序列(oracle) **/
        EVENT_KEY_ORAC = "SELECT SEQ_ASYNC_EVENT_ID.nextval as id FROM dual";
        /** 事件持久化(oracle) **/
        EVENT_INSERT_ORACLE = "insert into async_event(id,status,data,mark,type,expression,retry_count,created_date,modified_date) values ( ?," + EventState.CONSUME + ",?,?,?,?,0,systimestamp,systimestamp ) ";

        /** 死亡事件 **/
        EVENT_DEAD = "update async_event set status = " + EventState.DEAD +" where id = ?";
        /** 成功事件 **/
        EVENT_SUCCESS = "update async_event set status = " + EventState.SUCCESS +" where id = ?";
        /** 重试事件 **/
        EVENT_RETRY = "update async_event set retry_count = retry_count + 1 , status = " + EventState.RETRY +" where id = ? and status != " + EventState.SUCCESS + " and status != " + EventState.DEAD;
        /** 批量重试事件(对于10分钟内仍旧没有被重试的消息待消费消息，标记为重试消息) **/
        EVENT_BATCH_RETRY = "update async_event set retry_count = retry_count + 1 , status = " + EventState.RETRY + " where created_date < systimestamp - interval '10' minute and status = " + EventState.CONSUME;
        /** 批量死信事件**/
        EVENT_BATCH_DEAD = "update async_event set status = " + EventState.DEAD + " where  retry_count >= ?  and status = " + EventState.RETRY;
        /** 分页查找重试事件 **/
        EVENT_ALL_RETRY = "select id,status,expression,data,mark,type,created_date ,modified_date from async_event where  retry_count < ? and status = " + EventState.RETRY + " and rownum <= 100";

        /**------------------------------**/
        /**------------租约--------------**/
        /**------------------------------**/

        /** 查询租约 **/
        LEASE_ALL = "select id,period,master,version,created_date ,modified_date ,systimestamp as now from async_lease";
        /** 竞争租约 **/
        LEASE_ALTER = "update async_lease set master = ?,version = ?,period = ?,modified_date = systimestamp  where version = ?";
        /** 初始化租约 **/
        LEASE_ADD = "insert into async_lease(id,period,master,version,created_date,modified_date) values (1,?,'127.0.0.1',1,systimestamp,systimestamp)";

        /**------------------------------**/
        /**------------下线--------------**/
        /**------------------------------**/

        /** 节点下线(oracle) **/
        OFFLINE_ADD_ORACLE = "insert into async_lease_offline(id,master,created_date,modified_date) values (?,?,systimestamp,systimestamp)";
        /** 节点下线记录序列(oracle) **/
        OFFLINE_KEY_ORACLE = "SELECT SEQ_ASYNC_LEASE_OFFLINE.nextval as id FROM dual";
        /** 查找最近下线节点记录 **/
        OFFLINE_SEL = "select id,master,created_date,modified_date ,systimestamp as now from async_lease_offline where rownum = 1 order by id desc";
    }

    @Override
    public void insertEvent(List<SimpleEvent> events) {
        DbUtils.BatchOperator insertMan = null;
        try {
            insertMan = DbUtils.newBatchOperator(dataSource.getConnection()).begin();
            for(final SimpleEvent event : events) {
                if(DataSourceType.ORACLE.equals(this.dataSourceType)) {
                    insertMan.queryForObject(EVENT_KEY_ORAC, new IRowMap<Long>() {
                        @Override
                        public Long mapRow(ResultSet rs) throws SQLException {
                            Long id = rs.getLong("id");
                            event.setId(id);
                            return id;
                        }
                    });
                    if(event.getId() > 0) {
                        insertMan.insert(EVENT_INSERT_ORACLE, event.getId(), event.getData(), event.getMark(), event.getType(), event.getExpression());
                    } else {
                        throw new SQLException("Seq query should not be null!");
                    }
                }
            }
            insertMan.commit();
        } catch (SQLException e) {
            try {
                insertMan.roll();//roll back
            } catch (SQLException e1) {
            }
            throw new EventStorageException(e);
        } finally {
            try {
                insertMan.finnal();//close connection
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public int markAsDead(long id) {
        try {
            return new DbUtils(dataSource.getConnection()).executeSQL(EVENT_DEAD,id);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public int markAsSuccess(long id) {
        try {
            return new DbUtils(dataSource.getConnection()).executeSQL(EVENT_SUCCESS,id);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public int markAsRetry(long id) {
        try {
            return new DbUtils(dataSource.getConnection()).executeSQL(EVENT_RETRY,id);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public int batchMarkAsRetry() {
        try {
            return new DbUtils(dataSource.getConnection()).executeSQL(EVENT_BATCH_RETRY);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public int batchMarkAsDead(int maxRetryCount) {
        try {
            return new DbUtils(dataSource.getConnection()).executeSQL(EVENT_BATCH_DEAD,maxRetryCount);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public List<RetryEvent> selectRetryEvents(int maxRetryCount) {
        try {
            return new DbUtils(dataSource.getConnection()).queryForList(EVENT_ALL_RETRY,new IRowMap<RetryEvent>() {
                @Override
                public RetryEvent mapRow(ResultSet rs) throws SQLException {
                    RetryEvent event = new RetryEvent();
                    event.setCreatedTime(rs.getTimestamp("created_date"));
                    event.setModifiedTime(rs.getTimestamp("modified_date"));

                    event.setMark(rs.getString("mark"));
                    event.setData(rs.getString("data"));
                    event.setStatus(rs.getInt("status"));
                    event.setId(rs.getLong("id"));
                    event.setExpression(rs.getString("expression"));
                    event.setType(rs.getString("type"));
                    return event;
                }
            },maxRetryCount);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public Lease selectLease() {
        try {
            return new DbUtils(dataSource.getConnection()).queryForObject(LEASE_ALL,new IRowMap<Lease>() {
                @Override
                public Lease mapRow(ResultSet rs) throws SQLException {
                    Lease lease = new Lease();
                    lease.setCreatedTime(rs.getTimestamp("created_date"));
                    lease.setModifiedTime(rs.getTimestamp("modified_date"));
                    lease.setId(rs.getLong("id"));
                    lease.setPeriod(rs.getLong("period"));
                    lease.setMaster(rs.getString("master"));
                    lease.setVersion(rs.getLong("version"));
                    lease.setNow(rs.getTimestamp("now"));
                    return lease;
                }
            });
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public int updateLease(String masterInfo, long oldVersion,long period) {
        try {
            return new DbUtils(dataSource.getConnection()).executeSQL(LEASE_ALTER,masterInfo,oldVersion + 1,period,oldVersion);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public void initLease(long period) {
        try {
            new DbUtils(dataSource.getConnection()).executeSQL(LEASE_ADD,period);
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public int confirmOffline(Offline offline) {
        try {
            if(DataSourceType.ORACLE.equals(this.dataSourceType)) {
                long id = new DbUtils(dataSource.getConnection()).queryForObject(OFFLINE_KEY_ORACLE,new IRowMap<Long>(){
                    @Override
                    public Long mapRow(ResultSet rs) throws SQLException {
                        return rs.getLong("id");
                    }
                });
                if(id > 0) {
                    return new DbUtils(dataSource.getConnection()).executeSQL(OFFLINE_ADD_ORACLE,id,offline.getMaster());
                }
            }
            return 0;
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }

    @Override
    public Offline getLastOffline() {
        try {
            return new DbUtils(dataSource.getConnection()).queryForObject(OFFLINE_SEL, new IRowMap<Offline>() {

                @Override
                public Offline mapRow(ResultSet rs) throws SQLException {
                    if(!rs.next()) return null;
                    Offline offline = new Offline();
                    offline.setCreatedTime(rs.getTimestamp("created_date"));
                    offline.setModifiedTime(rs.getTimestamp("modified_date"));
                    offline.setId(rs.getLong("id"));
                    offline.setMaster(rs.getString("master"));
                    offline.setNow(rs.getTimestamp("now"));
                    return offline;
                }
            });
        } catch (SQLException e) {
            throw new EventStorageException(e);
        }
    }
}
