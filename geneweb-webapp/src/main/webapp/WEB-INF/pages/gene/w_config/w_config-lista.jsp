
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

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="W_CONFIG-Lista" schema="GENE">
	<gene:setString name="titoloMaschera" value="Configurazioni" />
	<gene:setString name="entita" value="W_CONFIG" />
	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
			<tr>
				<td>
					<div style="border-bottom: 1px dotted #808080; padding: 5px; background-color: #EFEFEF;"><b>Legenda</b></div>
					<div style="background-color: #EFEFEF; padding: 5px;">
						<b>Valore effettivo:</b> &egrave; il valore effettivamente utilizzato dall'applicativo. 
						<br>
						<b>Provenienza:</b> indica la provenienza del valore effettivo della configurazione.
						<br>
						<br>
						Se la configurazione &egrave; definita in un <b>file</b> di propriet&agrave; (global.properties) il suo valore 
						ha priorit&agrave; sull'analoga configurazione definita in <b>database</b> (tabella w_config),
						inoltre il suo valore &egrave; modificabile solamente agendo sul file di provenienza.
					</div>
				</td>			
			</tr>
			<tr>
				<td>
					<gene:formLista entita="W_CONFIG" where="W_CONFIG.SEZIONE IS NOT NULL AND W_CONFIG.CODAPP IN ('W_','${sessionScope.moduloAttivo}')" pagesize="20" sortColumn="3;4" tableclass="datilista" gestisciProtezioni="true">
						<gene:campoLista title="Opzioni" width="50">
							<c:set var="risultato" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetCaratteristicheProprietaFunction",pageContext,datiRiga.W_CONFIG_CHIAVE)}'/>
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItemResource	resource="popupmenu.tags.lista.visualizza" title="Visualizza" />
									<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
										<c:if test="${esisteProprietaDB eq 'true'}">
											<gene:PopUpItemResource	resource="popupmenu.tags.lista.modifica" title="Modifica" />
										</c:if>
									</c:if>
								</gene:PopUp>
							</c:if>
						</gene:campoLista>
						<gene:campoLista title="Cod. app." campo="CODAPP" />
						<gene:campoLista campo="SEZIONE" />
						<gene:campoLista title="Configurazione" campo="CHIAVE" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>	
						<gene:campoLista campo="DESCRIZIONE" />
						<gene:campoLista title="Valore criptato?" campo="CRIPTATO" />
						
						<c:choose>
							<c:when test="${datiRiga.W_CONFIG_CRIPTATO eq '1' && !empty valoreEffettivoProprieta}">
								<gene:campoLista title="Valore effettivo" campo="VALORE" value="**********"/>
							</c:when>
							<c:otherwise>
								<gene:campoLista title="Valore effettivo" campo="VALORE" value="${valoreEffettivoProprieta}"/>						
							</c:otherwise>
						</c:choose>

						<gene:campoLista title="Provenienza" campo="PROVENIENZA" campoFittizio="true" definizione="T100">
							<c:choose>
								<c:when test="${esisteProprieta eq 'false'}">
									<span style="border-left: 8px solid #FFAA00">&nbsp;</span>Database<br>
									La configurazione e' stata inserita nella tabella W_CONFIG successivamente all'avvio dell'applicativo, &egrave; necessario riavviare l'applicativo.
								</c:when>
								<c:when test="${esisteProprieta eq 'true' && esisteProprietaDB eq 'true'}">
									<span style="border-left: 8px solid #00C621">&nbsp;</span>Database
								</c:when>
								<c:otherwise>
									<span style="border-left: 8px solid #FF0000">&nbsp;</span>File
								</c:otherwise>
							</c:choose>
						</gene:campoLista>
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

		$("div.contenitore-dettaglio").css("width","1060px");
		
		$("table.datilista tr th").css("height","40px");
		$("table.datilista tr td").css("height","35px");
		
		$('table.datilista > thead > tr').find('th:eq(5)').css('width','70px');
		$('table.datilista > tbody > tr').find('td:eq(5)').css('width','70px');

		$("span[id^='colW_CONFIG_SEZIONE_']").find('span').css("width","140px");
		$("span[id^='colW_CONFIG_SEZIONE_']").find('span').css("display","block");

		$("span[id^='colW_CONFIG_CHIAVE_']").find('span').css("word-wrap","break-word");
		$("span[id^='colW_CONFIG_CHIAVE_']").find('span').css("width","200px");
		$("span[id^='colW_CONFIG_CHIAVE_']").find('span').css("display","block");
		
		$("span[id^='colW_CONFIG_DESCRIZIONE_']").find('span').css("word-wrap","break-word");
		$("span[id^='colW_CONFIG_DESCRIZIONE_']").find('span').css("width","200px");
		$("span[id^='colW_CONFIG_DESCRIZIONE_']").find('span').css("display","block");
		
		$("span[id^='colW_CONFIG_VALORE_']").find('span').css("word-wrap","break-word");
		$("span[id^='colW_CONFIG_VALORE_']").find('span').css("width","200px");
		$("span[id^='colW_CONFIG_VALORE_']").find('span').css("display","block");
		
	</gene:javaScript>
	
	
</gene:template>