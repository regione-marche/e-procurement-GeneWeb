<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista storia delle modifiche */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="StTrgLista" >
	<gene:setString name="titoloMaschera" value="Lista tracciatura modifiche su DB"/>

	<gene:redefineInsert name="corpo">
		<table class="lista">
		<tr><td >
			<gene:formLista entita="ST_TRG" sortColumn="-2" pagesize="20" tableclass="datilista" 
				gestisciProtezioni="true" > 
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:redefineInsert name="pulsanteListaInserisci" />
				<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
	
				<gene:campoLista title="Opzioni" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza dettaglio operazione"/>
					</gene:PopUp>
				</gene:campoLista>
				
				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />

				<gene:campoLista campo="ST_SEQ" />
				<gene:campoLista campo="ST_DATE" title="Data / ora" />
				<gene:campoLista campo="ST_OPERATION" title="Tipo operazione" href="${link}" headerClass="sortable" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoTipoOperazione" />
				<gene:campoLista campo="ST_TABLE" title="Entit&agrave;" headerClass="sortable" />
				<gene:campoLista campo="ST_SYSCON" title="Codice utente" headerClass="sortable"/>
				<gene:campoLista campo="ST_SYSUTE" title="Nome utente" headerClass="sortable"/>
				
				<gene:campoLista campo="ST_OSUSER" title="Utente sistema operativo" headerClass="sortable"/>
				<gene:campoLista campo="ST_IP_ADDRESS" title="Indirizzo di rete del client" headerClass="sortable"/>
				
			</gene:formLista>
		</td>
		</tr>

		</table>
  </gene:redefineInsert>

</gene:template>
