<%
/*
 * Created on 07-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
// IN FASE DI CREAZIONE DI UN NUOVO GRUPPO
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<html:form action="/GruppoDispatch">
	<table class="dettaglio-tab">
	    <tr>
	      <td class="etichetta-dato">Nome (*)</td>
	      <td class="valore-dato"> 
	      	<html:text property="nomeGruppo" styleClass="testo" size="20" maxlength="20" />
	      </td>
	    </tr>	
	    <tr>
	      <td class="etichetta-dato">Descrizione</td> <!-- title="Descrizione" sarebbe un doppione -->
	      <td class="valore-dato">
	      	<html:text property="descrizione" styleClass="testo" size="50" maxlength="48" />
				</td>
	    </tr>

	    <tr>
	      <td class="comandi-dettaglio" colSpan="2"> 
		      <!-- idGruppo e' un parametro che viene posto a zero per default -->
	      	<html:hidden property="idGruppo" value="0"/>
	      	<html:hidden property="metodo" value="creaGruppo" />
	      	<INPUT type="button" class="bottone-azione" value="Salva" title="Salva gruppo" onclick="javascript:gestisciSubmit();" >
	        &nbsp;
	      	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla creazione e torna alla lista" onclick="javascript:annulla();" >
	        &nbsp;
	      </td>
	    </tr>
	</table>
</html:form>