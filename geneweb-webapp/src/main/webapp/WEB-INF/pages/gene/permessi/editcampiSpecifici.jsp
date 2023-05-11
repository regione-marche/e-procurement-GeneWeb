<%
/*
 * Created on 23-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLE COLONNE AGGIUNTIVE DELLA LISTA
 // IN EDIT DELLA G_PERMESSI.
 // QUESTA PAGINA DOVRA' ESSERE RIDEFINITA NEI PROGETTI CHE RICHIEDERANNO UNA
 // PERSONALIZZAZIONE DELL'EDIT DEI DATI DELLA LISTA DEI PERMESSI.
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>


<display:column title="Ruolo" headerClass="nascosto" class="nascosto" >
	<input type="hidden" name="ruolo"  id="ruolo${permessoEntitaForm_rowNum - 1}" value="${permessoEntitaForm.ruolo}"/>
</display:column>