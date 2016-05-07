------ common
-- Table: cti_link_sip_group

-- DROP TABLE cti_link_sip_group;

CREATE TABLE cti_link_sip_group
(
  id serial NOT NULL, -- sip media server组id，用于灰度升级
  percent integer, -- 流量百分比
  description character varying, -- 说明
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_sip_group_pkey PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE cti_link_sip_group OWNER TO postgres;
COMMENT ON TABLE cti_link_sip_group IS 'sip media server组，用于灰度升级';
COMMENT ON COLUMN cti_link_sip_group.id IS 'sip media server组id';
COMMENT ON COLUMN cti_link_sip_group.percent IS '流量百分比';
COMMENT ON COLUMN cti_link_sip_group.description IS '说明';
COMMENT ON COLUMN cti_link_sip_group.create_time IS '记录创建时间';

-- Table: cti_link_sip_media_server

-- DROP TABLE cti_link_sip_media_server;

CREATE TABLE cti_link_sip_media_server
(
  id serial NOT NULL, -- 流水号
  instance_id character varying, -- 实例唯一id
  mac character varying, -- 实例mac地址
  group_id integer NOT NULL, -- sip media server组id
  name character varying, -- 名字 唯一
  ip_addr character varying, -- IP地址
  external_ip_addr character varying, -- 实例公网ip地址
  port integer DEFAULT 5060, -- sip信令端口
  description character varying, -- 说明
  status integer, -- sip media server的状态，比如正常和不正常
  active integer DEFAULT 1, -- 是否激活 1激活 0暂停
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_sip_media_server_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_sip_media_server_group_id_fkey FOREIGN KEY (group_id)
    REFERENCES cti_link_sip_group (id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_sip_media_server OWNER TO postgres;
COMMENT ON TABLE cti_link_sip_media_server IS '网关列表';
COMMENT ON COLUMN cti_link_sip_media_server.id IS 'id标识';
COMMENT ON COLUMN cti_link_sip_media_server.instance_id IS '实例唯一id';
COMMENT ON COLUMN cti_link_sip_media_server.mac IS '实例mac地址';
COMMENT ON COLUMN cti_link_sip_media_server.group_id IS 'sip media server组id';
COMMENT ON COLUMN cti_link_sip_media_server.name IS '名字 唯一';
COMMENT ON COLUMN cti_link_sip_media_server.ip_addr IS 'IP地址';
COMMENT ON COLUMN cti_link_sip_media_server.external_ip_addr IS '实例公网ip地址';
COMMENT ON COLUMN cti_link_sip_media_server.port IS 'sip信令端口';
COMMENT ON COLUMN cti_link_sip_media_server.description IS '说明';
COMMENT ON COLUMN cti_link_sip_media_server.status IS 'sip media server的状态，比如正常和不正常';
COMMENT ON COLUMN cti_link_sip_media_server.active IS '是否激活 1激活 0暂停';
COMMENT ON COLUMN cti_link_sip_media_server.create_time IS '记录创建时间';

-- Table: cti_link_sip_proxy

-- DROP TABLE cti_link_sip_proxy;
-- cti_link_sip_proxy存储sip proxy信息
CREATE TABLE cti_link_sip_proxy
(
  id serial NOT NULL, -- 流水号
  name character varying, -- 名字 唯一
  ip_addr character varying, -- IP地址
  port integer DEFAULT 5060, -- sip信令端口
  description character varying, -- 说明
  active integer DEFAULT 1, -- 是否激活 1激活 0暂停
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_sip_proxy_pkey PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE cti_link_sip_proxy OWNER TO postgres;
COMMENT ON TABLE cti_link_sip_proxy IS '网关列表';
COMMENT ON COLUMN cti_link_sip_proxy.id IS 'id标识';
COMMENT ON COLUMN cti_link_sip_proxy.name IS '名字 唯一';
COMMENT ON COLUMN cti_link_sip_proxy.ip_addr IS 'IP地址';
COMMENT ON COLUMN cti_link_sip_proxy.port IS 'sip信令端口';
COMMENT ON COLUMN cti_link_sip_proxy.description IS '说明';
COMMENT ON COLUMN cti_link_sip_proxy.active IS '是否激活 1激活 0暂停';
COMMENT ON COLUMN cti_link_sip_proxy.create_time IS '记录创建时间';

-- Table: cti_link_gateway

-- DROP TABLE cti_link_gateway;

CREATE TABLE cti_link_gateway
(
  id serial NOT NULL, -- 流水号
  name character varying, -- 名字 唯一
  prefix character varying, -- 号码前缀
  ip_addr character varying, -- IP地址
  port integer DEFAULT 5060, -- sip信令端口
  area_code character varying, -- 网关默认区号
  description character varying, -- 说明
  call_limit integer default 300, -- 网关吞吐能力
  disallow character varying DEFAULT 'all'::character varying, -- 网关codec选择disallow
  allow character varying DEFAULT 'alaw,ulaw,g729,gsm'::character varying, -- 网关codec选择allow
  dtmf_mode character varying DEFAULT 'rfc2833'::character varying, -- 网关的dtmf设置 rfc2833/info/auto/inband
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_gateway_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_gateway_name_unique UNIQUE (name)
)
WITHOUT OIDS;
ALTER TABLE cti_link_gateway OWNER TO postgres;
COMMENT ON TABLE cti_link_gateway IS '网关列表';
COMMENT ON COLUMN cti_link_gateway.id IS 'id标识';
COMMENT ON COLUMN cti_link_gateway.prefix IS '号码前缀';
COMMENT ON COLUMN cti_link_gateway.name IS '名字 唯一';
COMMENT ON COLUMN cti_link_gateway.ip_addr IS 'IP地址';
COMMENT ON COLUMN cti_link_gateway.port IS 'sip信令端口';
COMMENT ON COLUMN cti_link_gateway.area_code IS '网关默认区号';
COMMENT ON COLUMN cti_link_gateway.description IS '说明';
COMMENT ON COLUMN cti_link_gateway.call_limit IS '网关吞吐能力';
COMMENT ON COLUMN cti_link_gateway.disallow IS '网关codec选择disallow';
COMMENT ON COLUMN cti_link_gateway.allow IS '网关codec选择allow';
COMMENT ON COLUMN cti_link_gateway.dtmf_mode IS '网关的dtmf设置 rfc2833/info/auto/inband';
COMMENT ON COLUMN cti_link_gateway.create_time IS '记录创建时间';


-- Table: cti_link_routerset

-- DROP TABLE cti_link_routerset;

CREATE TABLE cti_link_routerset
(
  id serial NOT NULL, -- 流水号
  "name" character varying, -- 路由组名称
  description character varying, -- 描述
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_routerset_pkey PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE cti_link_routerset OWNER TO postgres;
COMMENT ON TABLE cti_link_routerset IS '路由组列表';
COMMENT ON COLUMN cti_link_routerset.id IS 'id标识';
COMMENT ON COLUMN cti_link_routerset."name" IS '路由组名称';
COMMENT ON COLUMN cti_link_routerset.description IS '描述';
COMMENT ON COLUMN cti_link_routerset.create_time IS '记录创建时间';

-- Table: cti_link_router

-- DROP TABLE cti_link_router;

CREATE TABLE cti_link_router
(
  id serial NOT NULL, -- 流水号
  routerset_id integer NOT NULL, -- 对应路由组id
  prefix character varying, -- 号码前缀
  gateway_id integer NOT NULL, -- 对应网关id
  priority integer NOT NULL DEFAULT 1, -- 路由优先级 数字越小越优先
  description character varying, -- 说明
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_router_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_router_routerset_id_fkey FOREIGN KEY (routerset_id)
    REFERENCES cti_link_routerset (id) MATCH SIMPLE,  
  CONSTRAINT cti_link_router_gateway_id_fkey FOREIGN KEY (gateway_id)
    REFERENCES cti_link_gateway (id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_router OWNER TO postgres;
COMMENT ON TABLE cti_link_router IS '号码路由表';
COMMENT ON COLUMN cti_link_router.id IS 'id标识';
COMMENT ON COLUMN cti_link_router.prefix IS '号码前缀';
COMMENT ON COLUMN cti_link_router.routerset_id IS '对应路由组id';
COMMENT ON COLUMN cti_link_router.gateway_id IS '对应网关id';
COMMENT ON COLUMN cti_link_router.priority IS '路由优先级 路由优先级 数字越小越优先';
COMMENT ON COLUMN cti_link_router.description IS '说明';
COMMENT ON COLUMN cti_link_router.create_time IS '记录创建时间';

-- Table: cti_link_area_code

-- DROP TABLE cti_link_area_code;

CREATE TABLE cti_link_area_code
(
  id serial NOT NULL, -- id标识
  province character varying(20), -- 省份
  prefix character varying(10), -- 万号
  area_code character varying(10), -- 区号
  city character varying(20), -- 城市
  CONSTRAINT cti_link_area_code_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_area_code
  OWNER TO postgres;
GRANT ALL ON TABLE cti_link_area_code TO postgres;
GRANT ALL ON TABLE cti_link_area_code TO public;
COMMENT ON TABLE cti_link_area_code
  IS '手机号码库';
COMMENT ON COLUMN cti_link_area_code.id IS 'id标识';
COMMENT ON COLUMN cti_link_area_code.province IS '省份';
COMMENT ON COLUMN cti_link_area_code.prefix IS '万号';
COMMENT ON COLUMN cti_link_area_code.area_code IS '区号';
COMMENT ON COLUMN cti_link_area_code.city IS '城市';

-- Index: cti_link_area_code_prefix_index

-- DROP INDEX cti_link_area_code_prefix_index;

CREATE INDEX cti_link_area_code_prefix_index
  ON cti_link_area_code
  USING btree
  (prefix );



-- Table: cti_link_public_voice

-- DROP TABLE cti_link_public_voice;

CREATE TABLE cti_link_public_voice
(
  id serial NOT NULL,
  voice_name character varying,
  path character varying,
  description character varying, 
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间
  CONSTRAINT cti_link_public_voice_pkey PRIMARY KEY (id)
) 
WITHOUT OIDS;
ALTER TABLE cti_link_public_voice OWNER TO postgres;
COMMENT ON TABLE cti_link_public_voice IS '公共语音库';
COMMENT ON COLUMN cti_link_public_voice.id IS '流水号';
COMMENT ON COLUMN cti_link_public_voice.voice_name IS '语音文件名';
COMMENT ON COLUMN cti_link_public_voice.path IS '语音文件路径';
COMMENT ON COLUMN cti_link_public_voice.description IS '描述';
COMMENT ON COLUMN cti_link_public_voice.create_time IS '记录创建时间';

-- Table: cti_link_public_moh

-- DROP TABLE cti_link_public_moh;

CREATE TABLE cti_link_public_moh
(
  id serial NOT NULL, -- 流水号
  name character varying NOT NULL, -- 类名 格式：企业号+类名
  directory character varying NOT NULL, -- 音频文件的路径
  application character varying NOT NULL default '',
  mode character varying NOT NULL default '',
  digit character varying NOT NULL default '',
  sort character varying NOT NULL default '',
  format character varying NOT NULL default '',
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_public_moh_id PRIMARY KEY (id)
) 
WITHOUT OIDS;
ALTER TABLE cti_link_public_moh OWNER TO postgres;
COMMENT ON TABLE cti_link_public_moh IS '保存music_on_hold文件路径信息';
COMMENT ON COLUMN cti_link_public_moh.id IS 'id号';
COMMENT ON COLUMN cti_link_public_moh.name IS '类名 格式：企业号+类名';
COMMENT ON COLUMN cti_link_public_moh.directory IS '音频文件的路径';
COMMENT ON COLUMN cti_link_public_moh.application IS '应用程序';
COMMENT ON COLUMN cti_link_public_moh.mode IS '模式';
COMMENT ON COLUMN cti_link_public_moh.digit IS '按键';
COMMENT ON COLUMN cti_link_public_moh.sort IS '排序方式';
COMMENT ON COLUMN cti_link_public_moh.format IS '格式';
COMMENT ON COLUMN cti_link_public_moh.create_time IS '记录创建时间';

-- Index: cti_link_public_moh_name_idex

-- DROP INDEX cti_link_public_moh_name_idex;

CREATE INDEX cti_link_public_moh_name_idex
  ON cti_link_public_moh
  USING btree
  (name);

-- Table: cti_link_public_moh_voice

-- DROP TABLE cti_link_public_moh_voice;

CREATE TABLE cti_link_public_moh_voice
(
  id serial NOT NULL, 
  moh_id integer NOT NULL,
  voice_id integer NOT NULL,
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_public_moh_voice_id PRIMARY KEY (id),
  CONSTRAINT cti_link_public_moh_voice_moh_id_fkey FOREIGN KEY (moh_id)
    REFERENCES cti_link_public_moh (id) MATCH SIMPLE,
  CONSTRAINT cti_link_public_moh_voice_voice_id_fkey FOREIGN KEY (voice_id)
    REFERENCES cti_link_public_voice (id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_public_moh_voice OWNER TO postgres;
COMMENT ON TABLE cti_link_public_moh_voice IS 'music_on_hold中语音文件';
COMMENT ON COLUMN cti_link_public_moh_voice.id IS 'id标识';
COMMENT ON COLUMN cti_link_public_moh_voice.moh_id IS 'moh类id';
COMMENT ON COLUMN cti_link_public_moh_voice.voice_id IS '语音文件id';
COMMENT ON COLUMN cti_link_public_moh_voice.create_time IS '记录创建时间';

-- Table: cti_link_system_setting

-- DROP TABLE cti_link_system_setting;

CREATE TABLE cti_link_system_setting
(
  id serial NOT NULL, -- id标识
  name character varying, -- name-value对
  value character varying, -- name-value对
  property character varying, -- 属性
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_system_setting_pkey PRIMARY KEY (id)
) 
WITHOUT OIDS;
ALTER TABLE cti_link_system_setting OWNER TO postgres;
COMMENT ON TABLE cti_link_system_setting IS '系统设置表';
COMMENT ON COLUMN cti_link_system_setting.id IS 'id标识';
COMMENT ON COLUMN cti_link_system_setting.name IS 'name-value对';
COMMENT ON COLUMN cti_link_system_setting.value IS 'name-value对';
COMMENT ON COLUMN cti_link_system_setting.property IS '属性';
COMMENT ON COLUMN cti_link_system_setting.create_time IS '记录创建时间';

-- Table: cti_link_entity

-- DROP TABLE cti_link_entity;

CREATE TABLE cti_link_entity
(
  enterprise_id integer NOT NULL,
  enterprise_name character varying,
  entity_type integer, 
  area_code character varying, 
  status integer, -- 企业业务状态 0:未开通 1:正常 2:欠费 3:停机 4:注销
  create_time timestamp with time zone DEFAULT now(), 
  CONSTRAINT cti_link_entity_pkey PRIMARY KEY (enterprise_id)
)
WITHOUT OIDS;
ALTER TABLE cti_link_entity OWNER TO postgres;
COMMENT ON TABLE cti_link_entity IS '实体基本信息表';
COMMENT ON COLUMN cti_link_entity.enterprise_id IS '企业实体ID 唯一标识 主键';
COMMENT ON COLUMN cti_link_entity.enterprise_name IS '实体名称';
COMMENT ON COLUMN cti_link_entity.entity_type IS '实体级别 3=客户';
COMMENT ON COLUMN cti_link_entity.area_code IS '所属区号';
COMMENT ON COLUMN cti_link_entity.create_time IS '创建时间';

-- Table: cti_link_trunk

-- DROP TABLE cti_link_trunk;

CREATE TABLE cti_link_trunk
(
  id serial NOT NULL,
  enterprise_id integer NOT NULL,
  sip_group_id integer default -1,
  number_trunk character varying,
  area_code character varying, 
  type integer NOT NULL,
  comment character varying,
  create_time timestamp with time zone DEFAULT now(), 
  CONSTRAINT cti_link_trunk_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_trunk_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_trunk OWNER TO postgres;
COMMENT ON TABLE cti_link_trunk IS '企业的中继号码表';
COMMENT ON COLUMN cti_link_trunk.id IS 'id标识';
COMMENT ON COLUMN cti_link_trunk.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_trunk.sip_group_id IS 'sip media server的组id，-1表示不指定id，用于按企业级灰度升级';
COMMENT ON COLUMN cti_link_trunk.number_trunk IS '中继号码';
COMMENT ON COLUMN cti_link_trunk.area_code IS '目的码所在地区区号';
COMMENT ON COLUMN cti_link_trunk.type IS '目的码类型：0--未绑定400或1010号码 1--绑定400或1010号码  2--手机虚拟号码';
COMMENT ON COLUMN cti_link_trunk.comment IS '备注';
COMMENT ON COLUMN cti_link_trunk.create_time IS '记录创建时间';

create index cti_link_trunk_enterprise_id_index on cti_link_trunk using btree(enterprise_id);

-- DROP TABLE cti_link_enterprise_hotline;
CREATE TABLE cti_link_enterprise_hotline
(
  id serial NOT NULL,
  enterprise_id integer NOT NULL, 
  hotline character varying NOT NULL,
  is_master integer NOT NULL DEFAULT 0,
  number_trunk character varying NOT NULL,
  type integer NOT NULL, -- 热线号码类型，1-400或1010号码 2--直线号码 3--手机虚拟号码
  name CHARACTER VARYING DEFAULT '',
  create_time timestamp with time zone DEFAULT now(),
  CONSTRAINT cti_link_enterprise_hotline_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_hotline_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_hotline OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_hotline IS '企业热线号码与目的码关系表';
COMMENT ON COLUMN cti_link_enterprise_hotline.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_hotline.hotline IS '热线号码，一个企业的多个热线号码都可以登录';
COMMENT ON COLUMN cti_link_enterprise_hotline.is_master IS '主热线号码 一个企业有且只有一个主热线号码';
COMMENT ON COLUMN cti_link_enterprise_hotline.number_trunk IS '目的码';
COMMENT ON COLUMN cti_link_enterprise_hotline.type IS '热线号码类型，1-400或1010号码 2--直线号码 3--手机虚拟号码';
COMMENT ON COLUMN cti_link_enterprise_hotline.create_time IS '记录创建时间';
COMMENT ON COLUMN cti_link_enterprise_hotline.name IS '热线号码名称';

create index cti_link_enterprise_hotline_enterprise_id_index on cti_link_enterprise_hotline (enterprise_id);
create index cti_link_enterprise_hotline_hotline_index on cti_link_enterprise_hotline (hotline);


-- DROP TABLE cti_link_enterprise_router;
CREATE TABLE cti_link_enterprise_router
(
  id serial NOT NULL,
  enterprise_id integer NOT NULL, -- 企业id
  ib_router_right integer NOT NULL, -- 呼入呼转
  ob_router_left integer NOT NULL, -- 外呼
  ob_router_right integer NOT NULL, -- 外呼呼转
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_router_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_router_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE,
  CONSTRAINT cti_link_enterprise_router_ib_router_right_fkey FOREIGN KEY (ib_router_right)
      REFERENCES cti_link_routerset (id) MATCH SIMPLE,
  CONSTRAINT cti_link_enterprise_router_ob_preview_router_left_fkey FOREIGN KEY (ob_router_left)
      REFERENCES cti_link_routerset (id) MATCH SIMPLE,
  CONSTRAINT cti_link_enterprise_router_ob_predictive_router_left_fkey FOREIGN KEY (ob_router_right)
      REFERENCES cti_link_routerset (id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_router OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_router IS '企业路由选择表';
COMMENT ON COLUMN cti_link_enterprise_router.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_router.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_router.ib_router_right IS '呼入呼转';
COMMENT ON COLUMN cti_link_enterprise_router.ob_router_left IS '外呼';
COMMENT ON COLUMN cti_link_enterprise_router.ob_router_right IS '外呼呼转';
COMMENT ON COLUMN cti_link_enterprise_router.create_time IS '记录创建时间';


-- DROP TABLE cti_link_enterprise_clid;
CREATE TABLE cti_link_enterprise_clid
(
  id serial NOT NULL,
  enterprise_id integer NOT NULL, -- 企业id
  ib_clid_right_type integer NOT NULL, -- 呼入透传号码类型 1:中继 2:客户 3:固定 4:按运营商分
  ib_clid_right_number character varying, -- ib_clid_right_type=1/3时的号码 多个号码以逗号分隔 ib_clid_right_type=4时号码格式：01087120766|unicom,01059222999|telecom,01087120777|mobile
  ob_clid_left_type integer NOT NULL, -- 外呼透传号码类型 1:中继 2:座席号码 3:固定
  ob_clid_left_number character varying, -- ob_preview_clid_left_type=1/3时的号码 多个号码以逗号分隔
  ob_clid_right_type integer NOT NULL, -- 外呼呼转透传号码类型 1:中继 2:客户 3:固定
  ob_clid_right_number character varying, -- ob_preview_clid_right_type=1/3时的号码 多个号码以逗号分隔
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_clid_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_clid_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_clid OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_clid IS '企业透传号码设置表';
COMMENT ON COLUMN cti_link_enterprise_clid.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_clid.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_clid.ib_clid_right_type IS '呼入透传号码类型 1:中继 2:客户 3:固定 4:热线号码';
COMMENT ON COLUMN cti_link_enterprise_clid.ib_clid_right_number IS 'ib_clid_right_type=1/3时的号码 多个号码以逗号分隔';
COMMENT ON COLUMN cti_link_enterprise_clid.ob_clid_left_type IS '外呼透传号码类型 1:中继 2:座席号码 3:固定 4:按运营商分';
COMMENT ON COLUMN cti_link_enterprise_clid.ob_clid_left_number IS 'ob_clid_left_type=1/3时的号码 多个号码以逗号分隔  ib_clid_right_type=4时号码格式：01087120766|unicom,01059222999|telecom,01087120777|mobile';
COMMENT ON COLUMN cti_link_enterprise_clid.ob_clid_right_type IS '外呼呼转透传号码类型 1:中继 2:客户 3:固定 4:按运营商分';
COMMENT ON COLUMN cti_link_enterprise_clid.ob_clid_right_number IS 'ob__clid_right_type=1/3时的号码 多个号码以逗号分隔  ob__clid_right_type=4时号码格式：01087120766|unicom,01059222999|telecom,01087120777|mobile';
COMMENT ON COLUMN cti_link_enterprise_clid.create_time IS '记录创建时间';


-- DROP TABLE cti_link_enterprise_setting;
CREATE TABLE cti_link_enterprise_setting
(
  id serial NOT NULL,
  enterprise_id integer, -- 客户enterprise_id
  name character varying, -- name-value对
  value character varying, -- nam-value对
  property character varying,
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_setting_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_setting_enterprise_id_fkey FOREIGN KEY (enterprise_id)
  REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_setting OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_setting IS '企业客户扩展设置表';
COMMENT ON COLUMN cti_link_enterprise_setting.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_setting.enterprise_id IS '客户enterprise_id ';
COMMENT ON COLUMN cti_link_enterprise_setting.name IS 'name-value对';
COMMENT ON COLUMN cti_link_enterprise_setting.value IS 'name-value对';
COMMENT ON COLUMN cti_link_enterprise_setting.property IS '属性';
COMMENT ON COLUMN cti_link_enterprise_setting.create_time IS '记录创建时间';

-- Index: cti_link_enterprise_setting_enterprise_id_index

-- DROP INDEX cti_link_enterprise_setting_enterprise_id_index;

CREATE INDEX cti_link_enterprise_setting_enterprise_id_index
  ON cti_link_enterprise_setting
  USING btree
  (enterprise_id);


 -- 放到enterpriseSetting里面
 -- ib_call_limit integer DEFAULT 0,
 -- ob_call_limit integer default 0,
 -- 有多少enterpriseSetting放到enterpriseSetting文档中
 


-- Table: cti_link_restrict_tel

-- DROP TABLE cti_link_restrict_tel;

CREATE TABLE cti_link_restrict_tel
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  restrict_type integer DEFAULT 1, -- 1:黑名单 2:白名单 
  type integer DEFAULT 1, -- 黑名单类型 1:呼入 2:外呼
  tel character varying, -- 加入黑名单电话
  tel_type integer DEFAULT 1, -- 电话号码类型 1:单个电话手机不加0固话加区号例如13409876543/01059222999 2:地区例如010/0311 3:未知号码
  "comment" character varying, -- 描述
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_restrict_tel_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_restrict_tel_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_restrict_tel OWNER TO postgres;
COMMENT ON TABLE cti_link_restrict_tel IS '呼叫限制表';
COMMENT ON COLUMN cti_link_restrict_tel.id IS '流水号';
COMMENT ON COLUMN cti_link_restrict_tel.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_restrict_tel.restrict_type IS '呼叫限制类型 1:黑名单 2:白名单';
COMMENT ON COLUMN cti_link_restrict_tel.type IS '类型 1:呼入 2:外呼';
COMMENT ON COLUMN cti_link_restrict_tel.tel IS '黑白名单电话';
COMMENT ON COLUMN cti_link_restrict_tel.tel_type IS '电话号码类型 1:单个电话手机不加0固话加区号例如13409876543/01059222999 2:地区例如010/031 3:未知号码';
COMMENT ON COLUMN cti_link_restrict_tel."comment" IS '描述';
COMMENT ON COLUMN cti_link_restrict_tel.create_time IS '记录创建时间';

-- Index: cti_link_restrict_tel_enterprise_id_index

-- DROP INDEX cti_link_restrict_tel_enterprise_id_index;

CREATE INDEX cti_link_restrict_tel_enterprise_id_index
  ON cti_link_restrict_tel
  USING btree
  (enterprise_id);

-- Index: cti_link_restrict_tel_tel_index

-- DROP INDEX cti_link_restrict_tel_tel_index;

CREATE INDEX cti_link_restrict_tel_tel_index
  ON cti_link_restrict_tel
  USING btree
  (tel);

-- Table: cti_link_ivr_profile

-- DROP TABLE cti_link_ivr_profile;

CREATE TABLE cti_link_ivr_profile
(
  id serial NOT NULL, -- 流水号
  ivr_name character varying NOT NULL, -- IVR名称
  ivr_type character varying NOT NULL, -- IVR 类型/模板 1:IVR 2:彩铃
  ivr_description character varying, -- IVR描述
  enterprise_id integer NOT NULL, -- 企业号
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_ivr_profile_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_ivr_profile_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITHOUT OIDS;
ALTER TABLE cti_link_ivr_profile OWNER TO postgres;
COMMENT ON TABLE cti_link_ivr_profile IS 'IVR配置表';
COMMENT ON COLUMN cti_link_ivr_profile.id IS 'id标识';
COMMENT ON COLUMN cti_link_ivr_profile.ivr_name IS 'IVR名称';
COMMENT ON COLUMN cti_link_ivr_profile.ivr_type IS 'IVR 类型/模板 1:IVR 2:彩铃';
COMMENT ON COLUMN cti_link_ivr_profile.ivr_description IS 'IVR描述';
COMMENT ON COLUMN cti_link_ivr_profile.enterprise_id IS '企业号';
COMMENT ON COLUMN cti_link_ivr_profile.create_time IS '记录创建时间';

-- Index: cti_link_ivr_profile_enterprise_id_idex

-- DROP INDEX cti_link_ivr_profile_enterprise_id_idex;

CREATE INDEX cti_link_ivr_profile_enterprise_id_idex
  ON cti_link_ivr_profile
  USING btree
  (enterprise_id);

-- Table: cti_link_enterprise_ivr

-- DROP TABLE cti_link_enterprise_ivr;

CREATE TABLE cti_link_enterprise_ivr
(
  id serial NOT NULL, -- id标识
  ivr_id integer NOT NULL, -- 所属ivr_id
  enterprise_id integer NOT NULL, -- 企业id
  path character varying, -- 节点
  path_name character varying, -- 节点名称
  parent_id integer DEFAULT 0, -- 父节点id
  action integer, -- 动作类型
  property character varying, -- 属性值
  anchor character varying, -- 锚点属性值
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_ivr_pkey PRIMARY KEY (id ),
  CONSTRAINT cti_link_enterprise_ivr_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cti_link_enterprise_ivr_ivr_id_fkey FOREIGN KEY (ivr_id)
      REFERENCES cti_link_ivr_profile (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_enterprise_ivr
  OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_ivr
  IS 'IVR详表';
COMMENT ON COLUMN cti_link_enterprise_ivr.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_ivr.ivr_id IS '所属ivr_id';
COMMENT ON COLUMN cti_link_enterprise_ivr.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_ivr.path IS '节点';
COMMENT ON COLUMN cti_link_enterprise_ivr.path_name IS '节点名称';
COMMENT ON COLUMN cti_link_enterprise_ivr.parent_id IS '父节点id';
COMMENT ON COLUMN cti_link_enterprise_ivr.action IS '动作类型';
COMMENT ON COLUMN cti_link_enterprise_ivr.property IS '属性值';
COMMENT ON COLUMN cti_link_enterprise_ivr.anchor IS '锚点属性值';
COMMENT ON COLUMN cti_link_enterprise_ivr.create_time IS '记录创建时间';


-- Index: cti_link_enterprise_ivr_ivr_id_idex

-- DROP INDEX cti_link_enterprise_ivr_ivr_id_idex;

CREATE INDEX cti_link_enterprise_ivr_ivr_id_idex
  ON cti_link_enterprise_ivr
  USING btree
  (ivr_id );

-- Table: cti_link_enterprise_time

-- DROP TABLE cti_link_enterprise_time;

CREATE TABLE cti_link_enterprise_time
(
  id serial NOT NULL, -- id流水号
  enterprise_id integer NOT NULL, -- 企业ID
  name character varying NOT NULL, -- 时间条件名称
  type integer DEFAULT 1, -- 时间类型 1:按星期 2:按特殊日期
  from_day character varying, -- type=2时使用 企业特殊假日日期起始，格式2010-03-02。type=1时为空串
  to_day character varying, -- type=2时使用 企业特殊假日日期结束，格式2010-03-02。type=1时为空串
  day_of_week character varying,  -- type=1时使用 星期几 2:一 3:二 4:三 5:四 6:五 7:六 1:日 字符串分隔 例如:1,2,3,4,5。type=2时为空
  start_time character varying NOT NULL, -- 开始时间 格式09:00
  end_time character varying NOT NULL, -- 结束时间 格式18:00
  priority integer, -- 优先级 从1开始，数字越小优先级越高
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_time_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_time_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_time OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_time IS '时间节点配置表';
COMMENT ON COLUMN cti_link_enterprise_time.id IS 'id流水号';
COMMENT ON COLUMN cti_link_enterprise_time.enterprise_id IS '企业ID';
COMMENT ON COLUMN cti_link_enterprise_time.name IS '时间条件名称';
COMMENT ON COLUMN cti_link_enterprise_time.type IS '时间类型 1:按星期 2:按特殊日期';
COMMENT ON COLUMN cti_link_enterprise_time.from_day IS 'type=2时使用 企业特殊假日日期起始，格式2010-03-02，type=1时为空串';
COMMENT ON COLUMN cti_link_enterprise_time.to_day IS 'type=2时使用 企业特殊假日日期结束，格式2010-03-02，type=1时为空串';
COMMENT ON COLUMN cti_link_enterprise_time.day_of_week IS 'type=1时使用 星期几 2:一 3:二 4:三 5:四 6:五 7:六 1:日 字符串分隔 例如:1,2,3,4,5。type=2时为空';
COMMENT ON COLUMN cti_link_enterprise_time.start_time IS '开始时间 格式09:00';
COMMENT ON COLUMN cti_link_enterprise_time.end_time IS '结束时间 格式18:00';
COMMENT ON COLUMN cti_link_enterprise_time.priority IS '优先级 从1开始，数字越小优先级越高';
COMMENT ON COLUMN cti_link_enterprise_time.create_time IS '记录创建时间';

CREATE INDEX cti_link_enterprise_time_idex
  ON cti_link_enterprise_time
  USING btree
  (id,day_of_week,start_time,end_time);

-- Table: cti_link_enterprise_area_group

-- DROP TABLE cti_link_enterprise_area_group;

CREATE TABLE cti_link_enterprise_area_group
(
  id serial NOT NULL, -- 流水号
  enterprise_id integer NOT NULL, -- 商家编号
  group_name character varying, -- 地区名称
  group_type integer DEFAULT 1,
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_area_group_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_area_group_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_area_group OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_area_group IS '企业地区路由表';
COMMENT ON COLUMN cti_link_enterprise_area_group.id IS '流水号';
COMMENT ON COLUMN cti_link_enterprise_area_group.enterprise_id IS '企业ID';
COMMENT ON COLUMN cti_link_enterprise_area_group.group_type IS '地区组类型 1:地区组 2:其他地区';
COMMENT ON COLUMN cti_link_enterprise_area_group.group_name IS 'area_group_name';
COMMENT ON COLUMN cti_link_enterprise_area_group.create_time IS '记录创建时间';

-- Table: cti_link_enterprise_area

-- DROP TABLE cti_link_enterprise_area;

CREATE TABLE cti_link_enterprise_area
(
  id serial NOT NULL, -- 流水号
  enterprise_id integer NOT NULL, -- 商家编号
  group_id integer NOT NULL,
  area_code character varying, -- 区号
  province character varying, -- 省份
  city character varying, -- 城市
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_area_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_area_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cti_link_enterprise_area_group_id_fkey FOREIGN KEY (group_id)
    REFERENCES cti_link_enterprise_area_group (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_area OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_area IS '地区组';
COMMENT ON COLUMN cti_link_enterprise_area.id IS '流水号';
COMMENT ON COLUMN cti_link_enterprise_area.enterprise_id IS '企业ID';
COMMENT ON COLUMN cti_link_enterprise_area.group_id IS '对应cti_link_enterprise_area_group->id';
COMMENT ON COLUMN cti_link_enterprise_area.area_code IS '地区区号 例如010 空通配全部';
COMMENT ON COLUMN cti_link_enterprise_area.province IS '冗余数据 省';
COMMENT ON COLUMN cti_link_enterprise_area.city IS '冗余数据 市';
COMMENT ON COLUMN cti_link_enterprise_area.create_time IS '记录创建时间';

CREATE INDEX cti_link_enterprise_area_idex
  ON cti_link_enterprise_area
  USING btree
  (id,enterprise_id,area_code);

-- Table: cti_link_enterprise_voice

-- DROP TABLE cti_link_enterprise_voice;

CREATE TABLE cti_link_enterprise_voice
(
  id serial NOT NULL, --流水号
  enterprise_id integer NOT NULL, -- 企业号
  voice_name character varying, --企业语音文件名
  path character varying, --企业语音文件路径
  description character varying, -- 描述
  audit_status integer NOT NULL default 3, -- 审核状态：1--未审核（上传完时的状态），2--审核中（客服点击审核），3--审核通过，4--审核失败
  audit_comment character varying,
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  expired_hour character varying, -- 上传录音文件保留时长 单位为小时
  CONSTRAINT cti_link_enterprise_voice_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_voice_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
) 
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_voice OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_voice IS '企业语音库';
COMMENT ON COLUMN cti_link_enterprise_voice.id IS '流水号';
COMMENT ON COLUMN cti_link_enterprise_voice.enterprise_id IS '企业号';
COMMENT ON COLUMN cti_link_enterprise_voice.voice_name IS '企业语音文件名';
COMMENT ON COLUMN cti_link_enterprise_voice.path IS '企业语音文件路径';
COMMENT ON COLUMN cti_link_enterprise_voice.description IS '描述';
COMMENT ON COLUMN cti_link_enterprise_voice.audit_status IS '审核状态：1--未审核（上传完时的状态），2--审核中（客服点击审核），3--审核通过，4--审核失败';
COMMENT ON COLUMN cti_link_enterprise_voice.audit_comment IS '审核失败原因';
COMMENT ON COLUMN cti_link_enterprise_voice.create_time IS '记录创建时间';
COMMENT ON COLUMN cti_link_enterprise_voice.expired_hour IS '上传录音文件保留时长 单位为小时';

-- Table: cti_link_enterprise_moh

-- DROP TABLE cti_link_enterprise_moh;

CREATE TABLE cti_link_enterprise_moh
(
  id serial NOT NULL, -- 流水号
  enterprise_id integer NOT NULL,
  name character varying NOT NULL, -- 类名 格式：企业号+类名
  directory character varying NOT NULL, -- 音频文件的路径
  application character varying NOT NULL default '',
  mode character varying NOT NULL default '',
  digit character varying NOT NULL default '',
  sort character varying NOT NULL default '',
  format character varying NOT NULL default '',
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_moh_id PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_moh_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_moh OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_moh IS '保存music_on_hold文件路径信息';
COMMENT ON COLUMN cti_link_enterprise_moh.id IS 'id号';
COMMENT ON COLUMN cti_link_enterprise_moh.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_moh.name IS '类名 格式：企业号+类名';
COMMENT ON COLUMN cti_link_enterprise_moh.directory IS '音频文件的路径';
COMMENT ON COLUMN cti_link_enterprise_moh.application IS '应用程序';
COMMENT ON COLUMN cti_link_enterprise_moh.mode IS '模式';
COMMENT ON COLUMN cti_link_enterprise_moh.digit IS '按键';
COMMENT ON COLUMN cti_link_enterprise_moh.sort IS '排序方式';
COMMENT ON COLUMN cti_link_enterprise_moh.format IS '格式';
COMMENT ON COLUMN cti_link_enterprise_moh.create_time IS '记录创建时间';

-- Index: cti_link_enterprise_moh_name_idex

-- DROP INDEX cti_link_enterprise_moh_name_idex;

CREATE INDEX cti_link_enterprise_moh_name_idex
  ON cti_link_enterprise_moh
  USING btree
  (name);

-- Table: cti_link_enterprise_moh_voice

-- DROP TABLE cti_link_enterprise_moh_voice;

CREATE TABLE cti_link_enterprise_moh_voice
(
  id serial NOT NULL, 
  enterprise_id integer NOT NULL,
  moh_id integer NOT NULL,
  voice_id integer NOT NULL,
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_moh_voice_id PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_moh_voice_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE,
  CONSTRAINT cti_link_enterprise_moh_voice_moh_id_fkey FOREIGN KEY (moh_id)
    REFERENCES cti_link_enterprise_moh (id) MATCH SIMPLE,
  CONSTRAINT cti_link_enterprise_moh_voice_voice_id_fkey FOREIGN KEY (voice_id)
    REFERENCES cti_link_enterprise_voice (id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_moh_voice OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_moh_voice IS 'music_on_hold中语音文件';
COMMENT ON COLUMN cti_link_enterprise_moh_voice.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_moh_voice.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_moh_voice.moh_id IS 'moh类id';
COMMENT ON COLUMN cti_link_enterprise_moh_voice.voice_id IS '语音文件id';
COMMENT ON COLUMN cti_link_enterprise_moh_voice.create_time IS '记录创建时间';

-- Table: cti_link_enterprise_ivr_router

-- DROP TABLE cti_link_enterprise_ivr_router;

CREATE TABLE cti_link_enterprise_ivr_router
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  active integer DEFAULT 1, -- 路由是否激活 0:禁用 1:启用
  router_type integer, -- 路由目的类型 1:IVR 2:固定号码
  router_property character varying, -- 路由目的详细配置
  description character varying, -- 路由说明
  priority integer, -- 优先级
  rule_time_property character varying DEFAULT ''::character varying, -- 时间规则配置
  rule_area_property character varying DEFAULT ''::character varying, -- 来电地区规则配置
  rule_trunk_property character varying DEFAULT ''::character varying, -- 中继号码规则配置
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_ivr_router_pkey PRIMARY KEY (id ),
  CONSTRAINT cti_link_enterprise_ivr_router_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cti_link_enterprise_ivr_routerr_priority_unique UNIQUE (enterprise_id , priority )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_enterprise_ivr_router
  OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_ivr_router
  IS 'IVR路由配置表';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.active IS '路由是否激活 0:禁用 1:启用';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.router_type IS '路由目的类型 1:IVR 2:固定号码';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.router_property IS '路由目的详细配置';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.description IS '路由说明';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.priority IS '优先级';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.rule_time_property IS '时间规则配置';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.rule_area_property IS '来电地区规则配置';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.rule_trunk_property IS '中继号码规则配置';
COMMENT ON COLUMN cti_link_enterprise_ivr_router.create_time IS '记录创建时间';


-- Table: cti_link_enterprise_voicemail

-- DROP TABLE cti_link_enterprise_voicemail;

CREATE TABLE cti_link_enterprise_voicemail
(
  id serial NOT NULL, 
  enterprise_id integer NOT NULL,
  name character varying,
  vno character varying,
  type integer DEFAULT 1, 
  create_time timestamp with time zone DEFAULT now(), 
  CONSTRAINT cti_link_enterprise_voicemail_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_enterprise_voicemail_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
)
WITHOUT OIDS;
ALTER TABLE cti_link_enterprise_voicemail OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_voicemail IS '留言箱表';
COMMENT ON COLUMN cti_link_enterprise_voicemail.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_voicemail.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_voicemail.name IS '留言箱名称';
COMMENT ON COLUMN cti_link_enterprise_voicemail.vno IS '留言箱号 3-5位';
COMMENT ON COLUMN cti_link_enterprise_voicemail.type IS '留言箱类型 1:公共留言箱 2:队列私有留言箱 3:座席私有留言箱 ';
COMMENT ON COLUMN cti_link_enterprise_voicemail.create_time IS '记录创建时间';


-- Table: cti_link_enterprise_hangup_action

-- DROP TABLE cti_link_enterprise_hangup_action;

CREATE TABLE cti_link_enterprise_hangup_action
(
  id serial NOT NULL, -- 流水号
  enterprise_id integer, -- 企业id
  url character varying, -- 挂机推送地址 格式http://a.b.com/interface/a.jsp
  param_name character varying, -- 推送参数 多个参数以逗号分隔 status,node
  param_variable character varying, -- 推送参数对应底层变量
  timeout integer default 3, -- 超时时长 秒默认3
  retry integer default 1, -- 重试次数 默认1
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  type integer NOT NULL DEFAULT 1, -- 1.挂机推送2.外呼挂机推送3.外呼接通推送4.来电推送5.座席状态推送
  interval_time integer DEFAULT 0, -- 间隔时间
  method integer DEFAULT 0, -- 推送方式  0 post  1 get
  CONSTRAINT cti_link_enterprise_hangup_action_pkey PRIMARY KEY (id )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_enterprise_hangup_action
  OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_hangup_action
  IS '企业挂机推送设置,企业可以有多个推送';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.id IS '流水号';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.url IS '座席工号';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.param_name IS '平均响铃时长';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.param_variable IS '平均通话时长';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.timeout IS '超时时长 秒默认3';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.retry IS '重试次数 默认1';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.create_time IS '记录创建时间';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.type IS '1.呼入来电推送2.呼入呼转响铃推送3.呼入呼转接通推送4.呼入挂机推送5.外呼响铃推送6.外呼呼转响铃推送7.外呼接通推送8.外呼挂机推送9.按键推送';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.interval_time IS '间隔时间';
COMMENT ON COLUMN cti_link_enterprise_hangup_action.method IS '推送方式  0 post  1 get';

-- Table: cti_link_enterprise_hangup_set 

-- DROP TABLE cti_link_enterprise_hangup_set; 

CREATE TABLE cti_link_enterprise_hangup_set 
( 
  id serial NOT NULL, 
  enterprise_id integer NOT NULL, -- 企业ID 
  type integer, -- 类型 0:呼入 1:外呼
  variable_name character varying, -- 变量名称 
  variable_value character varying, -- 变量值
  variable_value_type integer default 0, -- 变量类型 0:表达式 1:字符串 
  sort integer, -- 排序序号从1开始
  create_time timestamp with time zone NOT NULL DEFAULT now(), -- 创建时间 
CONSTRAINT cti_link_enterprise_hangup_set_pkey PRIMARY KEY (id) 
) 
WITH ( 
  OIDS=FALSE 
); 
ALTER TABLE cti_link_enterprise_hangup_set 
OWNER TO postgres; 
COMMENT ON TABLE cti_link_enterprise_hangup_set 
IS '挂机设置变量配置表'; 
COMMENT ON COLUMN cti_link_enterprise_hangup_set.enterprise_id IS '企业ID'; 
COMMENT ON COLUMN cti_link_enterprise_hangup_set.type IS '类型 0:呼入 1:外呼';
COMMENT ON COLUMN cti_link_enterprise_hangup_set.variable_name IS '变量名称'; 
COMMENT ON COLUMN cti_link_enterprise_hangup_set.variable_value IS '变量值'; 
COMMENT ON COLUMN cti_link_enterprise_hangup_set.variable_value_type IS '变量类型 0:表达式 1:字符串 '; 
COMMENT ON COLUMN cti_link_enterprise_hangup_set.sort IS '排序序号从1开始';
COMMENT ON COLUMN cti_link_enterprise_hangup_set.create_time IS '创建时间'; 

-- Table: cti_link_tel_set

-- DROP TABLE cti_link_tel_set;

CREATE TABLE cti_link_tel_set
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  set_name character varying NOT NULL, -- 组名称
  tsno character varying NOT NULL, -- 组号
  strategy character varying DEFAULT 'order'::character varying, -- 组呼叫策略, order:顺序,random:随机
  timeout integer DEFAULT 300, -- 组超时时间,秒数,默认300
  is_stop integer default 0,--是否停用状态标记
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  modify_time timestamp with time zone DEFAULT now(), --记录修改时间
  CONSTRAINT cti_link_tel_set_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_tel_set_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_tel_set
  OWNER TO postgres;
COMMENT ON TABLE cti_link_tel_set
  IS '座席对应电话表';
COMMENT ON COLUMN cti_link_tel_set.id IS 'id标识';
COMMENT ON COLUMN cti_link_tel_set.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_tel_set.set_name IS '组名称';
COMMENT ON COLUMN cti_link_tel_set.tsno IS '组号';
COMMENT ON COLUMN cti_link_tel_set.strategy IS '组呼叫策略, order:顺序,random:随机';
COMMENT ON COLUMN cti_link_tel_set.timeout IS '组超时时间,秒数,默认300';
COMMENT ON COLUMN cti_link_tel_set.create_time IS '记录创建时间';
COMMENT ON COLUMN cti_link_tel_set.modify_time IS '记录修改时间';
COMMENT ON COLUMN cti_link_tel_set.is_stop IS '是否是停用状态 1:停用0:不停用';

CREATE INDEX cti_link_tel_set_id_index ON cti_link_tel_set USING btree (id);
CREATE INDEX cti_link_tel_set_tsno_index ON cti_link_tel_set USING btree (tsno);
CREATE INDEX cti_link_tel_set_enterprise_id_index ON  cti_link_tel_set USING btree (enterprise_id);


-- Table: cti_link_tel_set_tel

-- DROP TABLE cti_link_tel_set_tel;

CREATE TABLE cti_link_tel_set_tel
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  set_id integer NOT NULL, -- 电话所属组id
  tsno character varying NOT NULL, -- 电话所属组号
  tel_name character varying NOT NULL, -- 电话名称
  tel character varying NOT NULL, -- 电话
  tel_type integer DEFAULT 1, -- 电话类型 1:固话 2:手机
  area_code character varying, -- 电话区号
  timeout integer DEFAULT 30, -- 超时时间
  priority integer NOT NULL DEFAULT 1, -- 优先级,数字越小越优先
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_tel_set_tel_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_tel_set_tel_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_tel_set_tel
  OWNER TO postgres;
COMMENT ON TABLE cti_link_tel_set_tel
  IS '电话组对应电话表';
COMMENT ON COLUMN cti_link_tel_set_tel.id IS 'id标识';
COMMENT ON COLUMN cti_link_tel_set_tel.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_tel_set_tel.set_id IS '电话所属组id';
COMMENT ON COLUMN cti_link_tel_set_tel.tsno IS '电话所属组号';
COMMENT ON COLUMN cti_link_tel_set_tel.tel_name IS '电话名称';
COMMENT ON COLUMN cti_link_tel_set_tel.tel IS '电话';
COMMENT ON COLUMN cti_link_tel_set_tel.tel_type IS '电话类型 1:固话 2:手机';
COMMENT ON COLUMN cti_link_tel_set_tel.area_code IS '电话区号';
COMMENT ON COLUMN cti_link_tel_set_tel.timeout IS '超时时间';
COMMENT ON COLUMN cti_link_tel_set_tel.priority IS '优先级,数字越小越优先';
COMMENT ON COLUMN cti_link_tel_set_tel.create_time IS '记录创建时间';

-- Index: cti_link_tel_set_tel_set_id_index

-- DROP INDEX cti_link_tel_set_tel_set_id_index;

CREATE INDEX cti_link_tel_set_tel_set_id_index
  ON cti_link_tel_set_tel
  USING btree
  (set_id);

-- Index: cti_link_tel_set_tel_tsno_index

-- DROP INDEX cti_link_tel_set_tel_tsno_index;

CREATE INDEX cti_link_tel_set_tel_tsno_index
  ON cti_link_tel_set_tel
  USING btree
  (tsno COLLATE pg_catalog."default");


-- Table: cti_link_queue

-- DROP TABLE cti_link_queue;

CREATE TABLE cti_link_queue
(
  id serial NOT NULL,
  enterprise_id integer NOT NULL, -- 企业ID 
  qno character varying NOT NULL,
  description character varying,
  music_class character varying DEFAULT 'default'::character varying,
  queue_timeout integer DEFAULT 600, -- 最大等待时间
  say_agentno boolean DEFAULT false, -- 语音报号
  member_timeout integer DEFAULT 15, -- 超时时间
  retry integer DEFAULT 1,  -- 座席超时无应答,呼叫下一座席的延迟秒数
  wrapup_time integer DEFAULT 15, -- 整理时间
  max_len integer DEFAULT 5, -- 最大等待数
  strategy character varying DEFAULT 'rrmemory'::character varying,
  service_level integer,
  weight integer DEFAULT 0,
  vip_support integer DEFAULT 0,
  join_empty integer DEFAULT 0,
  announce_sound integer DEFAULT 0, -- 播报固定语音 0关闭 1打开
  announce_sound_frequency integer DEFAULT 0, -- 播报固定语音周期
  announce_sound_file character varying, -- 固定语音文件
  announce_position integer DEFAULT 0, -- 位置播报 0关闭 1大于announce_position_param时播放 2小于等于announce_position_param时播放
  announce_position_youarenext integer DEFAULT 0, -- 位置报告时播报下一位 0关闭 1打开
  announce_position_frequency integer DEFAULT 0, -- 位置播报周期
  announce_position_param integer DEFAULT 0, -- 多余少余n个时播报，0表示不起作用
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_queue_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_queue_name_unique UNIQUE (qno),
  CONSTRAINT cti_link_queue_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITHOUT OIDS;
ALTER TABLE cti_link_queue OWNER TO postgres;
COMMENT ON TABLE cti_link_queue IS ' 队列参数表';
COMMENT ON COLUMN cti_link_queue.id IS 'id标识';
COMMENT ON COLUMN cti_link_queue.enterprise_id IS '企业ID'; 
COMMENT ON COLUMN cti_link_queue.description IS '队列描述：客服队列/投诉队列';
COMMENT ON COLUMN cti_link_queue.qno IS '队列号 默认四位数字支持3-5位';
COMMENT ON COLUMN cti_link_queue.music_class IS '等待音乐class';
COMMENT ON COLUMN cti_link_queue.queue_timeout IS '队列超时时间';
COMMENT ON COLUMN cti_link_queue.say_agentno IS '语音报号';
COMMENT ON COLUMN cti_link_queue.member_timeout IS '坐席超时时间';
COMMENT ON COLUMN cti_link_queue.retry IS '座席超时无应答,呼叫下一座席的延迟秒数'; 
COMMENT ON COLUMN cti_link_queue.wrapup_time IS '整理时间';
COMMENT ON COLUMN cti_link_queue.max_len IS '最大等待数';
COMMENT ON COLUMN cti_link_queue.strategy IS '呼叫策略';
COMMENT ON COLUMN cti_link_queue.service_level IS '服务水平秒数，低于此时间内接听的认为是高服务水平';
COMMENT ON COLUMN cti_link_queue.weight IS '队列优先级';
COMMENT ON COLUMN cti_link_queue.vip_support IS '队列是否支持vip级别 0:不支持 1:支持';
COMMENT ON COLUMN cti_link_queue.join_empty IS '队列中为空时是否可以join';
COMMENT ON COLUMN cti_link_queue.announce_sound IS '播报固定语音 0关闭 1打开';
COMMENT ON COLUMN cti_link_queue.announce_sound_frequency IS '播报固定语音周期';
COMMENT ON COLUMN cti_link_queue.announce_sound_file IS '固定语音文件';
COMMENT ON COLUMN cti_link_queue.announce_position IS '位置播报 0关闭 1大于announce_position_param时播放 2小于等于announce_position_param时播放';
COMMENT ON COLUMN cti_link_queue.announce_position_youarenext IS '位置报告时播报下一位 0关闭 1打开';
COMMENT ON COLUMN cti_link_queue.announce_position_frequency IS '位置播报周期';
COMMENT ON COLUMN cti_link_queue.announce_position_param IS '多余/少余n个时播报，0表示不起作用';
COMMENT ON COLUMN cti_link_queue.create_time IS '记录创建时间';

-- Table: cti_link_agent

-- DROP TABLE cti_link_agent;

CREATE TABLE cti_link_agent
(
  id serial NOT NULL, -- 流水号
  enterprise_id integer NOT NULL, -- 企业编号
  cno character varying, -- 座席号 设计可以支持4-8位
  crm_id character varying, -- 座席crm id
  active integer DEFAULT 1, -- 座席是否激活 非激活状态座席不能登录，不能加入队列
  wrapup integer NOT NULL DEFAULT 10, -- 座席整理时间 呼入与外呼整理时间使用一个字段
  area_code character varying, -- 绑定电话所属区号
  name character varying, -- 座席名称
  call_power integer DEFAULT 0, -- 呼叫权限 0:不限制 1:限制国际(只能拨国内) 2:限制长途(只能拨本地) 3:限制本地(只能拨分机)
  agent_type integer DEFAULT 1, -- 1为电话座席 2为电脑座席
  is_ob integer DEFAULT 1, -- 是否可以外呼，0:不允许，1：允许
  ib_record integer DEFAULT 1, -- 呼入是否录音，0:不录音，1：录音
  ob_record integer DEFAULT 1, -- 外呼是否录，0:不录音，1：录音
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_agent_pkey PRIMARY KEY (id ),
  CONSTRAINT cti_link_agent_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_agent
  OWNER TO postgres;
COMMENT ON TABLE cti_link_agent
  IS '企业电话线路表(座席表)';
COMMENT ON COLUMN cti_link_agent.id IS '流水号';
COMMENT ON COLUMN cti_link_agent.enterprise_id IS '企业编号';
COMMENT ON COLUMN cti_link_agent.cno IS '座席号 设计可以支持4-8位';
COMMENT ON COLUMN cti_link_agent.crm_id IS '座席crm id';
COMMENT ON COLUMN cti_link_agent.active IS '座席是否激活 非激活状态座席不能登录，不能加入队列';
COMMENT ON COLUMN cti_link_agent.wrapup IS '座席整理时间 呼入与外呼整理时间使用一个字段';
COMMENT ON COLUMN cti_link_agent.area_code IS '绑定电话所属区号';
COMMENT ON COLUMN cti_link_agent.name IS '座席名称';
COMMENT ON COLUMN cti_link_agent.call_power IS '呼叫权限 0:不限制 1:限制国际(只能拨国内) 2:限制长途(只能拨本地) 3:限制本地(只能拨分机)';
COMMENT ON COLUMN cti_link_agent.agent_type IS '1为电话座席 2为电脑座席';
COMMENT ON COLUMN cti_link_agent.is_ob IS '是否可以外呼，0:不允许，1：允许';
COMMENT ON COLUMN cti_link_agent.ib_record IS '呼入是否录音，0:不录音，1：录音';
COMMENT ON COLUMN cti_link_agent.ob_record IS '外呼是否录，0:不录音，1：录音';
COMMENT ON COLUMN cti_link_agent.create_time IS '记录创建时间';

create index cti_link_agent_enterprise_id_index on cti_link_agent using btree(enterprise_id);

create index cti_link_agent_enterprise_id_cno_index on cti_link_agent using btree(enterprise_id,cno);

-- Table: cti_link_agent_tel

-- DROP TABLE cti_link_agent_tel;

CREATE TABLE cti_link_agent_tel
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  agent_id integer NOT NULL, -- 座席id
  tel character varying, -- 电话号码 软电话时为软电话分机号
  tel_type integer DEFAULT 1, -- 电话类型 1:固话 2:手机 3:分机 4:软电话
  area_code character varying, -- 电话区号
  is_validity integer DEFAULT 0, -- 是否已验证 0:未验证 1:已验证
  is_bind integer default 0, -- 是否正在绑定 0:未使用 1:正在使用
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_agent_tel_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_agent_tel_agent_id_fkey FOREIGN KEY (agent_id)
    REFERENCES cti_link_agent (id) MATCH SIMPLE,
  CONSTRAINT cti_link_agent_tel_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_agent_tel OWNER TO postgres;
COMMENT ON TABLE cti_link_agent_tel IS '座席对应电话表';
COMMENT ON COLUMN cti_link_agent_tel.id IS 'id标识';
COMMENT ON COLUMN cti_link_agent_tel.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_agent_tel.agent_id IS '座席id';
COMMENT ON COLUMN cti_link_agent_tel.tel IS '电话号码';
COMMENT ON COLUMN cti_link_agent_tel.tel_type IS '电话类型 1:固话 2:手机 3:分机 4:软电话';
COMMENT ON COLUMN cti_link_agent_tel.area_code IS '电话区号';
COMMENT ON COLUMN cti_link_agent_tel.is_bind IS '是否正在绑定 0:未使用 1:正在使用';
COMMENT ON COLUMN cti_link_agent_tel.create_time IS '记录创建时间';

-- Table: cti_link_agent_crontab

-- DROP TABLE cti_link_agent_crontab;

CREATE TABLE cti_link_agent_crontab
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  agent_id integer NOT NULL, -- 座席id
  cron_type integer NOT NULL, -- 定时任务类型 1:自动上线 2:自动下线 3:自动更换绑定电话
  day_of_week character varying, -- 定时任务星期 例如 1,2,3,4,5周一到周五
  start_time character varying NOT NULL, -- 开始start_time 例如:08:00
  agent_tel_id integer NOT NULL, -- agent_tel id
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_agent_crontab_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_agent_crontab_agent_id_fkey FOREIGN KEY (agent_id)
    REFERENCES cti_link_agent (id) MATCH SIMPLE,
  CONSTRAINT cti_link_agent_crontab_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_agent_crontab OWNER TO postgres;
COMMENT ON TABLE cti_link_agent_crontab IS '座席对应电话表';
COMMENT ON COLUMN cti_link_agent_crontab.id IS 'id标识';
COMMENT ON COLUMN cti_link_agent_crontab.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_agent_crontab.agent_id IS '座席id';
COMMENT ON COLUMN cti_link_agent_crontab.cron_type IS '电话号码';
COMMENT ON COLUMN cti_link_agent_crontab.day_of_week IS '定时任务星期 例如 1,2,3,4,5周一到周五';
COMMENT ON COLUMN cti_link_agent_crontab.start_time IS '开始start_time 例如:08:00';
COMMENT ON COLUMN cti_link_agent_crontab.agent_tel_id IS 'cti_link_agent_tel id';
COMMENT ON COLUMN cti_link_agent_crontab.create_time IS '记录创建时间';


-- Table: cti_link_queue_member

-- DROP TABLE cti_link_queue_member;

CREATE TABLE cti_link_queue_member
(
  id serial NOT NULL,
  enterprise_id integer NOT NULL, -- 企业id
  queue_id integer NOT NULL,
  qno character varying,
  interface character varying NOT NULL,
  penalty integer,
  tel character varying, -- 原始电话号码
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  agent_id integer NOT NULL,
  CONSTRAINT cti_link_queue_member_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_queue_member_agent_id_fkey FOREIGN KEY (agent_id)
    REFERENCES cti_link_agent (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cti_link_queue_member_queue_id_fkey FOREIGN KEY (queue_id)
    REFERENCES cti_link_queue (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITHOUT OIDS;
ALTER TABLE cti_link_queue_member OWNER TO postgres;
COMMENT ON TABLE cti_link_queue_member IS '服务队列登记表（当座席登陆时，应该要主动登记服务队列中）';
COMMENT ON COLUMN cti_link_queue_member.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_queue_member.queue_id IS '对应cti_link_queue->id';
COMMENT ON COLUMN cti_link_queue_member.interface IS '比如:SIP/1114103159@10010012000';
COMMENT ON COLUMN cti_link_queue_member.penalty IS '优先级 按照技能组，如果有多个技能组将技能相加';
COMMENT ON COLUMN cti_link_queue_member.tel IS '原始电话号码 比如:4003159';
COMMENT ON COLUMN cti_link_queue_member.create_time IS '记录创建时间';
COMMENT ON COLUMN cti_link_queue_member.agent_id IS '对应cti_link_agent->agent_id';

-- Index: cti_link_queue_member_interface_index

-- DROP INDEX cti_link_queue_member_interface_index;

CREATE INDEX cti_link_queue_member_interface_index
  ON cti_link_queue_member
  USING btree
  (interface COLLATE pg_catalog."default");

  CREATE INDEX cti_link_queue_member_agent_id_index
  ON cti_link_queue_member
  USING btree
  (agent_id);

-- Table: cti_link_skill

-- DROP TABLE cti_link_skill;

CREATE TABLE cti_link_skill
(
  id serial NOT NULL, 
  enterprise_id integer NOT NULL,
  name character varying, 
  "comment" character varying, 
  create_time timestamp with time zone DEFAULT now(),
  CONSTRAINT cti_link_skill_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_skill_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_skill OWNER TO postgres;
COMMENT ON TABLE cti_link_skill IS '企业技能组';
COMMENT ON COLUMN cti_link_skill.id IS 'id标识';
COMMENT ON COLUMN cti_link_skill.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_skill.name IS '技能组名称';
COMMENT ON COLUMN cti_link_skill."comment" IS '描述';
COMMENT ON COLUMN cti_link_skill.create_time IS '记录创建时间';

-- Table: cti_link_queue_skill

-- DROP TABLE cti_link_queue_skill;

CREATE TABLE cti_link_queue_skill
(
  id serial NOT NULL, 
  enterprise_id integer NOT NULL,
  queue_id integer NOT NULL,
  skill_id integer NOT NULL,
  skill_level integer NOT NULL,
  create_time timestamp with time zone DEFAULT now(),
  CONSTRAINT cti_link_queue_skill_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_queue_skill_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE,
  CONSTRAINT cti_link_queue_skill_queue_id_fkey FOREIGN KEY (queue_id)
    REFERENCES cti_link_queue (id) MATCH SIMPLE,
  CONSTRAINT cti_link_queue_skill_skill_id_fkey FOREIGN KEY (skill_id)
    REFERENCES cti_link_skill (id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_queue_skill OWNER TO postgres;
COMMENT ON TABLE cti_link_queue_skill IS '企业队列中包含的电话转接到技能组关系表';
COMMENT ON COLUMN cti_link_queue_skill.id IS 'id标识';
COMMENT ON COLUMN cti_link_queue_skill.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_queue_skill.queue_id IS '队列id';
COMMENT ON COLUMN cti_link_queue_skill.skill_id IS '技能id';
COMMENT ON COLUMN cti_link_queue_skill.skill_level IS '技能最小值';
COMMENT ON COLUMN cti_link_queue_skill.create_time IS '记录创建时间';

-- Table: cti_link_agent_skill

-- DROP TABLE cti_link_agent_skill;

CREATE TABLE cti_link_agent_skill
(
  id serial NOT NULL, 
  enterprise_id integer NOT NULL,
  agent_id integer NOT NULL,
  skill_id integer NOT NULL,
  skill_level integer NOT NULL,
  create_time timestamp with time zone DEFAULT now(),
  CONSTRAINT cti_link_agent_skill_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_agent_skill_enterprise_id_fkey FOREIGN KEY (enterprise_id)
    REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE,
  CONSTRAINT cti_link_agent_skill_agent_id_fkey FOREIGN KEY (agent_id)
    REFERENCES cti_link_agent (id) MATCH SIMPLE,
  CONSTRAINT cti_link_agent_skill_skill_id_fkey FOREIGN KEY (skill_id)
    REFERENCES cti_link_skill (id) MATCH SIMPLE
) 
WITHOUT OIDS;
ALTER TABLE cti_link_agent_skill OWNER TO postgres;
COMMENT ON TABLE cti_link_agent_skill IS '企业座席拥有的技能关系表';
COMMENT ON COLUMN cti_link_agent_skill.id IS 'id标识';
COMMENT ON COLUMN cti_link_agent_skill.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_agent_skill.agent_id IS '队列id';
COMMENT ON COLUMN cti_link_agent_skill.skill_id IS '技能id';
COMMENT ON COLUMN cti_link_agent_skill.skill_level IS '技能等级 1-5从高到低';
COMMENT ON COLUMN cti_link_agent_skill.create_time IS '记录创建时间';

CREATE INDEX cti_link_agent_skill_enterprise_id_index
  ON cti_link_agent_skill
  USING btree
  (enterprise_id);


-- Table: cti_link_enterprise_investigation

-- DROP TABLE cti_link_enterprise_investigation;

CREATE TABLE cti_link_enterprise_investigation
(
  id serial NOT NULL, -- id标识
  enterprise_id integer NOT NULL, -- 企业id
  path character varying, -- 节点
  path_name character varying, -- 节点名称
  action integer, -- 动作 只能是选择和播放
  property character varying, -- 属性值
  anchor character varying, -- 锚点属性值
  parent_id integer DEFAULT 0, -- 父节点id
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  CONSTRAINT cti_link_enterprise_investigation_pkey PRIMARY KEY (id ),
  CONSTRAINT cti_link_enterprise_investigation_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_enterprise_investigation
  OWNER TO postgres;
COMMENT ON TABLE cti_link_enterprise_investigation
  IS '满意度调查设置表';
COMMENT ON COLUMN cti_link_enterprise_investigation.id IS 'id标识';
COMMENT ON COLUMN cti_link_enterprise_investigation.enterprise_id IS '企业id';
COMMENT ON COLUMN cti_link_enterprise_investigation.path IS '节点';
COMMENT ON COLUMN cti_link_enterprise_investigation.path_name IS '节点名称';
COMMENT ON COLUMN cti_link_enterprise_investigation.action IS '动作 只能是选择和播放';
COMMENT ON COLUMN cti_link_enterprise_investigation.property IS '属性值';
COMMENT ON COLUMN cti_link_enterprise_investigation.anchor IS '锚点属性值';
COMMENT ON COLUMN cti_link_enterprise_investigation.parent_id IS '父节点id ';
COMMENT ON COLUMN cti_link_enterprise_investigation.create_time IS '记录创建时间';



-- Table: cti_link_order_call_back

-- DROP TABLE cti_link_order_call_back;

CREATE TABLE cti_link_order_call_back
(
  id serial NOT NULL, -- 主键
  enterprise_id integer NOT NULL, -- 企业ID
  main_unique_id character varying,
  qno character varying NOT NULL, -- 队列号
  tel character varying, -- 主叫号码
  is_call integer, -- 是否外呼  0 还没呼叫  1  已呼叫
  order_time timestamp with time zone DEFAULT now(), -- 预约时间
  create_time timestamp with time zone DEFAULT now(), -- 记录创建时间
  cno character varying, -- 执行此外呼的坐席
  call_back_time timestamp with time zone, -- 坐席回呼时间
  queue_name character varying, -- 队列名称
  area character varying, -- 地区
  CONSTRAINT cti_link_order_call_back_pkey PRIMARY KEY (id),
  CONSTRAINT cti_link_order_call_back_enterprise_id_fkey FOREIGN KEY (enterprise_id)
      REFERENCES cti_link_entity (enterprise_id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cti_link_order_call_back
  OWNER TO postgres;
COMMENT ON TABLE cti_link_order_call_back
  IS '预约回呼记录';
COMMENT ON COLUMN cti_link_order_call_back.id IS '主键';
COMMENT ON COLUMN cti_link_order_call_back.enterprise_id IS '企业ID';
COMMENT ON COLUMN cti_link_order_call_back.qno IS '队列号';
COMMENT ON COLUMN cti_link_order_call_back.tel IS '主叫号码';
COMMENT ON COLUMN cti_link_order_call_back.is_call IS '是否外呼  0 还没呼叫  1  已呼叫';
COMMENT ON COLUMN cti_link_order_call_back.order_time IS '预约时间';
COMMENT ON COLUMN cti_link_order_call_back.create_time IS '记录创建时间';
COMMENT ON COLUMN cti_link_order_call_back.cno IS '执行此外呼的坐席';
COMMENT ON COLUMN cti_link_order_call_back.call_back_time IS '坐席回呼时间';
COMMENT ON COLUMN cti_link_order_call_back.queue_name IS '队列名称';
COMMENT ON COLUMN cti_link_order_call_back.area IS '地区';


-- 分机配置表  webrtc软电话
