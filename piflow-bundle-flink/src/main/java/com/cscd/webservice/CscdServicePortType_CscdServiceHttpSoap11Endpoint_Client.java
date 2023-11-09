package com.cscd.webservice;

/** Please modify this class to meet your needs This class is not complete */
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;

/**
 * This class was generated by Apache CXF 3.1.12 2017-09-14T15:48:20.063+08:00 Generated source
 * version: 3.1.12
 */
public final class CscdServicePortType_CscdServiceHttpSoap11Endpoint_Client {

  private static final QName SERVICE_NAME = new QName("http://webservice.cscd.com", "CscdService");

  private CscdServicePortType_CscdServiceHttpSoap11Endpoint_Client() {}

  public static void main(String args[]) throws java.lang.Exception {
    URL wsdlURL = CscdService.WSDL_LOCATION;
    if (args.length > 0 && args[0] != null && !"".equals(args[0])) {
      File wsdlFile = new File(args[0]);
      try {
        if (wsdlFile.exists()) {
          wsdlURL = wsdlFile.toURI().toURL();
        } else {
          wsdlURL = new URL(args[0]);
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }

    CscdService ss = new CscdService(wsdlURL, SERVICE_NAME);
    CscdServicePortType port = ss.getCscdServiceHttpSoap11Endpoint();

    {
      System.out.println("Invoking searchByExpr...");
      String _searchByExpr_code = "";
      String _searchByExpr_expr = "";
      String _searchByExpr__return = port.searchByExpr(_searchByExpr_code, _searchByExpr_expr);
      System.out.println("searchByExpr.result=" + _searchByExpr__return);
    }
    {
      System.out.println("Invoking searchArticles...");
      String _searchArticles_code = "";
      String _searchArticles_author = "";
      String _searchArticles_institute = "";
      String _searchArticles_title = "";
      String _searchArticles_orcId = "";
      String _searchArticles__return =
          port.searchArticles(
              _searchArticles_code,
              _searchArticles_author,
              _searchArticles_institute,
              _searchArticles_title,
              _searchArticles_orcId);
      System.out.println("searchArticles.result=" + _searchArticles__return);
    }
    {
      System.out.println("Invoking getCode...");
      String _getCode_user = "";
      String _getCode_passwd = "";
      String _getCode__return = port.getCode(_getCode_user, _getCode_passwd);
      System.out.println("getCode.result=" + _getCode__return);
    }
    {
      System.out.println("Invoking releaseCode...");
      String _releaseCode_code = "";
      port.releaseCode(_releaseCode_code);
    }
    {
      System.out.println("Invoking getArticles...");
      String _getArticles_code = "";
      String _getArticles_cscdIds = "";
      try {
        String _getArticles__return = port.getArticles(_getArticles_code, _getArticles_cscdIds);
        System.out.println("getArticles.result=" + _getArticles__return);

      } catch (CscdServiceException_Exception e) {
        System.out.println("Expected exception: CscdServiceException has occurred.");
        System.out.println(e.toString());
      }
    }
    {
      System.out.println("Invoking getCitedInfo...");
      String _getCitedInfo_code = "";
      String _getCitedInfo_cscdId = "";
      try {
        String _getCitedInfo__return = port.getCitedInfo(_getCitedInfo_code, _getCitedInfo_cscdId);
        System.out.println("getCitedInfo.result=" + _getCitedInfo__return);

      } catch (CscdServiceException_Exception e) {
        System.out.println("Expected exception: CscdServiceException has occurred.");
        System.out.println(e.toString());
      }
    }
    {
      System.out.println("Invoking searchByExprRange...");
      String _searchByExprRange_code = "";
      String _searchByExprRange_expr = "";
      Integer _searchByExprRange_start = null;
      Integer _searchByExprRange_limit = null;
      String _searchByExprRange__return =
          port.searchByExprRange(
              _searchByExprRange_code,
              _searchByExprRange_expr,
              _searchByExprRange_start,
              _searchByExprRange_limit);
      System.out.println("searchByExprRange.result=" + _searchByExprRange__return);
    }

    System.exit(0);
  }
}
