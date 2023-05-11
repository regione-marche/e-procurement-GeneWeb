
<%
	/*
	 * Created on 20-Ott-2008
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
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda"%>

<gene:template file="lista-template.jsp" gestisciProtezioni="true"
	idMaschera="W_APPLICATION_KEYS-lista" schema="GENEWEB">
	<gene:setString name="titoloMaschera" value="Credenziali servizi di pubblicazione" />
	<gene:setString name="entita" value="W_APPLICATION_KEYS" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td><gene:formLista entita="W_APPLICATION_KEYS" pagesize="20" sortColumn="2"
					tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_APPLICATION_KEYS">
					<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"
						width="50">
						<c:if test="${currentRow >= 0}">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"
								onClick="chiaveRiga='${chiaveRigaJava}'">
								<gene:PopUpItemResource
									resource="popupmenu.tags.lista.visualizza"
									title="Visualizza" />
									
								<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"
										title="Elimina" />
							</gene:PopUp>
							
							<input type="checkbox" name="keys" value="${chiaveRiga}" />
							
						</c:if>
					</gene:campoLista>
					<gene:campoLista campo="CLIENTID" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
					<gene:campoLista campo="NOTE"/>
				</gene:formLista></td>
			</tr>
			<tr><jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" /></tr>
		</table>
	</gene:redefineInsert>
</gene:template>