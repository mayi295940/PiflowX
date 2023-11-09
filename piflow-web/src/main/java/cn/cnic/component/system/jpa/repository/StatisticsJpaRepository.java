package cn.cnic.component.system.jpa.repository;

import cn.cnic.component.system.entity.Statistics;
import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface StatisticsJpaRepository
    extends JpaRepository<Statistics, String>, JpaSpecificationExecutor<Statistics>, Serializable {}
