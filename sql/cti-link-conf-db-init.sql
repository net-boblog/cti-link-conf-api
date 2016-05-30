------ common
insert into cti_link_sip_group(id,percent,description) values(1,100,'sip-media-server 运行组');
insert into cti_link_sip_group(id,percent,description) values(2,100,'sip-media-server 升级组');
select setval('cti_link_sip_group_id_seq',3);

insert into cti_link_sip_proxy(id,name,ip_addr,port,description,active) values(1,'sip-proxy-1','10.10.10.1',5060,'sip-proxy-1',1);
insert into cti_link_sip_proxy(id,name,ip_addr,port,description,active) values(2,'sip-proxy-2','10.10.10.2',5060,'sip-proxy-2',1);
select setval('cti_link_sip_proxy_id_seq',3);

insert into cti_link_gateway(id,name,prefix,ip_addr,port,area_code,description,call_limit,disallow,allow,dtmf_mode) values(1,'vos','111','10.10.10.1',32766,'010','外呼vos',3000,'all','alaw,ulaw,g729,gsm','rfc4733');
select setval('cti_link_gateway_id_seq',2);

insert into cti_link_routerset(id,name,description) values(0,'空路由','空路由'); -- 空路由, 企业路由选择表enterprise_router需要，呼入呼叫坐席，预览/预测外呼呼客户与呼座席非空设置，功能未开通时，默认值写空路由。
--设定系统默认路由
insert into cti_link_system_setting(name,value,property) values('default_router','-1','默认路由'); --默认路由

insert into cti_link_routerset(id,name,description) values(1,'默认路由','默认外呼路由');
select setval('cti_link_routerset_id_seq',2);

insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'0',1,1,'0开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'1',1,1,'1开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'2',1,1,'2开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'3',1,1,'3开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'4',1,1,'4开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'5',1,1,'5开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'6',1,1,'6开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'7',1,1,'7开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'8',1,1,'8开头');
insert into cti_link_router(routerset_id,prefix,gateway_id,priority,description) values(1,'9',1,1,'9开头');


--添加公共语音库
insert into cti_link_public_voice(id,voice_name,path,description) values(100000,'上班时间欢迎词','on_duty.wav','您好，感谢您的来电，业务咨询请按1，客户服务请按2，留言请按9。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100001,'下班时间欢迎词','off_duty.wav','您好，感谢您的来电，现在是我们的下班时间，请在滴声后留言，我们将及时处理，谢谢。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100002,'留言','default_voice_mail.wav','请在滴声后留言。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100003,'转接中1','default_transfer.wav','转接中，请稍后。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100004,'上班时间总机分机','tel_exten_on_duty.wav','欢迎致电本公司，请拨分机号码，查号请拨零。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100005,'下班时间总机分机','tel_exten_off_duty.wav','现在是下班时间，请在滴声后留言。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100006,'转接中2','public_transfer.wav','您的来电正在转接中，请稍后。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100007,'欢迎词','public_welcome.wav','欢迎致电本公司业务咨询请按1，客户服务请按2，其他服务请按3，语音留言请按9。');
insert into cti_link_public_voice(id,voice_name,path,description) values(100012,'天润测试','tinet_test.wav','尊敬的客户，欢迎致电天润融通测试热线，业务咨询请按1，投诉及故障申告请按2，语音留言请按9');
insert into cti_link_public_voice(id,voice_name,path,description) values(100013,'0.5秒空音','no.wav','');

--公共语音库中的等待音乐
insert into cti_link_public_voice(id,voice_name,path,description) values(200000,'系统默认等待音乐','default_moh.wav','默认等待音乐');


insert into cti_link_public_moh(id,name,directory,application,mode,digit,sort,format) values (0,'default','/var/lib/moh/default','','files','','random','');
insert into cti_link_public_moh_voice(moh_id,voice_id) values(0,200000);

-- 添加系统设置
insert into cti_link_system_setting(name,value,property) values('default_area_code','010',''); -- 默认区号
insert into cti_link_system_setting(name,value,property) values('alert_email','cti-link.list@ti-net.com.cn',''); -- 默认区号
insert into cti_link_system_setting(name,value,property) values('alert_tel','13426307922',''); -- 默认区号
-- 系统设置

insert into cti_link_system_setting(name,value,property) values('unicom_segment','130,131,132,145,155,156,185,186,176',''); -- 联通手机号码段
insert into cti_link_system_setting(name,value,property) values('telecom_segment','133,153,180,181,189,177',''); -- 电信手机号码段
insert into cti_link_system_setting(name,value,property) values('mobile_segment','134,135,136,137,138,139,150,151,152,157,158,159,182,187,188,147,178',''); -- 移动手机号码段

insert into cti_link_system_setting(name,value,property) values('aws_s3_expiration','2',''); -- aws s3 资源访问有效期 单位：分钟

insert into cti_link_system_setting(name,value,property) values('tts_proxy_url','http://internal-vocp-ttssc-internal-8477452.cn-north-1.elb.amazonaws.com.cn',''); -- ttssc url

-- 添加系统设置

insert into cti_link_system_setting(name,value,property) values('ami_response_timeout','65','second'); -- ami超时时间

 -- BOSS2接口访问域名设置
insert into cti_link_system_setting(name,value,property) values('timed_task_email_address','cti-link.list@ti-net.com.cn',''); -- 定时任务报告邮件
insert into cti_link_system_setting(name,value,property) values('curl_engine','5','20'); -- curl推送模块加载选项 start/stop 默认空非null 默认3个线程
insert into cti_link_system_setting(name,value,property) values('service_level','10',''); -- 系统默认服务水平参数
insert into cti_link_system_setting(name,value,property) values('sms_url','http://smsc.ti-net.com.cn/SmsSendNew.jsp','mobile,message,cell'); -- 短信通道用户名密码配置

insert into cti_link_system_setting(name,value,property) values('predictive_channel_limit','3000','');
insert into cti_link_system_setting(name,value,property) values('webrtc_websocket_url','1','wss://sbc.cti-link.com:7443'); -- websocket url
insert into cti_link_system_setting(name,value,property) values('webrtc_stun_server','{url:''stun:172.16.236.21:7478''}',''); -- webrtc stun server

insert into cti_link_system_setting(name,value,property) values('speech_engine','1','4'); -- 预测外呼未接通话语音分析模块 property为并发线程数

-- 系统设置

insert into cti_link_system_setting(name,value,property) values('unicom_segment','130,131,132,145,155,156,185,186',''); -- 联通手机号码段
insert into cti_link_system_setting(name,value,property) values('telecom_segment','133,153,180,181,189',''); -- 电信手机号码段
insert into cti_link_system_setting(name,value,property) values('mobile_segment','134,135,136,137,138,139,150,151,152,157,158,159,182,187,188,147',''); -- 移动手机号码段

insert into cti_link_system_setting(name,value,property)values('curl_again','0','失败curl重新推送开关 0关闭 1开启');

insert into cti_link_system_setting(name,value,property)values('ib_call_remember_time','86400','呼入来电记忆持续秒数，默认1天');
insert into cti_link_system_setting(name,value,property)values('ob_call_remember_time','604800','外呼来电记忆持续秒数，默认1天');

-- control api访问频度, value个数, property时间单位, second, minute, hour, day
insert into cti_link_system_setting(name,value,property)values('control_api_max_request_count','60000','minute');
insert into cti_link_system_setting(name,value,property)values('control_api_white_ip_list','*.*.*.*','control api ip白名单, 多个逗号分隔');
