<%/*
       * Created on 13-giu-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA PARTE STANDARD DELL'HEAD HTML
      %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" prefix="csrf" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaParametrizzata">
	<fmt:message key="label.tags.uffint.singolo" />
</c:set>

<c:choose>
	<c:when test="${not empty param.title}">
		<TITLE>${param.title}</TITLE>
	</c:when>
	<c:otherwise>
		<TITLE>${applicationScope.appTitle}</TITLE>
	</c:otherwise>
</c:choose>

<meta http-equiv="X-UA-Compatible" content="IE=Edge"> <%-- per forzare ultima versione di Internet Explorer --%>
<META http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<META http-equiv="Cache-Control" content="no-cache">
<META http-equiv="Pragma" content="no-cache">
<META http-equiv="Expires" content="-1">
<link rel="shortcut icon"
	href="${contextPath}/img/favicon.ico">
<c:choose>
<c:when test="${!empty (applicationScope.pathCss)}">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/jquery/ui/${applicationScope.pathCss}jquery-ui.css">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/${applicationScope.pathCss}elda.css">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/${applicationScope.pathCss}elda-custom.css">
</c:when>
<c:otherwise>
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/jquery/ui/std/jquery-ui.css">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/std/elda.css">
<link rel="STYLESHEET" type="text/css"
	href="${contextPath}/css/std/elda-custom.css">
</c:otherwise>
</c:choose>
<script type="text/javascript"
	src="${contextPath}/js/general.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/navbarMenu.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/floatingMenu.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/popupMenu.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery-2.2.4.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery-migrate-1.4.1.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery-ui-1.12.1.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery.ui.datepicker-it.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/custom.js"></script>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/jquery/dataTable/dataTable/jquery.dataTables.css" >
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.dataTables.min.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/textext/js/textext.core.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/textext/js/textext.plugin.arrow.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/textext/js/textext.plugin.autocomplete.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/textext/js/textext.plugin.filter.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/textext/js/textext.plugin.suggestions.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/textext/js/textext.plugin.tags.js"></script>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/textext/css/textext.core.css" >
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/textext/css/textext.plugin.arrow.css" >
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/textext/css/textext.plugin.autocomplete.css" >
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/textext/css/textext.plugin.tags.css" >

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/w_message/css/jquery.w_message.css" >
<script type="text/javascript" src="${pageContext.request.contextPath}/js/w_message/js/jquery.w_message.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.alphanum.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.character.js"></script>

<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.wtags.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.cookie.js"></script>

<c:set var="isMessaggiInterniAbilitatiFunction" value='${gene:callFunction("it.eldasoft.gene.tags.functions.IsMessaggiInterniAbilitatiFunction",  pageContext)}' scope="request"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:if test='${(!empty sessionScope.profiloUtente) && isMessaggiInterniAbilitatiFunction eq "true"}'>

	<c:set var="isInvioMessaggiAbilitato" value="false" />
	<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou11#")}'>
		<c:set var="isInvioMessaggiAbilitato" value="true" />
	</c:if>

	<script type="text/javascript">
		$(window).ready(function (){
			var _contextPath="${pageContext.request.contextPath}";
			mymessage.init([_contextPath]);
			$(".over-hidden").css("height","40px");
			mymessage.creamc($(".info-utente"), "PREPEND", ${isInvioMessaggiAbilitato}, -400, 0);
		});
	</script>
</c:if>

<%-- <c:if test="${modo eq 'NUOVO' || modo eq 'MODIFICA'}"> --%>
	<script type="text/javascript">
		$(window).ready(function (){
			var _formScheda = $("form[action*='Scheda.do']");
			var _formLista = $("form[action*='Lista.do']");
			var _formGeneweb = $("form input[name=jspPath][value*='/WEB-INF/pages/geneweb/']");
			
			if (_formScheda.size() > 0 && _formLista.size() == 0 && _formGeneweb.size() == 0) {
				var _contextPath = "${pageContext.request.contextPath}";
				var _codapp = "${sessionScope.moduloAttivo}";
				var _profilo = "${profiloAttivo}";
				myTags.init(_contextPath, _codapp, _profilo);
				myTags.addTags();
			} 
		});
	</script>	
<%-- </c:if> --%>


<script type="text/javascript">
//<!-- M.F. 20.10.2006 Setto il contextPath utilizzato nei javascript
var contextPath="${contextPath}";

var csrfToken = "<csrf:token/>";

var codProfiloAttivo="${profiloAttivo}";

if (navigator.appName == "Microsoft Internet Explorer" && !ie10) {
  document.write("<link REL='stylesheet' HREF='${contextPath}/css/${applicationScope.pathCss}elda-ie.css' TYPE='text/css'>");
} 

		function generaPopupOpzioniUtenteLoggato() {
			<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}" chiudi="false">

			<c:if test='${profiloUtente.utenteLdap == 0 and gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Mio-account")}'>
				<elda:jsVocePopup functionJS="utLogMioAccount()" descrizione="Il mio account"/>	
			</c:if>
			<c:if test='${(profiloUtente.utenteLdap == 0 && !profiloUtente.autenticazioneSSO) and gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Cambia-password")}'>
				<elda:jsVocePopup functionJS="utLogCambiaPassword()" descrizione="Cambia password"/>	
			</c:if>
		<c:choose>
			<c:when test='${sessionScope.sentinellaCodProfiloUnico eq "1"}'>
				<c:if test='${sentinellaSelezionaUffint eq "1"}'>
					<elda:jsVocePopup functionJS="utLogCambiaUfficioIntestatario()" descrizione="Cambia ${fn:toLowerCase(nomeEntitaParametrizzata)}"/>	
				</c:if>
			</c:when>
			<c:otherwise>
				<c:if test='${sentinellaSelezionaUffint eq "1"}'>
					<elda:jsVocePopup functionJS="utLogCambiaUfficioIntestatario()" descrizione="Cambia ${fn:toLowerCase(nomeEntitaParametrizzata)}"/>	
				</c:if>
				<elda:jsVocePopup functionJS="utLogCambiaProfilo()" descrizione="Cambia profilo"/>	
			</c:otherwise>
		</c:choose>
				
				<c:if test='${sentinellaAccessoAltroApplicativo eq "1"}'>
				<elda:jsVocePopup functionJS="accediAltroApplicativoLista('${contextPath}')" descrizione="Accedi ad altro applicativo"/>
				</c:if>
				
				<elda:jsVocePopup functionJS="utLogEsci()" descrizione="Esci"/>	
			</elda:jsBodyPopup>
			return linkset;
		}
		
		function utLogCambiaPassword() {
			document.location.href="${pageContext.request.contextPath}/geneAdmin/InitCambiaPasswordAdmin.do?<csrf:token/>&metodo=cambioBase&provenienza=menu";
		}
		
		function utLogMioAccount() {
			document.location.href="${pageContext.request.contextPath}/geneAdmin/InitEditMioAccount.do?<csrf:token/>&metodo=modifica";
		}
		
		function utLogCambiaProfilo() {
			document.location.href="${pageContext.request.contextPath}/CheckProfilo.do?<csrf:token/>";
		}
		
		function utLogCambiaUfficioIntestatario() {
			document.location.href="${pageContext.request.contextPath}/CheckUfficioIntestatario.do?<csrf:token/>";
		}
		
		function utLogEsci() {
			if (confirm('Sei sicuro di volerti disconnettere?')) {
				document.location.href="${pageContext.request.contextPath}/Logout.do";
			}
		}
		
//-->
</script>
<script src="${contextPath}/csrfguard"></script>
