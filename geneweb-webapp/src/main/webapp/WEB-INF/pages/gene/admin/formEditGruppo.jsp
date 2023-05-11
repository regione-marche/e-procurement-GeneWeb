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
// CONTENENTE I DATI DI UN GRUPPO E LE RELATIVE FUNZIONALITA' IN FASE DI
// EDITING DEL DETTAGLIO STESSO
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<html:form action="/GruppoDispatch" >
	<table class="dettaglio-tab">
	    <tr>
	      <td class="etichetta-dato">Nome (*)</td>
	      <td class="valore-dato"> 
	      	<html:text property="nomeGruppo" styleClass="testo" size="20" maxlength="20" />
	      </td>
	    </tr>	
	    <tr>
	      <td class="etichetta-dato" >Descrizione</td> 
	      <td class="valore-dato">
	      	<html:text property="descrizione" styleClass="testo" size="50" maxlength="48" />
				</td>
	    </tr>

	    <tr>
	      <td class="comandi-dettaglio" colSpan="2"> 
		      <!-- idGruppo e' un parametro hidden letto dal request e -->
	      	<html:hidden property="idGruppo" />
	      	<html:hidden property="metodo" value="updateGruppo" />
				<c:if test='${gruppoForm.idGruppo != "" }' >
	      	<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:gestisciSubmit();" >
	      </c:if>
				<c:if test='${gruppoForm.idGruppo == "" }' >
	      	<INPUT type="button" class="bottone-azione" value="Salva" title="Salva gruppo" onclick="javascript:gestisciSubmit();" >
	      </c:if>
	      <!-- Il pulsante Annulla è presente solo nella fase di modifica di un gruppo e non nella fase creazione di un nuovo gruppo -->
				<c:if test='${gruppoForm.idGruppo != "" }' >
	      	<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaModifica('<c:out value='${gruppoForm.idGruppo}' />');" >
	      </c:if>
	        &nbsp;
	      </td>
	    </tr>
	</table>
</html:form>