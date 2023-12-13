module.exports = {
    title:"PiFlow大数据流水线管理系统",
    logOut: '退出',
    sidebar: {
        dashboard: "首页",
        flow: "流水线",
        group: "流水线组",
        processes: "流水线进程",
        template: "流程模板",
        data_source: "数据源",
        schedule: '流水线调度',
        stopHub: '自定义组件',
        sparkJar: "Spark Jar包",
        testData: "测试数据",
        example: "样例",
        admin: "高级",
        admin_schedule: '监控同步任务',
        stopsComponent: '组件显示'
    },
    page: {
        prev_text: "上一页",
        next_text: "下一页"
    },
    index: {
        cpu: "核"
    },
    tip: {
        title: "PiFlow系统提示",
        existed: "用户名重复！",
        fault_content: "后台程序运行发生未知错误！",
        update_success_content: "更新成功！",
        update_fail_content: "更新失败！",
        save_success_content: "保存成功！",
        save_fail_content: "保存失败！",
        add_success_content: "添加成功！",
        add_fail_content: "添加失败！",
        run_success_content: "运行成功！",
        run_fail_content: "运行失败！",
        mount_success_content: "挂载成功！",
        mount_fail_content: "挂载失败！",
        stop_success_content: "停止成功！",
        stop_fail_content: "停止失败！",
        unmount_success_content: "卸载成功！",
        unmount_fail_content: "卸载失败！",
        get_success_content: "数据采集成功！",
        get_fail_content: "数据采集失败！",
        debug_success_content: "debug 运行成功！",
        debug_fail_content: "debug 运行失败！",
        delete_success_content: "删除成功！",
        delete_fail_content: "删除失败！",
        request_fail_content: "数据请求失败！",

        upload_success_content: "模板上传成功！",
        upload_fail_content: "模板上传失败！",
        download_success_content: "下载成功！",
        download_fail_content: "下载失败！",

        data_fail_content: "数据采集失败！",
    },
    modal: {
        placeholder: "请输入内容...",
        template_title: "保存模板名称",
        ok_text: "提交",
        upload_text: "提交",
        confirm: "确定",
        cancel_text: "取消",
        delete_content: "确定要删除",
        cancel_content: "确定要取消本次创建吗"
    },
    flow_columns: {
        create_title: "新建流水线",
        update_title: "更改流水线",
        flow_name: "流水线名称",
        driverMemory: "Driver内存",
        executorNumber: "Executor数量",
        executorMemory: "Executor内存",
        executorCores: "ExecutorCPU核数",
        name: "名称",
        description: "描述信息",
        CreateTime: "创建时间",
        action: "操作"
    },
    group_columns: {
        create_title: "新建流水线组",
        update_title: "更改流水线组",
        group_name: "流水线组名称",
        name: "名称",
        description: "描述信息",
        CreateTime: "创建时间",
        action: "操作"
    },
    progress_columns: {
        progress: "进程",
        endTime: "开始时间",
        startTime: "结束时间",
        processType: "流程类型",
        description: "描述信息",
        id: "ProcessGroupId",
        name: "名称",
        state: "状态",
        action: "操作"
    },
    template_columns: {
        name: "名称",
        crtDttm: "创建时间",
        action: "操作"
    },
    dataSource_columns: {
        create_title: "新建数据源",
        update_title: "更改数据源",
        type: "数据源类型",
        dataSource_name: "数据源名称",
        addProperty: "添加属性",
        name: "名称",
        description: "描述信息",
        CreateTime: "创建时间",
        dataSourceType: "数据源类型",
        action: "操作",
        isLinked: "是否同步更改?相关的组件："
    },
    schedule_columns: {
        create_title: "新建流水线调度",
        update_title: "更改流水线调度",
        scheduleType: "调度类型",
        startDate: "开始时间",
        endDate: "结束时间",
        flowIsGroup: "流水线 / 流水线组",
        cron: "CRON表达式",
        type:'类型',
        name: "名称",
        StartTime:"开始时间",
        EndTime:"结束时间",
        status: "状态",
        action: "操作"
    },
    StopHub_columns: {
        upload: "新建自定义组件",
        name: "组件名称",
        version: "版本",
        jarUrl: "Jar 地址",
        status: "状态",
        action: "操作",
        jarDescription: '点 击 或 拖 动 jar 在 此 处 上 传'
    },
    testData_columns:{
        create_title: "新建测试数据",
        update_title: "更改测试数据",
        testData_name: "测试名称",
        name: "名称",
        description: "描述信息",
        methed: "创建方式",
        header: "表头",
        delimiter: "分隔符",
        schema: "表结构",
        CreateTime: "创建时间",
        action: "操作",
        fileDescription: "点 击 或 拖 动 文件 在 此 处 上 传",
        uploadPrompt: "若您上传的CSV文件包含中文字符，请使用UTF-8编码"
    },
    admin_columns: {
        create_title: "新建调度",
        update_title: "更改调度",
        jobName: "名称",
        jobClass: "类",
        cronExpression: "CRON表达式",
        name: "调度名称",
        class: "类",
        cron: "CRON表达式",
        status: "状态",
        createTime: "创建时间",
        action: "操作"
    },
    stopsComponent_columns: {
        name: "组件名称",
        description: "描述信息",
    },
    homeInfo: {
        introduction_title: "简介",
        monitor_title: "监控",
        statistics_title: "统计",
        CPU_Disk: 'CPU磁盘使用率',
        Memory_Disk: '内存磁盘使用率',
        HDFS_Disk: 'HDFS磁盘使用率',
        totalCapacity: '总容量',
        Used: '已使用',
        introduction_Info: "PiFlow是一款基于分布式计算引擎Spark开发的大数据流水线系统，以所见即所得的方式，实现大数据采集、处理、存储与分析流程化配置、运行与智能监控，提供了100+的标准化组件， 包括Hadoop 、Spark、MLlib、Hive、Solr、ElasticSearch、Redis等，更支持面向领域、灵活的二次组件开发，性能优越。",
        flowStatistics: "流水线统计信息，包括流水线Flow数量、运行态流水线Processor数量，以及各运行状态下Processor数量。",
        scheduleStatistics: "调度统计信息，包括调度流水线/流水线组数量，以及各个状态下Schedule数量。",
        groupStatistics: "流水线组统计信息，包括流水线组Group数量、运行态流水线组Processor数量，以及各运行状态下Processor数量。",
        OtherStatistics: "其他统计信息，包括数据源DataSource数量、自定义组件插件StopsHub数量、模板Template数量。",
        ComponentStatistics: "组件统计信息，包括数据处理组件Stop数量，数据处理组件组StopGroup数量。",
    }
}