<%
/*
 * Created on: 05-nov-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<% // sezione di codice per disabilitare i comandi per entrare in modifica dei dati %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, param.entita)}' />
<c:set var="autorizzatoModifiche" value="1" scope="request" />

<c:if test='${!empty (filtroLivelloUtente)}'>
	<gene:sqlSelect nome="autori" parametri="${param.inputFiltro}" tipoOut="VectorString" >
		select autori from g_permessi where ${param.filtroCampoEntita} and g_permessi.syscon = ${profiloUtente.id}
	</gene:sqlSelect>
	<c:if test='${!empty autori}'>
		<c:set var="autorizzatoModifiche" value="${autori[0]}" scope="request" />
	</c:if>
	
	<% // se l'utente non è amministratore e il lavoro non è modificabile, allora blocco l'accesso alla modifica %>
	<c:if test='${autorizzatoModifiche eq "2"}'>
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaInserisci"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
	</c:if>
</c:if>
