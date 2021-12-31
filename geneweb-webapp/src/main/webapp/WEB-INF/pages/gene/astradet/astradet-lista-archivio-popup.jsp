<%/*
       * Created on 23-apr-2008
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<gene:template file="popup-template.jsp">
	<c:set var="entita" value="ASTRADET" />
	<gene:setString name="titoloMaschera" value="Lista delle strade"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="${entita}" sortColumn="2" inserisciDaArchivio="false" >
			<!-- Aggiungo gli item al menu contestuale di riga -->
			<gene:campoLista title="${titoloMenu}&nbsp;Opzioni" width="55" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>
			<gene:campoLista campo="CODVIA" headerClass="sortable"  href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="VIAPIA" headerClass="sortable" />
			<gene:campoLista campo="STR_CAT_S" headerClass="sortable" visibile="false" />
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
