<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="testata" value="${sessionScope.recordDettRicerca.testata}" scope="request" />

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>


<table class="dettaglio-tab">
  <tr>
    <td class="etichetta-dato">Tipo report</td>
    <td class="valore-dato"> 
      <c:forEach items="${listaTipoRicerca}" var="tipoRic">
      	<c:if test="${tipoRic.tipoTabellato eq fn:trim(testata.tipoRicerca)}">
		      <c:set var="nomeRic" value="${tipoRic.descTabellato}"/>      		
      	</c:if>
      </c:forEach>
     	<c:out value="${nomeRic}"/>
    </td>
  </tr>
  <c:remove var="nomeRic" />
  <tr>
    <td class="etichetta-dato">Titolo</td>
    <td class="valore-dato"> <c:out value="${testata.nome}"/> </td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Descrizione</td>
    <td class="valore-dato"> <c:out value="${testata.descrizione}"/> </td>
  </tr>
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
  <tr>
    <td class="etichetta-dato" >Report personale</td>
    <td class="valore-dato"> 
			<c:choose>
    		<c:when test='${testata.personale}'>
    			<c:out value='Si'/>
    		</c:when>
    		<c:otherwise>
    			<c:out value='No'/>
    		</c:otherwise>
    	</c:choose>      	
    </td>
  </tr>
  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP119#")}'>
  <tr>
    <td class="etichetta-dato" >Pubblica il report come servizio con codice</td>
    <td class="valore-dato"> <c:out value="${testata.codReportWS}"/> </td>
  </tr>
  </c:if>
</c:if>
 <!-- F.D. se c'è l'ou48 è giusto dare la possibilità di scegliere 
 se invece non c'è l'ou48 vuol dire che se si è qui c'è l'ou49 quindi non si visualizza-->
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
 	<tr>
    <td class="etichetta-dato" >Mostra nel menù Report</td>
    <td class="valore-dato"> 
			<c:choose>
    		<c:when test='${testata.disp}'>
    			<c:out value='Si'/>
    		</c:when>
    		<c:otherwise>
    			<c:out value='No'/>
    		</c:otherwise> 
    	</c:choose>      	
    </td>
  </tr>
</c:if>   
  <tr>
    <td class="etichetta-dato" >Risultati per pagina</td>
    <td class="valore-dato"> <c:out value="${testata.risPerPag}"/> </td>
  </tr>
<% /*
			Spiegazione del test seguente:
		  ou49 = solo report personali
		  ou53 = report base
		  ou54 = report avanzato
    */ %>
<c:if test='${!(fn:contains(listaOpzioniUtenteAbilitate, "ou49") and 
							  fn:contains(listaOpzioniUtenteAbilitate, "ou53") and 
						    !fn:contains(listaOpzioniUtenteAbilitate, "ou54")) and 
						  testata.famiglia ne 4}'>
	<tr>
		<td class="etichetta-dato" >Abilita modelli predisposti</td>
		<td class="valore-dato"> 
		 	<c:choose>
		 		<c:when test='${testata.visModelli}'>
		 			<c:out value='Si' />
		 		</c:when>
		 		<c:otherwise>
		 			<c:out value='No' />
		 	</c:otherwise> 
		 	</c:choose>
	  </td>
	</tr>
</c:if>
<c:if test='${testata.famiglia eq 1 or testata.famiglia eq 4}'>
  <tr>
    <td class="etichetta-dato" >Stampa parametri nel risultato</td>
    <td class="valore-dato"> 
			<c:choose>
    		<c:when test='${testata.visParametri}'>
    			<c:out value='Si'/>
    		</c:when>
    		<c:otherwise>
    			<c:out value='No'/>
    		</c:otherwise>
    	</c:choose>      	
    </td>
  </tr>
</c:if>
<c:if test='${moduloAttivo ne "W0" and (testata.famiglia ne 2 and testata.famiglia ne 4)}'>
  <tr>
    <td class="etichetta-dato" >Visualizza la scheda di dettaglio</td>
    <td class="valore-dato"> 
			<c:choose>
    		<c:when test='${testata.linkScheda}'>
    			<c:out value='Si'/>
    		</c:when>
    		<c:otherwise>
    			<c:out value='No'/>
    		</c:otherwise>
    	</c:choose>      	
    </td>
  </tr>
</c:if>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica dati generali" onclick="javascript:modifica()" >
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