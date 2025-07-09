<%/* 
       * Created on 16-set-2012
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE L'INTERA PAGINA PER LA SCELTA DELL'UFFICIO INTESTATARIO
      // CON CUI ACCEDERE ALL'APPLICAZIONE
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="loginMultiEnte" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "loginMultiEnte")}'/>
<c:set var="urlSupportoEsterno" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.GetPropertyPerUffintFunction", "assistenza.urlSupportoEsterno",(loginMultiEnte eq "1"?uffint:""),".","false")}'/>

<gene:template file="generic-template.jsp" gestisciProtezioni="false" schema="GENE">
	<gene:redefineInsert name="preTitolo">
	  <jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp">
		<jsp:param name="hideHistory" value="1" />
	  </jsp:include>
	</gene:redefineInsert>
	<gene:redefineInsert name="corpo">
		<div class="contenitore-arealavoro">
			<div class="titolomaschera">Richiedi supporto tecnico normativo</div>
			<div class="dettaglio-home">
		  	<div>
			  <c:choose>
				<c:when test="${not empty urlSupportoEsterno}">
					<c:if test='${fn:indexOf(urlSupportoEsterno, "://") < 0}' >
						<c:set var="urlSupportoEsterno" value="//${urlSupportoEsterno}"/>
					</c:if>
					Visita il seguente <a href="${urlSupportoEsterno}" class="link-generico" target="_blank" >link</a>
				</c:when>
				<c:otherwise>
					Opzione non disponibile
				</c:otherwise>
			  </c:choose>
		  	</div>
			</div>
			<div class="titolomaschera">Richiedi assistenza o segnala un malfunzionamento</div>
			<div class="dettaglio-home">
				<div>
					Si prega di utilizzare uno dei seguenti canali ufficiali:
					<ul>
						<li>Web: <a href="https://assistenza.maggioli.it" class="link-generico" target="_blank">Assistenza Maggioli</a></li>
					</ul>
					<ul>
						<li>Mail: <a href="mailto:help.eprocurement@maggioli.it" class="link-generico">help.eprocurement@maggioli.it</a></li>
					</ul>
					<ul>
						<li>Centralino: 0422 267770</li>
					</ul>
				</div>
			</div>
		</div>
	</gene:redefineInsert>
</gene:template>