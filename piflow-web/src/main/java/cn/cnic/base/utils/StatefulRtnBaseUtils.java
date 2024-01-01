package cn.cnic.base.utils;

import cn.cnic.base.vo.StatefulRtnBase;
import org.slf4j.Logger;

public class StatefulRtnBaseUtils {

  /** Introducing logs, note that they are all packaged under "org.slf4j" */
  private static Logger logger = LoggerUtil.getLogger();

  /**
   * set Failure information
   *
   * @param errorMsg
   * @return
   */
  public static StatefulRtnBase setFailedMsg(String errorMsg) {
    StatefulRtnBase statefulRtnBase = new StatefulRtnBase();
    logger.info(errorMsg);
    statefulRtnBase.setReqRtnStatus(false);
    statefulRtnBase.setErrorCode(statefulRtnBase.ERRCODE_FAIL);
    statefulRtnBase.setErrorMsg(errorMsg);
    return statefulRtnBase;
  }

  /**
   * set Success message
   *
   * @param SuccessdMsg
   * @return
   */
  public static StatefulRtnBase setSuccessdMsg(String SuccessdMsg) {
    StatefulRtnBase statefulRtnBase = new StatefulRtnBase();
    statefulRtnBase.setReqRtnStatus(true);
    statefulRtnBase.setErrorCode(statefulRtnBase.ERRCODE_SUCCESS);
    statefulRtnBase.setErrorMsg(SuccessdMsg);
    return statefulRtnBase;
  }
}