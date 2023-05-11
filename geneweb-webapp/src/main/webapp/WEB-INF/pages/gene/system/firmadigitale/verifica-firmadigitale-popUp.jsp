<%
/*
 * Created on: 16/06/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="digitalSignatureUrlCheck" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-check-url")}'/>
<c:set var="digitalSignatureProvider" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "digital-signature-provider")}'/>


<gene:template file="popup-template.jsp" >
	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	
	<c:choose>
		<c:when test="${!empty digitalSignatureUrlCheck && !empty digitalSignatureProvider && (digitalSignatureProvider eq 1 || digitalSignatureProvider eq 2)}">
			<jsp:include page="verifica-firmadigitale-interno-maggioli.jsp">
				<jsp:param name="jspParent" value="popUp"/>
			</jsp:include>
		</c:when>
		<c:otherwise>
			<jsp:include page="verifica-firmadigitale-interno.jsp">
				<jsp:param name="jspParent" value="popUp"/>
			</jsp:include>
		</c:otherwise>
	</c:choose>

	

</gene:template>

