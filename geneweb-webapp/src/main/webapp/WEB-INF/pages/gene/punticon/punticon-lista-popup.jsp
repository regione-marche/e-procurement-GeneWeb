<%
/*
 * Created on: 24-04-2014
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione dei punti di contatto */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:template file="popup-template.jsp" >
	<gene:setString name="titoloMaschera" value="Selezione punto di contatto"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="PUNTICON" sortColumn="2" inserisciDaArchivio='false' >
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>
			<gene:campoLista campo="CODEIN" headerClass="sortable"  visibile="false"/>
			<gene:campoLista campo="NUMPUN" headerClass="sortable"  visibile="false"/>
			<gene:campoLista campo="NOMPUN" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
