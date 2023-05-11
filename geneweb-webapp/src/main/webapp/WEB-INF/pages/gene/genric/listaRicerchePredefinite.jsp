<%
/*
 * Created on 21-set-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI LISTA  
 // CONTENENTE LA EFFETTIVA LISTA DELLE RICERCHE PREDEFINITE E LE RELATIVE FUNZIONALITA' 
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://www.owasp.org/index.php/Category:OWASP_CSRFGuard_Project/Owasp.CsrfGuard.tld" prefix="csrf" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="token"><csrf:token/></c:set>

<c:if test="${!empty (querySql)}">
	<jsp:include page="/WEB-INF/pages/gene/genric/debugRisultatiRicerca.jsp" />
</c:if>

<table class="lista">
	<tr>
		<td>
			<display:table name="listaRicerchePredefinite" id="ricercaForm" class="datilista"  requestURI="ListaRicerchePredefinite.do?${token}" pagesize="25" sort="list">
			<c:if test='${empty applicationScope.configurazioneChiusa || applicationScope.configurazioneChiusa eq "0"}'>
				<display:column style="width:20px">  
					<c:if test="${ricercaForm.personale == 'true'}">
						<center><img src='${contextPath}/img/personal.gif' height='16' width='16' alt='Report personale' title="Report personale"></center>
					</c:if>
				</display:column>
			</c:if>
				<display:column property="nomeRicerca" title="Titolo" href="EseguiRicercaPredefinita.do?${token}&metodo=caricaPerEstrazione&" paramId="idRicerca"  
								paramProperty="idRicerca" sortable="true" headerClass="sortable" >  </display:column>
				<display:column property="descrRicerca" title="Descrizione" sortable="true" headerClass="sortable" >  </display:column>
			</display:table>
		</td>
	</tr>
</table>