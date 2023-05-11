<%
/*
 * Created on 04-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
// CONTENENTE I DATI DI UN GRUPPO E LE RELATIVE FUNZIONALITA' IN SOLA VISUALIZZAZIONE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<table class="dettaglio-tab">
    <tr>
      <td class="etichetta-dato"> Tipo documento</td>
      <td class="valore-dato"> <c:out value="${modelliForm.tipoModello}" /> </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Nome</td>
      <td class="valore-dato"> <c:out value="${modelliForm.nomeModello}" /> </td>
    </tr>
	<tr>
      <td class="etichetta-dato" >Descrizione</td>
      <td class="valore-dato"> <c:out value="${modelliForm.descrModello}" /> </td>
    </tr>
	<tr>
      <td class="etichetta-dato" >File</td>
      <td class="valore-dato"> <a href="javascript:mostraModello(${modelliForm.idModello},'${modelliForm.nomeFile}');" title="Download modello"><c:out value="${modelliForm.nomeFile}" /></a> </td>
    </tr>
	<tr>
	<tr>
		<td class="etichetta-dato">Componi in formato PDF</td>
		<td class="valore-dato"><c:choose>
				<c:when test="${modelliForm.pdf}">
					<c:out value="Si" />
				</c:when>
				<c:otherwise>
					<c:out value="No" />
				</c:otherwise>
			</c:choose></td>
	</tr>
	<td class="etichetta-dato" >Modello riepilogativo ?</td>
      <td class="valore-dato">
      	<c:choose>
					<c:when test="${modelliForm.riepilogativo}">
						<c:out value="Si" />
					</c:when>
					<c:otherwise>
						<c:out value="No" />
					</c:otherwise> 
				</c:choose>
     </td>
  </tr>
  <c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>		
    <tr>
      <td class="etichetta-dato" >Modello personale</td>
		  <td class="valore-dato">
				<c:choose>
					<c:when test="${modelliForm.personale}">
						<c:out value="Si" />
					</c:when>
					<c:otherwise>
						<c:out value="No" />
					</c:otherwise> 
				</c:choose>
		  </td>
		</tr>
	</c:if>  
    <c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
		<tr>
	    <td class="etichetta-dato" >Mostra in Modelli predisposti</td>
	    <td class="valore-dato"> 
			<c:choose>
				<c:when test="${modelliForm.disponibile}">
					<c:out value="Si" />
				</c:when>
				<c:otherwise>
					<c:out value="No" />
				</c:otherwise> 
			</c:choose>
		  </td>
	  </tr>
	</c:if>
	<tr>
    <td class="etichetta-dato" >Schema principale</td>
    <td class="valore-dato"> <c:if test="${!empty (modelliForm.entPrinc)}"><c:out value="${modelliForm.schemaPrinc} - ${modelliForm.descSchemaPrinc}" /></c:if></td>
  </tr>
	<tr>
    <td class="etichetta-dato" >Argomento principale</td>
    <td class="valore-dato"> <c:if test="${!empty (modelliForm.entPrinc)}"><c:out value="${modelliForm.mneEntPrinc} - ${modelliForm.descEntPrinc}" /></c:if></td>
  </tr>
	<tr>
      <td class="comandi-dettaglio" colSpan="2">
        <INPUT type="button" class="bottone-azione" value="Modifica" title="Modifica modello" onclick="javascript:modifica('<c:out value='${modelliForm.idModello}' />')">
        &nbsp;
      </td>
    </tr>
</table>
