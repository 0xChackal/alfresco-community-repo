update ACT_GE_PROPERTY set VALUE_ = '7.1.0-M16' where NAME_ = 'schema.version';

update ACT_GE_PROPERTY set VALUE_ = '7.1.0-M16' where NAME_ = 'schema.history';

alter table ACT_RE_DEPLOYMENT add column VERSION_ int default 1;
alter table ACT_RE_DEPLOYMENT add column PROJECT_RELEASE_VERSION_ nvarchar(255);
alter table ACT_RE_DEPLOYMENT add column KEY_ varchar(255);
alter table ACT_RE_DEPLOYMENT add column ENGINE_VERSION_ varchar(255);

alter table ACT_RE_PROCDEF add column APP_VERSION_ integer;
alter table ACT_RE_PROCDEF add column ENGINE_VERSION_ varchar(255);

alter table ACT_RU_EXECUTION add column APP_VERSION_ integer;
alter table ACT_RU_EXECUTION add column IS_COUNT_ENABLED_ TINYINT;
alter table ACT_RU_EXECUTION add column EVT_SUBSCR_COUNT_ integer;
alter table ACT_RU_EXECUTION add column TASK_COUNT_ integer;
alter table ACT_RU_EXECUTION add column JOB_COUNT_ integer;
alter table ACT_RU_EXECUTION add column TIMER_JOB_COUNT_ integer;
alter table ACT_RU_EXECUTION add column SUSP_JOB_COUNT_ integer;
alter table ACT_RU_EXECUTION add column DEADLETTER_JOB_COUNT_ integer;
alter table ACT_RU_EXECUTION add column VAR_COUNT_ integer;
alter table ACT_RU_EXECUTION add column ID_LINK_COUNT_ integer;
alter table ACT_RU_EXECUTION add column START_TIME_ datetime;
alter table ACT_RU_EXECUTION add column START_USER_ID_ varchar(255);
alter table ACT_RU_EXECUTION add column IS_MI_ROOT_ TINYINT;
alter table ACT_RU_EXECUTION add column ROOT_PROC_INST_ID_ varchar(64);

create index ACT_IDC_EXEC_ROOT on ACT_RU_EXECUTION(ROOT_PROC_INST_ID_);

alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_PARENT;
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_PARENT foreign key (PARENT_ID_) references ACT_RU_EXECUTION (ID_) on delete cascade;

alter table ACT_RU_EXECUTION drop foreign key ACT_FK_EXE_SUPER;
alter table ACT_RU_EXECUTION add constraint ACT_FK_EXE_SUPER foreign key (SUPER_EXEC_) references ACT_RU_EXECUTION (ID_) on delete cascade;

update ACT_RU_EVENT_SUBSCR set PROC_DEF_ID_ = CONFIGURATION_ where EVENT_TYPE_ = 'message' and PROC_INST_ID_ is null and EXECUTION_ID_ is null;

alter table ACT_RE_DEPLOYMENT modify DEPLOY_TIME_ timestamp(3) NULL;

alter table ACT_RU_TASK modify CREATE_TIME_ timestamp(3) NULL;

alter table ACT_RU_TASK add column BUSINESS_KEY_ varchar(255);
alter table ACT_RU_TASK add column CLAIM_TIME_ datetime;
alter table ACT_RU_TASK add column APP_VERSION_ integer;

create table ACT_RU_TIMER_JOB (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    TYPE_ varchar(255) NOT NULL,
    LOCK_EXP_TIME_ timestamp NULL,
    LOCK_OWNER_ varchar(255),
    EXCLUSIVE_ boolean,
    EXECUTION_ID_ varchar(64),
    PROCESS_INSTANCE_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    RETRIES_ integer,
    EXCEPTION_STACK_ID_ varchar(64),
    EXCEPTION_MSG_ varchar(4000),
    DUEDATE_ timestamp NULL,
    REPEAT_ varchar(255),
    HANDLER_TYPE_ varchar(255),
    HANDLER_CFG_ varchar(4000),
    TENANT_ID_ varchar(255) default '',
    primary key (ID_)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;

create table ACT_RU_SUSPENDED_JOB (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    TYPE_ varchar(255) NOT NULL,
    EXCLUSIVE_ boolean,
    EXECUTION_ID_ varchar(64),
    PROCESS_INSTANCE_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    RETRIES_ integer,
    EXCEPTION_STACK_ID_ varchar(64),
    EXCEPTION_MSG_ varchar(4000),
    DUEDATE_ timestamp NULL,
    REPEAT_ varchar(255),
    HANDLER_TYPE_ varchar(255),
    HANDLER_CFG_ varchar(4000),
    TENANT_ID_ varchar(255) default '',
    primary key (ID_)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;

create table ACT_RU_DEADLETTER_JOB (
    ID_ varchar(64) NOT NULL,
    REV_ integer,
    TYPE_ varchar(255) NOT NULL,
    EXCLUSIVE_ boolean,
    EXECUTION_ID_ varchar(64),
    PROCESS_INSTANCE_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    EXCEPTION_STACK_ID_ varchar(64),
    EXCEPTION_MSG_ varchar(4000),
    DUEDATE_ timestamp NULL,
    REPEAT_ varchar(255),
    HANDLER_TYPE_ varchar(255),
    HANDLER_CFG_ varchar(4000),
    TENANT_ID_ varchar(255) default '',
    primary key (ID_)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;

create table ACT_RU_INTEGRATION (
    ID_ varchar(64) not null,
    EXECUTION_ID_ varchar(64),
    PROCESS_INSTANCE_ID_ varchar(64),
    PROC_DEF_ID_ varchar(64),
    FLOW_NODE_ID_ varchar(64),
    CREATED_DATE_ timestamp,
    primary key (ID_)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE utf8_bin;

alter table ACT_RU_JOB
    add constraint ACT_FK_JOB_EXECUTION
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_JOB
    add constraint ACT_FK_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_JOB
    add constraint ACT_FK_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);

alter table ACT_RU_TIMER_JOB
    add constraint ACT_FK_TIMER_JOB_EXECUTION
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_TIMER_JOB
    add constraint ACT_FK_TIMER_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_TIMER_JOB
    add constraint ACT_FK_TIMER_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);

alter table ACT_RU_TIMER_JOB
    add constraint ACT_FK_TIMER_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_)
            references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_SUSPENDED_JOB
    add constraint ACT_FK_SUSPENDED_JOB_EXECUTION
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_SUSPENDED_JOB
    add constraint ACT_FK_SUSPENDED_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_SUSPENDED_JOB
    add constraint ACT_FK_SUSPENDED_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);

alter table ACT_RU_SUSPENDED_JOB
    add constraint ACT_FK_SUSPENDED_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_)
            references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_DEADLETTER_JOB
    add constraint ACT_FK_DEADLETTER_JOB_EXECUTION
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_DEADLETTER_JOB
    add constraint ACT_FK_DEADLETTER_JOB_PROCESS_INSTANCE
        foreign key (PROCESS_INSTANCE_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_DEADLETTER_JOB
    add constraint ACT_FK_DEADLETTER_JOB_PROC_DEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);

alter table ACT_RU_DEADLETTER_JOB
    add constraint ACT_FK_DEADLETTER_JOB_EXCEPTION
        foreign key (EXCEPTION_STACK_ID_)
            references ACT_GE_BYTEARRAY (ID_);

alter table ACT_RU_INTEGRATION
    add constraint ACT_FK_INT_EXECUTION
        foreign key (EXECUTION_ID_)
            references ACT_RU_EXECUTION (ID_)
            on delete cascade;

alter table ACT_RU_INTEGRATION
    add constraint ACT_FK_INT_PROC_INST
        foreign key (PROCESS_INSTANCE_ID_)
            references ACT_RU_EXECUTION (ID_);

alter table ACT_RU_INTEGRATION
    add constraint ACT_FK_INT_PROC_DEF
        foreign key (PROC_DEF_ID_)
            references ACT_RE_PROCDEF (ID_);


INSERT INTO ACT_RU_DEADLETTER_JOB (ID_, REV_, TYPE_, EXCLUSIVE_, EXECUTION_ID_, PROCESS_INSTANCE_ID_, PROC_DEF_ID_,
                                   EXCEPTION_STACK_ID_,     EXCEPTION_MSG_, DUEDATE_, REPEAT_, HANDLER_TYPE_, HANDLER_CFG_, TENANT_ID_)
    (SELECT ID_, REV_, TYPE_, EXCLUSIVE_, EXECUTION_ID_, PROCESS_INSTANCE_ID_, PROC_DEF_ID_,
            EXCEPTION_STACK_ID_, EXCEPTION_MSG_, DUEDATE_, REPEAT_, HANDLER_TYPE_, HANDLER_CFG_, TENANT_ID_
     from ACT_RU_JOB WHERE RETRIES_ <= 0);

DELETE FROM ACT_RU_JOB
WHERE RETRIES_ <= 0;


INSERT INTO ACT_RU_SUSPENDED_JOB (ID_, REV_, TYPE_, EXCLUSIVE_, EXECUTION_ID_, PROCESS_INSTANCE_ID_, PROC_DEF_ID_,
                                  RETRIES_, EXCEPTION_STACK_ID_,     EXCEPTION_MSG_, DUEDATE_, REPEAT_, HANDLER_TYPE_, HANDLER_CFG_, TENANT_ID_)
    (SELECT job.ID_, job.REV_, job.TYPE_, job.EXCLUSIVE_, job.EXECUTION_ID_, job.PROCESS_INSTANCE_ID_, job.PROC_DEF_ID_,
            job.RETRIES_, job.EXCEPTION_STACK_ID_, job.EXCEPTION_MSG_, job.DUEDATE_, job.REPEAT_, job.HANDLER_TYPE_, job.HANDLER_CFG_, job.TENANT_ID_
     from ACT_RU_JOB job INNER JOIN ACT_RU_EXECUTION execution on execution.ID_ = job.PROCESS_INSTANCE_ID_ where execution.SUSPENSION_STATE_ = 2);

DELETE FROM ACT_RU_JOB
WHERE PROCESS_INSTANCE_ID_ IN (SELECT ID_ FROM ACT_RU_EXECUTION execution WHERE execution.PARENT_ID_ is null and execution.SUSPENSION_STATE_ = 2);


INSERT INTO ACT_RU_TIMER_JOB (ID_, REV_, TYPE_, LOCK_EXP_TIME_, LOCK_OWNER_, EXCLUSIVE_, EXECUTION_ID_, PROCESS_INSTANCE_ID_,
                              PROC_DEF_ID_, RETRIES_, EXCEPTION_STACK_ID_, EXCEPTION_MSG_, DUEDATE_, REPEAT_, HANDLER_TYPE_, HANDLER_CFG_, TENANT_ID_)
    (SELECT ID_, REV_, TYPE_, LOCK_EXP_TIME_, LOCK_OWNER_, EXCLUSIVE_, EXECUTION_ID_, PROCESS_INSTANCE_ID_,
            PROC_DEF_ID_, RETRIES_, EXCEPTION_STACK_ID_, EXCEPTION_MSG_, DUEDATE_, REPEAT_, HANDLER_TYPE_, HANDLER_CFG_, TENANT_ID_
     from ACT_RU_JOB WHERE (HANDLER_TYPE_ = 'activate-processdefinition' or HANDLER_TYPE_ = 'suspend-processdefinition'
        or HANDLER_TYPE_ = 'timer-intermediate-transition' or HANDLER_TYPE_ = 'timer-start-event' or HANDLER_TYPE_ = 'timer-transition') and LOCK_EXP_TIME_ is null);


DELETE FROM ACT_RU_JOB
WHERE (HANDLER_TYPE_ = 'activate-processdefinition'
    or HANDLER_TYPE_ = 'suspend-processdefinition'
    or HANDLER_TYPE_ = 'timer-intermediate-transition'
    or HANDLER_TYPE_ = 'timer-start-event'
    or HANDLER_TYPE_ = 'timer-transition')
  and LOCK_EXP_TIME_ is null;


update ACT_RU_EVENT_SUBSCR set PROC_DEF_ID_ = CONFIGURATION_ where EVENT_TYPE_ = 'message' and PROC_INST_ID_ is null and EXECUTION_ID_ is null and PROC_DEF_ID_ is null;