/**
 * Report_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.gene.ws.report;

public interface Report_PortType extends java.rmi.Remote {
    public java.lang.String getDefinizioneReport(java.lang.String codice) throws java.rmi.RemoteException;
    public java.lang.String getRisultatoReport(java.lang.String codice, java.lang.String codiceUfficioIntestatario, it.eldasoft.gene.ws.report.ValParametroType[] parametro, java.lang.Integer pagina, java.lang.Integer maxDimensionePagina) throws java.rmi.RemoteException;
    public java.lang.String getAllegato(java.lang.String idProgramma, long idDocumento) throws java.rmi.RemoteException;
}
