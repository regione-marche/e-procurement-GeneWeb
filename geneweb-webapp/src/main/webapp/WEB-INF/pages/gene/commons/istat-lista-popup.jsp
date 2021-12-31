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

<gene:template file="popup-template.jsp">
	<c:set var="entita" value="TABSCHE" />
	<gene:setString name="titoloMaschera" value="Seleziona comune"/>
	<gene:redefineInsert name="corpo">
		<gene:formLista pagesize="25" tableclass="datilista" 
				entita="TABSCHE" sortColumn="4" inserisciDaArchivio="false" >
			<gene:formSelect>
				select tb1.TABDESC, tb1.TABCOD3 , tabsche.tabdesc , tabsche.tabcod3 , tabsche.tabcod4
					from tabsche, tabsche tb1 
					where tabsche.tabcod='S2003' 
						and tabsche.tabcod1='09' 
						and tb1.tabcod='S2003' 
						and tb1.tabcod1='07' 
						and ${gene:getDBFunction(pageContext,"SUBSTR","tabsche.tabcod3;4;3")} = ${gene:getDBFunction(pageContext,"SUBSTR","tb1.tabcod2;2;3")}
			</gene:formSelect>
			<gene:campoLista title="Opzioni" width="50" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista title="Provincia" entita="TB1" campo="TABDESC" definizione="T120" headerClass="sortable"  href="javascript:archivioSeleziona(${datiArchivioArrayJs});" />
			<gene:campoLista title="Comune" entita="TB1" campo="TABCOD3" definizione="T5" headerClass="sortable" visibile="false" />
			<gene:campoLista title="Comune" entita="TABSCHE" campo="TABDESC" definizione="T120" headerClass="sortable" />
			<gene:campoLista title="Codice Istat" entita="TABSCHE" campo="TABCOD3" definizione="T9;1" headerClass="sortable" />
			<gene:campoLista title="C.A.P." entita="TABSCHE" campo="TABCOD4" definizione="T5" headerClass="sortable" />
		</gene:formLista>
		
  </gene:redefineInsert>
</gene:template>
