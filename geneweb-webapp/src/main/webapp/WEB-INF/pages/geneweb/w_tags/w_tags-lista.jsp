
<%
	/*
	 * Created on 17/12/2019
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="W_TAGS-lista" schema="GENEWEB">
	<gene:setString name="titoloMaschera" value="Configurazione etichette" />
	<gene:setString name="entita" value="W_TAGS" />
	
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="W_TAGS" sortColumn="3" where="W_TAGS.CODAPP IN ('${sessionScope.moduloAttivo}')" pagesize="20" 
					tableclass="datilista" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_TAGS">
						<gene:campoLista title="Opzioni <br><center>${titoloMenu}</center>"	width="50">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource	resource="popupmenu.tags.lista.visualizza" title="Visualizza" />
									<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
										<gene:PopUpItemResource	resource="popupmenu.tags.lista.modifica" title="Modifica" />
									</c:if>
									<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
										<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina"	title="Elimina" />
									</c:if>
								</gene:PopUp>
								<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
									<input type="checkbox" name="keys" value="${chiaveRiga}" />
								</c:if>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="CODAPP" visibile="false"/>
						<gene:campoLista width="100" title="Codice integrazione" campo="TAGCOD" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
						<gene:campoLista width="100" title="Etichetta integrazione" campo="TAGVIEW" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
						<gene:campoLista title="Descrizione" campo="TAGDESC" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
						<gene:campoLista title="Etichette visibili nelle maschere ?" campo="TAGVIS" width="80" />
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>
