<%
			/*
       * Created on: 14.22 14/03/2007
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
					Lista degli archivi istat dei comuni
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.archWhereFunctions.ComponiWhereTABSCHEFunction" />

<gene:template file="popup-template.jsp">
	<c:set var="entita" value="TABSCHE" />
	<gene:setString name="titoloMaschera" value="Seleziona comune"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" 
				entita="TABSCHE" sortColumn="2" inserisciDaArchivio="false" >
			<gene:formSelect>
				SELECT TABSCHE.TABDESC,   
         TABSCHE.TABCOD3  ,
					TABSCHE.TABCOD2
			    FROM TABSCHE  
			   WHERE ( TABSCHE.TABCOD = 'S2003' ) AND  
			         ( TABSCHE.TABCOD1 = '07' )   
				ORDER BY TABSCHE.TABDESC ASC
			</gene:formSelect>
			<!-- Aggiungo gli item al menu contestuale di riga -->
			<gene:campoLista title="${titoloMenu}&nbsp;Opzioni" width="55" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<% // Campi della lista %>
			<gene:campoLista title="Provincia" entita="TABSCHE" campo="TABDESC" definizione="T120" headerClass="sortable" href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista title="Sigla" entita="TABSCHE" campo="TABCOD3" definizione="T9;1" headerClass="sortable" visibile="false"/>
			<gene:campoLista title="Codice ISTAT" entita="TABSCHE" campo="TABCOD2" definizione="T5" headerClass="sortable" />
		</gene:formLista>
  </gene:redefineInsert>
</gene:template>
