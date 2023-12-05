select @dss_appconn_pipelineId:=id from `dss_appconn` where `appconn_name` = 'pipeline';

delete from `dss_appconn_instance` where  `appconn_id`=@dss_appconn_pipelineId;
delete from `dss_appconn`  where `appconn_name`='pipeline';
INSERT INTO `dss_appconn` (
            `appconn_name`,
            `is_user_need_init`,
            `level`,
            `if_iframe`,
            `is_external`,
            `reference`,
            `class_name`,
            `appconn_class_path`,
            `resource`)
            VALUES (
                'pipeline',
                0,
                1,
                1,
                1,
                NULL,
                'com.webank.wedatasphere.dss.appconn.pipeline.PipelineAppConn',
                'DSS_INSTALL_HOME_VAL/dss-appconns/pipeline',
                '');


select @dss_appconn_pipelineId:=id from `dss_appconn` where `appconn_name` = 'pipeline';

INSERT INTO `dss_appconn_instance`(
            `appconn_id`,
            `label`,
            `url`,
            `enhance_json`,
            `homepage_uri`)
            VALUES (@dss_appconn_pipelineId,
            'DEV',
            'http://APPCONN_INSTALL_IP:APPCONN_INSTALL_PORT/',
            '',
            '#/dashboard');

select @dss_pipeline_appconnId:=id from `dss_appconn` WHERE `appconn_name` in ('pipeline');

select @pipeline_menuId:=id from `dss_workspace_menu` WHERE `name` in ('流水线');
delete from `dss_workspace_menu_appconn` where `title_en`='Pipeline';

INSERT INTO `dss_workspace_menu_appconn` (
    `appconn_id`,
    `menu_id`,
    `title_en`,
    `title_cn`,
    `desc_en`,
    `desc_cn`,
    `labels_en`,
    `labels_cn`,
    `is_active`,
    `access_button_en`,
    `access_button_cn`,
    `manual_button_en`,
    `manual_button_cn`,
    `manual_button_url`,
    `icon`, `order`,
    `create_by`,
    `create_time`,
    `last_update_time`,
    `last_update_user`,
    `image`)
VALUES (@dss_pipeline_appconnId,
       @pipeline_menuId,
       'Pipeline',
       'Pipeline',
       'Pipeline is an easy to use, powerful big data pipeline system',
       'Pipeline是一个简单易用，功能强大的大数据流水线系统',
       'product, operations',
       '生产,运维',
       '1',
       'enter Pipeline',
       '进入Pipeline',
       'user manual',
       '用户手册',
       'http://127.0.0.1:8088/wiki/scriptis/manual/workspace_cn.html',
       'pipeline-logo',
       NULL,
       NULL,
       NULL,
       NULL,
       NULL,
       'pipeline-icon');

select @dss_pipelineId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.pipeline';

delete from `dss_workflow_node_to_ui` where `workflow_node_id`=@dss_pipelineId;
delete from `dss_workflow_node_to_group` where `node_id`=@dss_pipelineId;
delete from `dss_workflow_node` where `node_type`='linkis.appconn.pipeline';

INSERT INTO `dss_workflow_node` (
            `icon_path`,
            `node_type`,
            `appconn_name`,
            `submit_to_scheduler`,
            `enable_copy`,
            `should_creation_before_node`,
            `support_jump`,
            `jump_type`,
            `name`)
            VALUES (
            'icons/pipeline.icon',
            'linkis.appconn.pipeline',
            'pipeline',
            1,
            0,
            0,
            1,
            1,
            'pipeline');

select @dss_pipeline_nodeId:=id from `dss_workflow_node` where `node_type` = 'linkis.appconn.pipeline';

INSERT INTO `dss_workflow_node_to_group` (`node_id`, `group_id`) VALUES (@dss_pipeline_nodeId, 2);
INSERT INTO `dss_workflow_node_to_ui` (workflow_node_id, ui_id) VALUES (@dss_pipeline_nodeId, 1),(@dss_pipeline_nodeId,3),(@dss_pipeline_nodeId,5),(@dss_pipeline_nodeId,6),(@dss_pipeline_nodeId,35),(@dss_pipeline_nodeId,36);
