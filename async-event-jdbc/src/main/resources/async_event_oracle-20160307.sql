
CREATE TABLE  customer.ASYNC_EVENT
(
ID                       NUMBER(11) NOT NULL,
STATUS                   VARCHAR2(2 ) NOT NULL,
TYPE                     VARCHAR2(128) NOT NULL,
EXPRESSION               VARCHAR2(128 ) ,
DATA                     VARCHAR2(2000 ) ,
MARK                     VARCHAR2(32) ,
RETRY_COUNT              NUMBER(4) ,
CREATED_DATE             TIMESTAMP (6) NOT NULL,
MODIFIED_DATE            TIMESTAMP (6) NOT NULL
);

COMMENT ON TABLE   customer.ASYNC_EVENT IS '异步事件表';
COMMENT ON COLUMN  customer.ASYNC_EVENT.ID IS '主键ID';
COMMENT ON COLUMN  customer.ASYNC_EVENT.STATUS IS '状态（0，成功，1，待消费，2，待重试，3，私信）';
COMMENT ON COLUMN  customer.ASYNC_EVENT.TYPE IS '事件类型（class名称）';
COMMENT ON COLUMN  customer.ASYNC_EVENT.EXPRESSION IS '订阅者表达式。结构：（className#methodName）';
COMMENT ON COLUMN  customer.ASYNC_EVENT.DATA IS '事件对象（json串）';
COMMENT ON COLUMN  customer.ASYNC_EVENT.MARK IS '备注';
COMMENT ON COLUMN  customer.ASYNC_EVENT.RETRY_COUNT IS '重试次数';
COMMENT ON COLUMN  customer.ASYNC_EVENT.CREATED_DATE IS '创建时间';
COMMENT ON COLUMN  customer.ASYNC_EVENT.MODIFIED_DATE IS '修改时间';


ALTER TABLE  customer.ASYNC_EVENT add constraint PK_ASYNC_EVENT primary key (ID);

create sequence  customer.SEQ_ASYNC_EVENT_ID
minvalue 1
maxvalue 99999999999
start with 1
increment by 1
cache 100;

CREATE TABLE  customer.ASYNC_LEASE
(
ID                           NUMBER(11) NOT NULL,
PERIOD                       NUMBER(11) NOT NULL,
VERSION                      NUMBER(11) NOT NULL,
MASTER                       VARCHAR2(32 ) ,
CREATED_DATE                 TIMESTAMP (6) NOT NULL,
MODIFIED_DATE                TIMESTAMP (6) NOT NULL
);
COMMENT ON TABLE   customer.ASYNC_LEASE IS '异步事件租期表';
COMMENT ON COLUMN  customer.ASYNC_LEASE.ID IS '主键ID';
COMMENT ON COLUMN  customer.ASYNC_LEASE.PERIOD IS '租期(单位s)';
COMMENT ON COLUMN  customer.ASYNC_LEASE.VERSION IS '版本（用于并发控制）';
COMMENT ON COLUMN  customer.ASYNC_LEASE.MASTER IS '租期持有者信息(主节点ip_pid_romdam)';
COMMENT ON COLUMN  customer.ASYNC_LEASE.CREATED_DATE IS '创建时间';
COMMENT ON COLUMN  customer.ASYNC_LEASE.MODIFIED_DATE IS '修改时间';

ALTER TABLE  customer.ASYNC_LEASE add constraint PK_ASYNC_LEASE primary key (ID);

CREATE TABLE  customer.ASYNC_LEASE_OFFLINE
(
ID                           NUMBER(11) NOT NULL,
MASTER                       VARCHAR2(32 ) ,
CREATED_DATE                 TIMESTAMP (6) NOT NULL,
MODIFIED_DATE                TIMESTAMP (6) NOT NULL
);
COMMENT ON TABLE   customer.ASYNC_LEASE_OFFLINE IS '异步事件租期持有者下线表';
COMMENT ON COLUMN  customer.ASYNC_LEASE_OFFLINE.ID IS '主键ID';
COMMENT ON COLUMN  customer.ASYNC_LEASE_OFFLINE.MASTER IS '租期持有者信息(主节点ip_pid_romdam)';
COMMENT ON COLUMN  customer.ASYNC_LEASE_OFFLINE.CREATED_DATE IS '创建时间';
COMMENT ON COLUMN  customer.ASYNC_LEASE_OFFLINE.MODIFIED_DATE IS '修改时间';

ALTER TABLE  customer.ASYNC_LEASE_OFFLINE add constraint PK_ASYNC_LEASE_OFFLINE primary key (ID);

create sequence  customer.SEQ_ASYNC_LEASE_OFFLINE
minvalue 1
maxvalue 99999999999
start with 1
increment by 1
cache 100;