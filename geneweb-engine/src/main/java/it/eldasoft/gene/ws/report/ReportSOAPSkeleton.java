/**
 * ReportSOAPSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.gene.ws.report;

public class ReportSOAPSkeleton implements it.eldasoft.gene.ws.report.Report_PortType, org.apache.axis.wsdl.Skeleton {
    private it.eldasoft.gene.ws.report.Report_PortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "codice"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getDefinizioneReport", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/Report/", "GetDefinizioneReport"));
        _oper.setSoapAction("http://www.eldasoft.it/Report/GetDefinizioneReport");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getDefinizioneReport") == null) {
            _myOperations.put("getDefinizioneReport", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getDefinizioneReport")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "codice"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "codiceUfficioIntestatario"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "parametro"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.eldasoft.it/Report/", "ValParametroType"), it.eldasoft.gene.ws.report.ValParametroType[].class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "pagina"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.Integer.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "maxDimensionePagina"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), java.lang.Integer.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getRisultatoReport", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/Report/", "GetRisultatoReport"));
        _oper.setSoapAction("http://www.eldasoft.it/Report/GetRisultatoReport");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getRisultatoReport") == null) {
            _myOperations.put("getRisultatoReport", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getRisultatoReport")).add(_oper);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "idProgramma"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false), 
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("", "idDocumento"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"), long.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getAllegato", _params, new javax.xml.namespace.QName("", "risultato"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("http://www.eldasoft.it/Report/", "GetAllegato"));
        _oper.setSoapAction("http://www.eldasoft.it/Report/GetAllegato");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getAllegato") == null) {
            _myOperations.put("getAllegato", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getAllegato")).add(_oper);
    }

    public ReportSOAPSkeleton() {
        this.impl = new it.eldasoft.gene.ws.report.ReportSOAPImpl();
    }

    public ReportSOAPSkeleton(it.eldasoft.gene.ws.report.Report_PortType impl) {
        this.impl = impl;
    }
    public java.lang.String getDefinizioneReport(java.lang.String codice) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.getDefinizioneReport(codice);
        return ret;
    }

    public java.lang.String getRisultatoReport(java.lang.String codice, java.lang.String codiceUfficioIntestatario, it.eldasoft.gene.ws.report.ValParametroType[] parametro, java.lang.Integer pagina, java.lang.Integer maxDimensionePagina) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.getRisultatoReport(codice, codiceUfficioIntestatario, parametro, pagina, maxDimensionePagina);
        return ret;
    }

    public java.lang.String getAllegato(java.lang.String idProgramma, long idDocumento) throws java.rmi.RemoteException
    {
        java.lang.String ret = impl.getAllegato(idProgramma, idDocumento);
        return ret;
    }

}
