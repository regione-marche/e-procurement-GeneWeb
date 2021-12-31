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

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<html:form action="/DocumentoAssociato.do" method="post" enctype="multipart/form-data">
	<table class="dettaglio-notab">
	  <tr>
	    <td class="etichetta-dato">Data</td>
	    <td class="valore-dato">${documento.dataInserimento}
	        <html:hidden property="dataInserimento" value="${documento.dataInserimento}"/>
	    </td>
	  </tr>
	  <tr>
	    <td class="etichetta-dato">Titolo *</td>
	    <td class="valore-dato"><html:text property="titolo" value="${documento.titolo}" size="30" maxlength="64"/></td>
	  </tr>
	  <tr>
	<c:choose>
  	<c:when test='${not empty uploadFile and not empty documento.nomeDocAss}' >
	    <td class="etichetta-dato">Nome File
  	  <td class="valore-dato">
  	  	${documento.nomeDocAss}&nbsp;&nbsp;<html:file property="selezioneFile" size="30" maxlength="255"/>
  	  	<html:hidden property="nomeDocAss" value="${documento.nomeDocAss}"/>
  	  </td>
 	  </c:when>
  	<c:when test='${not empty uploadFile and empty documento.nomeDocAss}' >
	    <td class="etichetta-dato">Nome File *
  	  <td class="valore-dato">
  	  	<html:file property="selezioneFile" size="30" maxlength="255"/>
  	  </td>
 	  </c:when>
 	  <c:otherwise>
	    <td class="etichetta-dato">Nome File</td>
  	  <td class="valore-dato">
  	  	${documento.nomeDocAss}
  	  	<html:hidden property="nomeDocAss" value="${documento.nomeDocAss}"/>
  	  </td>
 	  </c:otherwise>
 	</c:choose>
	  </tr>
	<c:if test='${((! documento.documentoInAreaShared) && (! empty documento.id))}'>
		<tr>
	    <td class="etichetta-dato">Percorso file</td>
	    <td class="valore-dato">
	    	${documento.pathDocAss}
	    </td>
	  </tr>
	</c:if>
  <tr>
    <td class="etichetta-dato">Tipo documento</td>
    <td class="valore-dato">
      	<html:select property="tipoDocumento" name="documento">
      		<html:option value="">&nbsp;</html:option>
	      	<html:options collection="listaTipoDocumento" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Data scadenza documento</td>
    <td class="valore-dato">
    <input type="text" name="dataScadenzaDocumento" id="dataScadenzaDocumento" onblur="javascript:controllaInputData(this);" value="${documento.dataScadenzaDocumento}" class="data">
		&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Numero protocollo</td>
    <td class="valore-dato"><html:text property="numeroProtocollo" value="${documento.numeroProtocollo}" size="20" maxlength="20"/></td>
  </tr>
  <tr>
    <td class="etichetta-dato">Data protocollo</td>
    <td class="valore-dato">
    <input type="text" name="dataProtocollo" id="dataProtocollo" onblur="javascript:controllaInputData(this);" value="${documento.dataProtocollo}" class="data">
		&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Numero atto</td>
    <td class="valore-dato"><html:text property="numeroAtto" value="${documento.numeroAtto}" size="20" maxlength="20"/></td>
  </tr>
  <tr>
    <td class="etichetta-dato">Data atto</td>
    <td class="valore-dato">
    <input type="text" name="dataAtto" id="dataAtto" onblur="javascript:controllaInputData(this);" value="${documento.dataAtto}" class="data">
		&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
    </td>
  </tr>
	  <tr>
	    <td class="etichetta-dato">Annotazioni</td>
	    <td class="valore-dato"><html:textarea property="annotazioni" value="${documento.annotazioni}" cols="80" rows="25"/></td>
	  </tr>
	  <tr>
	    <td class="comandi-dettaglio" colSpan="2">
	      <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
	  <c:choose>
	    <c:when test='${empty documento.id}'>
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">
      </c:when>
      <c:otherwise>
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifiche(${documento.id});">      	
      </c:otherwise>
    </c:choose>
        &nbsp;
	    </td>
	  </tr>
	</table>
	<c:if test='${not empty documento.id}' >
		<html:hidden property="id" value="${documento.id}"/>
	</c:if>
	<html:hidden property="tipoAccesso" value="${documento.tipoAccesso}" /> <%-- campo usato dal MUDE --%>
  <html:hidden property="entita" value="${documento.entita}" />
  <html:hidden property="codApp" value="${documento.codApp}" />
	<html:hidden property="metodo" value="salva" />
   	<html:hidden property="documentoInAreaShared" value="${documento.documentoInAreaShared}"/>
	<c:choose>
		<c:when test='${documento.documentoInAreaShared}'>
			<html:hidden property="pathDocAss" value="[default]"/>
		</c:when>
	 	<c:otherwise>
	   		<html:hidden property="pathDocAss" value="${documento.pathDocAss}"/>
	 	</c:otherwise>
	</c:choose>
	<c:forEach items="${documento.valoriCampiChiave}" var="valoriCampi">
		<html:hidden property="valoriCampiChiave" value="${valoriCampi}"/>
	</c:forEach>
</html:form>

<script type="text/javascript">
<!--
	$('textarea[name="annotazioni"]').bind('input propertychange', function() {checkInputLength( $(this)[0], 2000)});
-->
</script>