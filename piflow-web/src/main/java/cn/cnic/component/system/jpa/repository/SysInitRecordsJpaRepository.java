package cn.cnic.component.system.jpa.repository;

import cn.cnic.component.system.entity.SysInitRecords;
import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysInitRecordsJpaRepository
    extends JpaRepository<SysInitRecords, String>,
        JpaSpecificationExecutor<SysInitRecords>,
        Serializable {}
