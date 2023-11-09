package cn.cnic.component.flow.jpa.repository;

import cn.cnic.component.flow.entity.Paths;
import java.io.Serializable;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PathsJpaRepository
    extends JpaRepository<Paths, String>, JpaSpecificationExecutor<Paths>, Serializable {

  @Transactional
  @Modifying
  @Query("update Paths c set c.enableFlag = :enableFlag where c.id = :id")
  int updateEnableFlagById(@Param("id") String id, @Param("enableFlag") boolean enableFlag);
}
