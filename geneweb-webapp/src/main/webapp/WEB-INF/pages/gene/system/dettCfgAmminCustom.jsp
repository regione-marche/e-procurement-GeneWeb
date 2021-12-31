<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" prefix="csrf" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<%-- ESEMPIO DI AGGIUNTA DI UNA VOCE AD UNA SEZIONE SPECIFICA
	$(function() {
				$("#configurazione").append(
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/cais/cais-lista.jsp&filtroArchiviata=2';\">\n"+
"					<img alt=\"Categorie d'iscrizione\" src=\"${contextPath}/img/Content-33.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=gene/cais/cais-lista.jsp&filtroArchiviata=2';\">Categorie d'iscrizione</a>\n"+
"					</b>\n"+
"				</p>\n"
				);
			});
--%>


<%-- ESEMPIO DI AGGIUNTA DI UNA NUOVA SEZIONE SPECIFICA IN CODA A QUELLE ESISTENTI
	$(function() {
		$("#accordion").append(
"			<h2 id=\"tit-ult-parametri\" style=\"height: 25px; padding-top: 10px; padding-left: 30px;\">Ulteriori parametri</h2>\n" +
"			<div id=\"ult-parametri\" class=\"sez-accordion\">\n" +
"				<p>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_config/dettConfig.jsp&detail=Pagina2';\">\n"+
"					<img alt=\"Parametri 2\" src=\"${contextPath}/img/Status-30.png\"></a>\n"+
"					&nbsp;&nbsp;&nbsp;\n"+
"					<b>\n"+
"					<a class=\"link-generico\" href=\"javascript:document.location.href='${contextPath}/ApriPagina.do?<csrf:token/>&href=geneweb/w_config/dettConfig.jsp&detail=Pagina2';\">Parametri 2</a>\n"+
"					</b>\n"+
"				</p>\n" +
"			</div>\n"
				);
			});
--%>
