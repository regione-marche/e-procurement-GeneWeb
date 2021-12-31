<%
/*
 * Created on 20-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT DEL
 // DETTAGLIO DEI DATI GENERALI DI UN ACCOUNT CONTENENTE LA SEZIONE RELATIVA
 // ALLE SOTTOSEZIONI DELLA PAGINA STESSA.
 // QUESTA PAGINA DOVRA' ESSERE RIDEFINITA NEI PROGETTI CHE RICHIEDERANNO UNA
 // PERSONALIZZAZIONE DELL'EDIT DEI DATI GENERALI DELL'ACCOUNT.
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="account" value="${requestScope.accountForm}"/>

<% 
// Valori di default dei campi SYSLIV, SYSAB3, SYSLIG, SYSABG, SYSLIC e SYSABC
// della tabella USRSYS
%>
<c:if test='${not empty account.livelloLavori}'>
	<input type="hidden" name="livelloLavori" value="${account.livelloLavori}"/>
</c:if>
<c:if test='${not empty account.abilitazioneLavori}'>
	<input type="hidden" name="abilitazioneLavori" value="${account.abilitazioneLavori}"/>
</c:if>
<c:if test='${not empty account.livelloGare}'>
	<input type="hidden" name="livelloGare" value="${account.livelloGare}"/>
</c:if>
<c:if test='${not empty account.abilitazioneGare}'>
	<input type="hidden" name="abilitazioneGare" value="${account.abilitazioneGare}"/>
</c:if>
<c:if test='${not empty account.livelloContratti}'>
	<input type="hidden" name="livelloContratti" value="${account.livelloContratti}"/>
</c:if>
<c:if test='${not empty account.abilitazioneContratti}'>
	<input type="hidden" name="abilitazioneContratti" value="${account.abilitazioneContratti}"/>
</c:if>
<c:if test='${not empty account.ruoloUtenteMercatoElettronico}'>
	<input type="hidden" name="ruoloUtenteMercatoElettronico" value="${account.ruoloUtenteMercatoElettronico}"/>
</c:if>

<script type="text/javascript">
<!--
// funzione richiamata per eseguire i controlli di obbligatorietà sulla sezione.
// aggiungere eventuali controlli di obbligatorietà del caso 
function eseguiControlliSezioneCustomSalva() {
	return true;
}
-->
</script>