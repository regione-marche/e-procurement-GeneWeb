
<%
	/*
	 * Created on 09-mar-2016
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

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="W_QUARTZ-Lista" schema="GENEWEB">
	<gene:setString name="titoloMaschera" value="Pianificazioni" />
	<gene:setString name="entita" value="W_QUARTZ" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="W_QUARTZ" where="W_QUARTZ.CODAPP IN ('${sessionScope.moduloAttivo}')" pagesize="20" sortColumn="3;4" tableclass="datilista" gestisciProtezioni="true">
						<gene:campoLista title="Opzioni" width="50">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource	resource="popupmenu.tags.lista.visualizza" title="Visualizza" />
									<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
										<gene:PopUpItemResource	resource="popupmenu.tags.lista.modifica" title="Modifica" />
									</c:if>
								</gene:PopUp>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="CODAPP" visibile="false"/>
						<gene:campoLista campo="BEAN_ID" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>	
						<gene:campoLista campo="DESCRIZIONE" />
						<gene:campoLista campo="CRON_EXPRESSION" />	
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<td class="comandi-dettaglio" colSpan="2">&nbsp;</td>
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	
	<gene:javaScript>
		$("span[id^='colW_QUARTZ_CRON_EXPRESSION_']").find('span').css("white-space","nowrap");
		$("span[id^='colW_QUARTZ_CRON_EXPRESSION_']").find('span').css("width","200px");
	</gene:javaScript>
	
	
</gene:template>