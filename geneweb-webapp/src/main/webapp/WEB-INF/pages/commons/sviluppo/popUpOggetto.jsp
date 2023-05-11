<%
			/*
       * Created on: 17.44 26/10/2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
      /*
				Descrizione:
					Gestione dell'oggetto
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<c:if test='${param.metodo eq "nuovo"}' >
	<gene:sqlSelect nome="outSql" parametri="" tipoOut="VectorString" >
		select max(ord) from W_OGGETTI
	</gene:sqlSelect>
	<c:set var="ord" value="${outSql[0]}"/>
	<c:if test="${empty ord}">
		<c:set var="ord" value="0"/>
	</c:if>
	<c:set var="ord" value="${(ord + 10)-(ord mod 10)}"/>
</c:if>

<gene:template file="popup-template.jsp">
	
	<gene:setString name="titoloMaschera" value="Gestione OGGETTO: ${param.tipo}.${param.id}"/>
	<gene:redefineInsert name="corpo">
		<c:set var="key" value="W_OGGETTI.TIPO=T:${param.tipo};W_OGGETTI.OGGETTO=T:${param.id}" scope="request" />
		<gene:formScheda entita="W_OGGETTI" >
			<input type="hidden" name="tipo" value="${param.tipo}" />
			<input type="hidden" name="id" value="${param.id}" />
			<gene:campoScheda campo="TIPO" modificabile="false" value="${param.tipo}"/>
			<gene:campoScheda campo="OGGETTO" modificabile="false" defaultValue="${param.id}"/>
			<gene:campoScheda campo="DESCR" />
			<gene:campoScheda campo="ORD" defaultValue="${ord}" />
			<gene:campoScheda>
				<c:if test='${param.metodo eq "nuovo"}' ><gene:redefineInsert name="pulsanteAnnulla"></gene:redefineInsert></c:if>
				<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</gene:formScheda>
  </gene:redefineInsert>
	<gene:redefineInsert name="debugDefault" />
	<gene:redefineInsert name="gestioneHistory" />
	<gene:javaScript>
		<c:if test='${empty datiRiga.W_OGGETTI_OGGETTO}'>
			schedaModifica=schedaNuovo;
		</c:if>
		document.forms[0].jspPathTo.value=document.forms[0].jspPath.value;
		
		var schedaConfermaOld=schedaConferma;
		schedaConferma=function(){
			opener.updateDescr("${param.tipo}","${param.id}",getValue("W_OGGETTI_DESCR"));
			schedaConfermaOld();
			//window.close();
		}
	</gene:javaScript>
</gene:template>
