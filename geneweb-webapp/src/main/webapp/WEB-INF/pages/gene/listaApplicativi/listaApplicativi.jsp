<%
	/*
	 * Created on 15-feb-2010
	 *
	 * Copyright (c) EldaSoft S.p.A.
	 * Tutti i diritti sono riservati.
	 *
	 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
	 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
	 * aver prima formalizzato un accordo specifico con EldaSoft.
	 */
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<HTML>
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript">
	function accediApplicativoAlice(url, profilo) {
		// l'applicativo va aperto in un browser completo di tutto
		var href = "${contextPath}/LoginAlice.do?url=" + url;
		if (profilo != null)
			href += "&profilo=" + profilo;
		window.open(href, "", "toolbar=yes,menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes,directories=yes");
		window.close();
	}
</script>

</HEAD>
<BODY>

<div style="width:97%">

<div class="titolomaschera">Accedi ad altro applicativo</div>

<div class="contenitore-dettaglio">

	<table class="dettaglio-home" style="width: 100%">
	
		<tr>
				<td width="10">
					&nbsp;
				</td>
		  	<td height="100" width="75" valign="middle">
		  		<center><img alt="Applicativi" src="${contextPath}/img/logoProfilo.gif"></center>
		  	</td>
		  	<td height="100" valign="middle">
		  		La lista elenca gli applicativi Maggioli ai quali è possibile accedere direttamente utilizzando le credenziali dell'utente <b>${sessionScope.profiloUtente.nome}</b> già connesso.
		  		<br><br>Selezionare l'applicativo di interesse.
		  	</td>
		</tr>
	
		<c:forEach items="${listaApplicativiProfili}" var="applicativo">
			<c:choose>
				<c:when test="${applicativo[0] == 'T'}">
					<tr>
						<td colspan="3" class="voce">
							<br>
							<b>${applicativo[1]}</b><br>
						</td>

					</tr>
				</c:when>
				<c:when test="${applicativo[0] == 'P'}">
					<tr>
						<td>
							&nbsp;
						</td>
						<td colspan="2" style="BORDER-BOTTOM: 1px dotted; PADDING: 10px">
							<a class="link-generico" href="javascript:accediApplicativoAlice('${applicativo[3]}', '${applicativo[4]}');" >${applicativo[1]}</a>
							<br><br>
							${applicativo[2]}<br>
						</td>
					</tr>
				</c:when>
				<c:when test="${applicativo[0] == 'I'}">
					<tr>
						<td colspan="3" class="voce" style="border: 0">
							<br>
							<b><a class="link-generico" href="javascript:accediApplicativoAlice('${applicativo[3]}');" >${applicativo[1]}</a></b><br>			
						</td>
					</tr>
				</c:when>
			</c:choose>

		</c:forEach>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="3">
				<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:window.close()">&nbsp;
			</td>
		</tr>
	</table>
</div>
</div>
</BODY>
</HTML>
	




