
<%
	/*
	 * Created on 6-Giu-2016
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

<gene:template file="lista-template.jsp" gestisciProtezioni="true" idMaschera="TAB46-LISTA" schema="GENE">

	<c:choose>
		<c:when test="${sessionScope.moduloAttivo eq 'PL'}">
			<c:set var="tab46cod_where" value="tab46cod in ('G__1','W__1','W0_1','W1_1','G2_1','G5_1','PT_1')" />
		</c:when>
		<c:when test="${sessionScope.moduloAttivo eq 'PG'}">
			<c:set var="tab46cod_where" value="tab46cod in ('G__1','W__1','W0_1','W1_1','G1_1')" />
		</c:when>
		<c:otherwise>
			<c:set var="codiceModuloAttivo" value="${sessionScope.moduloAttivo}_1" />
			<c:set var="tab46cod_where" value="tab46cod in ('G__1','W__1','W0_1','W1_1','${codiceModuloAttivo}')" />
		</c:otherwise>
	</c:choose>

	<gene:redefineInsert name="addHistory">
		<c:if test='${param.metodo ne "nuova"}' >
			<gene:historyAdd titolo='Lista dati tabellati' id="lista" />
		</c:if>
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Dati tabellati" />
	<gene:setString name="entita" value="TAB46" />
	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<td>
					<gene:formLista entita="V_TAB4_TAB6" where="${tab46cod_where}" distinct="true" pagesize="20" sortColumn="3" tableclass="datilista" gestisciProtezioni="true" >
						<gene:campoLista title="Opzioni" width="50">
							<c:if test="${currentRow >= 0}">
								<gene:PopUp variableJs="rigaPopUpMenu${currentRow}"	onClick="chiaveRiga='${chiaveRigaJava}'">
									<gene:PopUpItem title="Visualizza dettaglio" href="ListaDettaglio('${currentRow}','${datiRiga.V_TAB4_TAB6_TAB46TIP}')"/>
								</gene:PopUp>
							</c:if>
						</gene:campoLista>
						<gene:campoLista campo="TAB46TIP" href="javascript:ListaDettaglio('${currentRow}','${datiRiga.V_TAB4_TAB6_TAB46TIP}')"/>
						<gene:campoLista title="Descrizione del tabellato" campo="TAB46DESC" />
						<gene:campoLista title="Tabella di database">
							<c:set var="cod3" value="${fn:substring(datiRiga.V_TAB4_TAB6_TAB46TIP,2,3)}" />
							<c:choose>
								<c:when test="${cod3 eq 'x' or cod3 eq 'y'}">
									TAB3
								</c:when>
								<c:when test="${cod3 eq 'w' or cod3 eq 'k'}">
									TAB0
								</c:when>
								<c:when test="${cod3 eq 'v' or cod3 eq 'z'}">
									TAB2
								</c:when>
								<c:when test="${cod3 eq 'j'}">
									TAB5
								</c:when>
								<c:otherwise>
									TAB1
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
		
		<form name="listaTAB" action="${pageContext.request.contextPath}/ApriPagina.do" method="post">
			<input type="hidden" name="href" value="" /> 
			<input type="hidden" name="cod" value="" />
			<input type="hidden" name="titolo" value="" />
			<input type="hidden" name="metodo" value="apri" />
			<input type="hidden" name="activePage" value="0" />
		</form>
	
	</gene:redefineInsert>
	<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>
	
	<gene:javaScript>
		
		$('table.datilista > thead > tr').find('th:eq(3)').css('width','90px');
		$('table.datilista > tbody > tr').find('td:eq(3)').css('width','90px');
		$('table.datilista > thead > tr').find('th:eq(3)').css('text-align','center');
		$('table.datilista > tbody > tr').find('td:eq(3)').css('text-align','center');		
		
	
		function ListaDettaglio(row,tip) {
			var _num = parseInt(row) + 1;
			var _cod = tip;
			var _titolo = $("#colV_TAB4_TAB6_TAB46DESC_" + _num).find('span').text();
			var _cod3 = _cod.substring(2,3);
			if (_cod3 == 'x' || _cod3 == 'y') {
				document.listaTAB.href.value = "gene/tab3/tab3-lista.jsp";
			} else if (_cod3 == 'w' || _cod3 == 'k') {
				document.listaTAB.href.value = "gene/tab0/tab0-lista.jsp";
			} else if (_cod3 == 'v' || _cod3 == 'z') {
				document.listaTAB.href.value = "gene/tab2/tab2-lista.jsp";
			} else if (_cod3 == 'j') {
				document.listaTAB.href.value = "gene/tab5/tab5-lista.jsp";
			} else {
				document.listaTAB.href.value = "gene/tab1/tab1-lista.jsp";
			}
			
			document.listaTAB.cod.value = _cod;
			document.listaTAB.titolo.value = _titolo;
			document.listaTAB.submit();
		}
		
	</gene:javaScript>
	
</gene:template>