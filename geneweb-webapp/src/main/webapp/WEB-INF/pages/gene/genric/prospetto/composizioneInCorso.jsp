<%/*
   * Created on 19-mar-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA TEMPORANEA CHE INDICA L'ELABORAZIONE DEL MODELLO IN CORSO PER LA
  // GENERAZIONE DEL PROSPETTO ASSOCIATO AD UNA RICERCA CON PROSPETTO
%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<table class="lista">
	<tr>
		<td align="center">
		<img id="progressImage" src="${contextPath}/img/${applicationScope.pathImg}progressbar.gif" alt="progress bar" title="progress bar"/>
		<br>
		<br>
		Composizione '${componiModelloConIdUtenteForm.nomeModello}' in corso...
		</td>
	</tr>
</table>

<form action="${contextPath}/geneGenric/ComponiProspetto.do" method="post" name="parametriProspettoForm">
	<input type="hidden" name="metodo" value="componiModello"/> 
	<input type="hidden" name="idSessione" value="${componiModelloConIdUtenteForm.idSessione}"/>
	<input type="hidden" name="idModello" value="${componiModelloConIdUtenteForm.idModello}"/>
  <input type="hidden" name="idProspetto" value="${componiModelloConIdUtenteForm.idProspetto}"/>
	<input type="hidden" name="nomeModello" value="${componiModelloConIdUtenteForm.nomeModello}"/> 
	<input type="hidden" name="entita" value="${componiModelloConIdUtenteForm.entita}"/> 
	<input type="hidden" name="nomeChiavi" value="${componiModelloConIdUtenteForm.nomeChiavi}"/> 
	<input type="hidden" name="valori" value="${componiModelloConIdUtenteForm.valori}"/>
	<input type="hidden" name="fileComposto" value="${componiModelloConIdUtenteForm.fileComposto}"/>
	<input type="hidden" name="noFiltroEntitaPrincipale" value="${componiModelloConIdUtenteForm.noFiltroEntitaPrincipale}"/>
	<input type="hidden" name="paginaSorgente" value="${componiModelloConIdUtenteForm.paginaSorgente}"/>
	<c:forEach items="${componiModelloConIdUtenteForm.valChiavi}" var="chiave">
	<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
<form>