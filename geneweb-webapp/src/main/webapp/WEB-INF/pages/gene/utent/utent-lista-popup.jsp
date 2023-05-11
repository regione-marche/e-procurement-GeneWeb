<%/*
       * Created on 28-Apr-20086
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="filtroLivelloUtente"
	value='${gene:callFunction2("it.eldasoft.gene.tags.utils.functions.FiltroLivelloUtenteFunction", pageContext, "UTENT")}' />
<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Lista soggetti"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista entita="UTENT" where="${filtroLivelloUtente}" pagesize="20" tableclass="datilista" inserisciDaArchivio="true" sortColumn="3;4">
			<!-- Aggiungo gli item al menu contestuale di riga -->
			<gene:campoLista title="${titoloMenu}&nbsp;Opzioni" width="55" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
					<gene:PopUpItem title="Visualizza scheda" href="javascript:listaVisualizza();"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista degli utenti %>
			<gene:campoLista campo="CODUTE" visibile="false" />
			<gene:campoLista campo="COGUTE" headerClass="sortable" />
			<gene:campoLista campo="NOMEUTE" headerClass="sortable" />
			<gene:campoLista campo="NOMUTE" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="CFUTE" headerClass="sortable"/>
			<gene:campoLista campo="PIVAUTE" headerClass="sortable"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
