<%/*
   * Created on 24-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI VISUALIZZAZIONE
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<gene:setIdPagina schema="GENE" maschera="C0OGGASS-Scheda" />

<table class="dettaglio-notab">
  <tr>
    <td class="etichetta-dato">Data</td>
    <td class="valore-dato">${documento.dataInserimento}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Titolo</td>
    <td class="valore-dato">${documento.titolo}</td>
  </tr>
  <tr>
    <td class="etichetta-dato">Nome file</td>
    <td class="valore-dato">
    	<a href="javascript:mostraDocumento();" title="Download documento">${documento.nomeDocAss}</a>
    </td>
  </tr>
<c:if test='${! documento.documentoInAreaShared and documento.pathDocAss ne "[default]"}'>
	<tr>
    <td class="etichetta-dato">Percorso file</td>
    <td class="valore-dato">
    	${fn:replace(documento.pathDocAss, "/", "\\")}
    </td>
  </tr>
</c:if>
  <tr>
    <td class="etichetta-dato">Tipo documento</td>
    <td class="valore-dato">
      <c:forEach items="${listaTipoDocumento}" var="tipoDoc">
      	<c:if test="${tipoDoc.tipoTabellato eq documento.tipoDocumento}">
		      ${tipoDoc.descTabellato}      		
      	</c:if>
      </c:forEach>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Data scadenza documento</td>
    <td class="valore-dato">${documento.dataScadenzaDocumento}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Numero protocollo</td>
    <td class="valore-dato">${documento.numeroProtocollo}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Data protocollo</td>
    <td class="valore-dato">${documento.dataProtocollo}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Numero atto</td>
    <td class="valore-dato">${documento.numeroAtto}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Data atto</td>
    <td class="valore-dato">${documento.dataAtto}
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Annotazioni</td>
    <td class="valore-dato">${documento.annotazioni}</td>
  </tr>
  
  <html:hidden property="id" value="${documento.id}" />
  <html:hidden property="entita" value="${documento.entita}" />
  <c:forEach items="${documento.valoriCampiChiave}" var="valoriCampi">
  	<html:hidden property="valoriCampiChiave" value="${valoriCampi}"/>
  </c:forEach>
	<input type="hidden" name="key" value="${param.key}"/>
	<input type="hidden" name="keyParent" value="${param.keyParent}"/>
	
<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" && gene:checkProtFunz(pageContext, "MOD", "MOD")}'>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica documento" onclick="javascript:modifica();">&nbsp;
    </td>
  </tr>
</c:if>
</table>