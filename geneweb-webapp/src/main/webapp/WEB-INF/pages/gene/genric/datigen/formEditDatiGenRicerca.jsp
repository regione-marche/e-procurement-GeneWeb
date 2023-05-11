<%
/*
 * Created on 28-ago-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI TROVA RICERCHE
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<html:form action="/SalvaDatiGen" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Tipo report</td>
      <td class="valore-dato">
      	<html:select property="tipoRicerca">
	      	<html:options collection="listaTipoRicerca" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Titolo (*)</td>
      <td class="valore-dato"> 
      	<html:text property="nome" styleId="nome" size="50" maxlength="50"/>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato" >Descrizione</td>
      <td class="valore-dato"> 
	      <html:text property="descrizione" styleId="descrizione" size="80" maxlength="200" /> 
      </td>
    </tr>
    <c:choose>
	    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
		    <tr>
		      <td class="etichetta-dato" >Report personale</td>
		      <td class="valore-dato"> 
			      <html:checkbox property="personale" />
		      </td>
		    </tr>
    	</c:when>
	    <c:otherwise>
		    <html:hidden property="personale" value="1"/>
		</c:otherwise>
	</c:choose>
	<c:choose>
	  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#") && fn:contains(listaOpzioniDisponibili, "OP119#")}'>
		    <tr>
		      <td class="etichetta-dato" >Pubblica il report come servizio con codice</td>
		      <td class="valore-dato">
		      	<html:text property="codReportWS" styleId="codReportWS" size="30" maxlength="30" onchange="if (this.value.length>0) this.value=this.value.toUpperCase();"/> 
		      </td>
		    </tr>
	  </c:when>
		<c:otherwise>
		    <html:hidden property="codReportWS"/>
		</c:otherwise>
	</c:choose>
	
  <!-- F.D. se c'è l'ou48 è giusto dare la possibilità di scegliere 
 se invece non c'è l'ou48 vuol dire che se si è qui c'è l'ou49 quindi non si visualizza-->
  <c:choose>
	    <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
		    <tr>
		      <td class="etichetta-dato" >Mostra nel menù Report</td>
		      <td class="valore-dato"> 
			      <html:checkbox property="disp" />
		      </td>
		    </tr>
		</c:when>
	    <c:otherwise>
		    <html:hidden property="disp" value="1"/>
		</c:otherwise>
	</c:choose>		    
	  <tr>
    	<td class="etichetta-dato" >Risultati per pagina</td>
    	<td class="valore-dato">
	      	<html:select property="risPerPag">
	      	<html:options name="listaRisPerPagina" />
				</html:select>
			</td>
		</tr>
<% /*
			Spiegazione del test seguente:
		  ou49 = solo report personali
		  ou53 = report base
		  ou54 = report avanzato
    */ %>
<c:choose>
	<c:when test='${!(fn:contains(listaOpzioniUtenteAbilitate, "ou49") and 
							      fn:contains(listaOpzioniUtenteAbilitate, "ou53") and 
						       !fn:contains(listaOpzioniUtenteAbilitate, "ou54")) and 
						       testataRicercaForm.famiglia ne 4}'>
    <tr>
      <td class="etichetta-dato" >Abilita modelli predisposti</td>
      <td class="valore-dato">
				<html:checkbox property="visModelli" />
      </td>
    </tr>
	</c:when>
	<c:otherwise>
		<html:hidden property="visModelli" />
	</c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${testataRicercaForm.famiglia eq 1 or testataRicercaForm.famiglia eq 4}'>
  <tr>
    <td class="etichetta-dato" >Stampa parametri nel risultato</td>
    <td class="valore-dato"> 
				<html:checkbox property="visParametri" />
    </td>
  </tr>
  </c:when>
  <c:otherwise>
		<html:hidden property="visParametri" />
  </c:otherwise>
</c:choose>
<c:choose>
	<c:when test='${moduloAttivo ne "W0" && (testataRicercaForm.famiglia ne 2 and testataRicercaForm.famiglia ne 4)}'>
  <tr>
    <td class="etichetta-dato" >Visualizza la scheda di dettaglio</td>
    <td class="valore-dato"> 
				<html:checkbox property="linkScheda" />
    </td>
  </tr>
  </c:when>
  <c:otherwise>
		<html:hidden property="linkScheda" />
  </c:otherwise>
</c:choose>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
        <INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:gestisciSubmit();">
		<c:choose>
       <c:when test="${!empty testataRicercaForm.nome}">
 	      <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:cambiaTab('DG');">
       </c:when>
       <c:otherwise>
        <INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annullaModifiche();">
       </c:otherwise>
     </c:choose>
        <input type="hidden" name="metodo" />
        <input type="hidden" name="idProspetto" value="0" />
        <html:hidden property="profiloOwner" />
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>