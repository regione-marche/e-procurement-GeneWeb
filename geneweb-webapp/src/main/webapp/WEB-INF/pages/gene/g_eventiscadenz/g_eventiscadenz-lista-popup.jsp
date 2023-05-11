<%--
/*
 * Created on: 07-mag-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 Lista in popup di selezione di un evento associabile ad un'attivita' di scadenzario

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereG_EVENTISCADENZFunction" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_EVENTISCADENZ-lista-popup">
	<gene:setString name="titoloMaschera" value="Selezione dell'evento per attività scadenzario"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="G_EVENTISCADENZ" inserisciDaArchivio="false" sortColumn="3" gestisciProtezioni="true" where="PRG='${moduloAttivo}'">
			<%-- Aggiungo gli item al menu contestuale di riga --%>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<%-- Campi della lista --%>
			<gene:campoLista campo="COD" headerClass="sortable" width="60" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="TIT" headerClass="sortable" width="120"/>
			<gene:campoLista campo="DESCR" headerClass="sortable" width="120"/>
			<gene:campoLista campo="NOTE" headerClass="sortable" width="120"/>
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
