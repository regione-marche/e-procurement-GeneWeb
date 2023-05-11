/**
 * ReportSOAPImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package it.eldasoft.gene.ws.report;

import it.eldasoft.gene.bl.genric.ReportFacade;
import it.eldasoft.utils.spring.SpringAppContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class ReportSOAPImpl implements it.eldasoft.gene.ws.report.Report_PortType{
    public java.lang.String getDefinizioneReport(java.lang.String codice) throws java.rmi.RemoteException {
      ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
      ReportFacade facade = (ReportFacade) ctx.getBean("reportFacade");
      return facade.getDefinizioneReport(codice);
    }

    public java.lang.String getRisultatoReport(java.lang.String codice, java.lang.String codiceUfficioIntestatario, it.eldasoft.gene.ws.report.ValParametroType[] parametro, java.lang.Integer pagina, java.lang.Integer maxDimensionePagina) throws java.rmi.RemoteException {
      ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
      ReportFacade facade = (ReportFacade) ctx.getBean("reportFacade");
      return facade.getRisultatoReport(codice, codiceUfficioIntestatario, parametro, pagina, maxDimensionePagina);
    }

    public java.lang.String getAllegato(java.lang.String idProgramma, long idDocumento) throws java.rmi.RemoteException {
      ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(SpringAppContext.getServletContext());
      ReportFacade facade = (ReportFacade) ctx.getBean("reportFacade");
      return facade.getAllegato(idProgramma, idDocumento);
    }

}
