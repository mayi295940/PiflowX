package cn.cnic.component.process.entity;

import cn.cnic.base.BaseHibernateModelUUIDNoCorpAgentId;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "FLOW_PROCESS_GROUP_PATH")
public class ProcessGroupPath extends BaseHibernateModelUUIDNoCorpAgentId {

    private static final long serialVersionUID = 1L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FK_FLOW_PROCESS_GROUP_ID")
    private ProcessGroup processGroup;

    @Column(name = "LINE_FROM")
    private String from;

    @Column(name = "LINE_OUTPORT")
    private String outport;

    @Column(name = "LINE_INPORT")
    private String inport;

    @Column(name = "LINE_TO")
    private String to;

    @Column(name = "page_id")
    private String pageId;

}
