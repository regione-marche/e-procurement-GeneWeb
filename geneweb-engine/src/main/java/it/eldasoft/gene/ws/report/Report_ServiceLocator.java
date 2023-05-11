/**
 * Report_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.gene.ws.report;

public class Report_ServiceLocator extends org.apache.axis.client.Service implements it.eldasoft.gene.ws.report.Report_Service {

    public Report_ServiceLocator() {
    }


    public Report_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Report_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ReportSOAP
    private java.lang.String ReportSOAP_address = "http://www.eldasoft.it/Report";

    public java.lang.String getReportSOAPAddress() {
        return ReportSOAP_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ReportSOAPWSDDServiceName = "ReportSOAP";

    public java.lang.String getReportSOAPWSDDServiceName() {
        return ReportSOAPWSDDServiceName;
    }

    public void setReportSOAPWSDDServiceName(java.lang.String name) {
        ReportSOAPWSDDServiceName = name;
    }

    public it.eldasoft.gene.ws.report.Report_PortType getReportSOAP() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ReportSOAP_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getReportSOAP(endpoint);
    }

    public it.eldasoft.gene.ws.report.Report_PortType getReportSOAP(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            it.eldasoft.gene.ws.report.ReportSOAPStub _stub = new it.eldasoft.gene.ws.report.ReportSOAPStub(portAddress, this);
            _stub.setPortName(getReportSOAPWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setReportSOAPEndpointAddress(java.lang.String address) {
        ReportSOAP_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (it.eldasoft.gene.ws.report.Report_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                it.eldasoft.gene.ws.report.ReportSOAPStub _stub = new it.eldasoft.gene.ws.report.ReportSOAPStub(new java.net.URL(ReportSOAP_address), this);
                _stub.setPortName(getReportSOAPWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("ReportSOAP".equals(inputPortName)) {
            return getReportSOAP();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.eldasoft.it/Report/", "Report");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.eldasoft.it/Report/", "ReportSOAP"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ReportSOAP".equals(portName)) {
            setReportSOAPEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
