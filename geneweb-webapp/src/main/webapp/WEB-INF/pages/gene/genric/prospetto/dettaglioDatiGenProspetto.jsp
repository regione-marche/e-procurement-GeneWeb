<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<table class="dettaglio-tab">
   <tr>
     <td class="etichetta-dato">Tipo report</td>
     <td class="valore-dato"> 
      <c:forEach items="${listaTipoRicerca}" var="tipoRic">
      	<c:if test="${tipoRic.tipoTabellato eq fn:trim(datiGenProspettoForm.tipoRicerca)}">
		      <c:set var="nomeRic" value="${tipoRic.descTabellato}"/>      		
      	</c:if>
      </c:forEach>
     	<c:out value="${nomeRic}"/>
     </td>
   </tr>
   	<c:remove var="nomeRic" />
   <tr>
     <td class="etichetta-dato">Titolo</td>
     <td class="valore-dato"> <c:out value="${datiGenProspettoForm.nome}"/> </td>
   </tr>
   <tr>
     <td class="etichetta-dato" >Descrizione</td>
     <td class="valore-dato"> <c:out value="${datiGenProspettoForm.descrRicerca}"/> </td>
   </tr>
   
 <c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
   <tr>
     <td class="etichetta-dato" >Report personale</td>
     <td class="valore-dato"> 
			<c:choose>
    		<c:when test='${datiGenProspettoForm.personale}'>
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
    <td class="valore-dato"> <c:out value="${datiGenProspettoForm.codReportWS}"/> </td>
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
    		<c:when test='${datiGenProspettoForm.ricercaDisponibile}'>
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
    <td class="etichetta-dato" >File</td>
    <td class="valore-dato">
      <a href="javascript:mostraProspetto(${datiGenProspettoForm.idRicerca},'${datiGenProspettoForm.nomeFile}');" title="Download modello">
      	${datiGenProspettoForm.nomeFile}
      </a> 
    </td>
  </tr>
	<tr>
    <td colspan="2"><b>Sorgente dati</b></td>
  </tr>
	<tr>
    <td class="etichetta-dato">Sorgente</td>
      <td class="valore-dato">
	      <c:if test="${datiGenProspettoForm.tipoFonteDati == 0}">
	      	Base dati
	      </c:if>
	      <c:if test="${datiGenProspettoForm.tipoFonteDati == 1}">
	      	Report
	      </c:if>
      </td>
  </tr>
	<c:choose>
	<c:when test="${datiGenProspettoForm.tipoFonteDati == 1}">
	<tr>
    <td class="etichetta-dato" >Report origine dati</td>
      <td class="valore-dato">${datiGenProspettoForm.nomeRicercaSrc}</td>
    </tr>
	</c:when>
	<c:otherwise>
	<tr>
    <td class="etichetta-dato">Schema principale</td>
      <td class="valore-dato">
	      <c:if test="${!empty (datiGenProspettoForm.entPrinc)}">
	      	${datiGenProspettoForm.schemaPrinc} - ${datiGenProspettoForm.descSchemaPrinc}
	      </c:if>
      </td>
    </tr>
	<tr>
    <td class="etichetta-dato">Argomento principale</td>
    <td class="valore-dato">
    	<c:if test="${!empty (datiGenProspettoForm.entPrinc)}">
    		${datiGenProspettoForm.mneEntPrinc} - ${datiGenProspettoForm.descEntPrinc}
     	</c:if>
    </td>
  </tr>
	</c:otherwise>
	</c:choose>
  <tr>
    <td class="comandi-dettaglio" colSpan="2">
      <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica dati generali" onclick="javascript:modifica();">
      <INPUT type="button" class="bottone-azione" value="Esegui report" title="Esegui estrazione report" onclick="javascript:eseguiRicerca();">
      &nbsp;
    </td>
  </tr>
</table>