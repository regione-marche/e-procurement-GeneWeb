<%
/*
 * Created on 20-set-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // AREA SOPRA IL TITOLO E CONTENENTE LO STORICO NAVIGAZIONE ED ALCUNE INFO/FUNZIONI UTENTE
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="isNavigazioneDisattiva" value="${isNavigazioneDisabilitata}" />
<c:set var="connessioniAttive" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetCountSessioniAttiveUtenteFunction",pageContext)}' />

		<div class="over-hidden">
			<div class="breadcrumbs">
			<c:if test="${empty param.hideHistory}">
				<gene:history />
			</c:if>
			</div>
			<div class="info-utente">
				<c:if test='${! empty profiloUtente}'>Benvenuto <b>${profiloUtente.nome}</b> 
				<c:if test="${empty param.hideOpzioni && ((empty isNavigazioneDisattiva) or (isNavigazioneDisattiva ne '1')) && (! empty profiloAttivo)}">
				<A id="lUtente" href="javascript:showMenuPopup('lUtente',generaPopupOpzioniUtenteLoggato());">
				<IMG src="${pageContext.request.contextPath}/img/arrow-down.gif" alt="Opzioni" title="Opzioni"></A>
				</c:if>
				<c:if test='${connessioniAttive > 1}'>(Connessioni attive: ${connessioniAttive})</c:if>
				<c:if test='${ipConnessioniUtente.size() > 1}'>
				<IMG src="${pageContext.request.contextPath}/img/conf_allert.png" alt="Alert" title="${ipConnessioniUtente}" width="20px">
				</c:if>
				| <A id="idUtLogEsci" href="javascript:utLogEsci();" tabindex="1500" 
				  class="link-generico">Esci</A></c:if>
			  </div>
		</div>

		<c:if test="${! empty sessionScope.msgAdmin}">
		<div class="msg-admin">
			<c:out value="${sessionScope.msgAdmin}" />
		</div>
		</c:if>
				