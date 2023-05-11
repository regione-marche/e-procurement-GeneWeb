/**
 * Report_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.gene.ws.report;

public interface Report_Service extends javax.xml.rpc.Service {
    public java.lang.String getReportSOAPAddress();

    public it.eldasoft.gene.ws.report.Report_PortType getReportSOAP() throws javax.xml.rpc.ServiceException;

    public it.eldasoft.gene.ws.report.Report_PortType getReportSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
