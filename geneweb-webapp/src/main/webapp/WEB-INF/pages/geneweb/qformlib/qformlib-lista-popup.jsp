<%
/*
 * Created on: 23/04/2021
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 /* Lista popup di selezione del formulario */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereQFORMLIBFunction" />

<gene:template file="popup-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="lista-qformlib-popup">
	<gene:setString name="titoloMaschera" value="Selezione del modello Q-form"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" entita="QFORMLIB" sortColumn="2" gestisciProtezioni="true" inserisciDaArchivio='false' >
			<% // Aggiungo gli item al menu contestuale di riga %>
			<gene:campoLista title="Opzioni" width="50">
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>
		
			<gene:campoLista campo="CODMODELLO" headerClass="sortable" width="90" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista campo="TITOLO" headerClass="sortable" />
			<gene:campoLista campo="TIPLAV" headerClass="sortable" width="120"/>
			<gene:campoLista campo="DAIMPORTO" headerClass="sortable" width="120"/>
			<gene:campoLista campo="AIMPORTO" headerClass="sortable" width="120"/>
			<gene:campoLista campo="DESCRIZIONE" visibile="false"/>
			<gene:campoLista campo="ID" visibile="false"/>
			<gene:campoLista campo="TIPOLOGIA" visibile="false"/>
			<gene:campoLista campo="DULTAGG" visibile="false"/>
		</gene:formLista>
  </gene:redefineInsert>
  <gene:javaScript>
	function selezioneCampo(datiArchivio){
		archivioSeleziona(datiArchivio);
		eval("window.opener.document.forms[0].datiArchivioImpostati").onchange();
		 
	}
	
	function archivioSeleziona(arrayValori){
		var element;
		var close=true;
		try{
			parentForm=eval('window.opener.activeForm');
		}catch(e){
			outMsg(e.message);
			close=false;
		}
		//lForm=getObjectById('archivioReq');
		// {M.F. 20/11/2006} I campi di collegamento all'archivio sono stati inglobati nella lista
		lForm=document.forms[0];
		lArray=lForm.archCampi.value.split(";");
		for(i=0;i<lArray.length;i++){
			element=parentForm.getCampo(lArray[i]);
			if(element!=null){
				parentForm.setValue(lArray[i],arrayValori[i]);
			}else{
				outMsg("Non esiste la colonna "+lArray[i]+" nella pagina chiamante !");
				close=false;
			}
		}
		if(close){
			if ($.isFunction(eval("window.opener").aggiornaCampiNacostiDaArchivio)) {
        		eval("window.opener").aggiornaCampiNacostiDaArchivio();
    		}
			window.close();
		}
	}
	
	
</gene:javaScript>
</gene:template>

