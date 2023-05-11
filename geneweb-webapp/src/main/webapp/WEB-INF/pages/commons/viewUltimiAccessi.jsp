<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista dei tecnici progettisti */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>


<c:choose>
    <c:when test="${! empty profiloUtente}">
       <c:set var="filtroUtente" value="SYSCON = ${profiloUtente.id} AND (CODEVENTO = 'LOGIN' OR CODEVENTO = 'LOGOUT' OR CODEVENTO = 'LOGIN_LOCK' CODEVENTO = 'LOGIN_UNLOCK') AND DATAORA > CURRENT_DATE - '1 month'::interval"/>
    </c:when>    
    <c:otherwise>
        <c:set var="filtroUtente" value="1=2"/>
    </c:otherwise>
</c:choose>

<gene:template file="lista-template.jsp" gestisciProtezioni="false">
	<gene:setString name="titoloMaschera" value="Accessi negli ultimi 30 giorni"/>
	
	<gene:redefineInsert name="azioniContesto">
	<div 	id="menulaterale" class="menulaterale" 
			onMouseover="highlightSubmenuLaterale(event,'on');"
			onMouseout="highlightSubmenuLaterale(event,'off');">
<table>
	<tbody>	
	<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
		<gene:insert name="addAzioniContestoBottom" />
	</tbody>
</table>
</div>
	</gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr>
			<td>
				<display:table name="${ultimiAccessi}" defaultsort="-1" id="ultimiAccessi" class="datilista" sort="list">
						<display:column property="id" title="Nr. evento" sortable="false" headerClass="sortable"></display:column>
						<display:column property="data" title="Data e ora" sortable="false" headerClass="sortable" decorator="it.eldasoft.gene.commons.web.displaytag.DataOraDecorator"></display:column>
						<display:column property="oggEvento" title="Livello evento" sortable="false" headerClass="sortable"></display:column>
						<display:column property="codEvento" title="Codice evento" sortable="false" headerClass="sortable"></display:column>
						<display:column property="ip" title="Ip evento" sortable="false" headerClass="sortable"></display:column>
						<display:column property="descr" title="Descrizione evento" sortable="false" headerClass="sortable"></display:column>
				</display:table>
			</td>
		</tr>
	</table>
  </gene:redefineInsert>
  
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript"
	src="${contextPath}/js/general.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/navbarMenu.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/floatingMenu.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/popupMenu.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery-3.6.1.min.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery-migrate-3.4.0.min.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/jquery-ui-1.13.2.js"></script>
<script type="text/javascript"
	src="${pageContext.request.contextPath}/js/jquery.ui.datepicker-it.js"></script>
<script type="text/javascript"
	src="${contextPath}/js/custom.js"></script>
  
  <script type="text/javascript">
  $(document).ready(function(){
	$("td:contains('ACCESSO_SIMULTANEO')").closest(".odd").css( "background-color", "#e9e443" );	  
	$("td:contains('ACCESSO_SIMULTANEO')").closest(".even").css( "background-color", "#f1ee8e" );
});
  </script>
</gene:template>
