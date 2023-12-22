package cn.cnic.base.utils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;

/**
 * @author nature
 */
public class ThreadPoolExecutorUtils {

  public static ThreadPoolExecutor createThreadPoolExecutor(
      Integer corePoolSize, Integer maximumPoolSize, Long keepAliveTime) {
    if (null == corePoolSize) {
      corePoolSize = 1;
    }
    if (null == maximumPoolSize) {
      maximumPoolSize = 5;
    }
    if (null == keepAliveTime) {
      keepAliveTime = 0L;
    }
    return new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>(100000));
  }
}
