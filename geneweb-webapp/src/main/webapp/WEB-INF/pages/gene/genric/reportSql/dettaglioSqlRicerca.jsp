<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="testata" value="${sessionScope.recordDettRicerca.testata}" scope="request" />


<table class="dettaglio-tab">
  <tr>
    <td class="etichetta-dato">Istruzione SQL da eseguire (*)</td>
    <td class="valore-dato">
     	<c:out value="${requestScope.defSql}" escapeXml="false" />
    </td>
  </tr>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica query SQL" onclick="javascript:modifica()" >
      <c:if test="${!empty (sessionScope.recordDettModificato) || !sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}">
	      <INPUT type="button" class="bottone-azione" value="Salva report" title="Salva report nella banca dati" onclick="javascript:salvaRicerca()" >
      </c:if>
      <c:if test="${!empty (sessionScope.recordDettModificato) && !empty (testata.id)}">
        <INPUT type="button" class="bottone-azione" value="Annulla modifica" title="Annulla le modifiche e ricarica il report dalla banca dati" onclick="javascript:ripristinaRicercaSalvata()" >
      </c:if>
      <c:if test="${!empty (sessionScope.recordDettModificato) && empty (testata.id)}">
        <INPUT type="button" class="bottone-azione" value="Annulla inserimento" title="Annulla" onclick="javascript:annullaCreazioneRicerca()" >
      </c:if>
      <c:if test="${empty (sessionScope.recordDettModificato) && sessionScope.recordDettRicerca.statoReportNelProfiloAttivo}"><INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:eseguiRicerca()" ></c:if>
       &nbsp;
    </td>
  </tr>
</table>  