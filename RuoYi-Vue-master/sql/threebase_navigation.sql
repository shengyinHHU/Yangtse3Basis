-- 三基业务导航配置表
-- 若依原生 sys_menu 仍用于系统路由和权限；以下两张表只管理三基业务页内部导航。

create table if not exists threebase_module (
  module_id bigint(20) not null auto_increment comment '模块ID',
  code varchar(80) not null comment '模块编码',
  name varchar(50) not null comment '模块名称',
  category varchar(50) default '基础工作' comment '业务分类',
  owner varchar(50) default '待配置' comment '责任部门',
  summary varchar(500) default '' comment '模块说明',
  status varchar(20) default 'implemented' comment '状态',
  order_num int(4) default 0 comment '显示排序',
  visible char(1) default '0' comment '是否显示（0显示 1隐藏）',
  create_time datetime comment '创建时间',
  update_time datetime comment '更新时间',
  primary key (module_id),
  unique key uk_threebase_module_code (code)
) engine=innodb auto_increment=100 comment='三基业务导航模块表';

create table if not exists threebase_feature (
  feature_id bigint(20) not null auto_increment comment '功能ID',
  module_code varchar(80) not null comment '所属模块编码',
  code varchar(80) not null comment '功能编码',
  name varchar(50) not null comment '功能名称',
  type varchar(30) default 'document' comment '功能类型',
  status varchar(20) default 'implemented' comment '状态',
  description varchar(500) default '' comment '功能说明',
  tags text comment '功能标签JSON',
  fields text comment '字段清单JSON',
  order_num int(4) default 0 comment '显示排序',
  visible char(1) default '0' comment '是否显示（0显示 1隐藏）',
  create_time datetime comment '创建时间',
  update_time datetime comment '更新时间',
  primary key (feature_id),
  unique key uk_threebase_feature_code (code),
  key idx_threebase_feature_module (module_code)
) engine=innodb auto_increment=1000 comment='三基业务导航功能表';
