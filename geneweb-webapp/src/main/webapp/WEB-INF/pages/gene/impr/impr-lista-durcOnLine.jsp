<%
/*
 * Created on: 17-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista Durc On Line */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%/*Imposto il menu nel titolo*/%>
<c:if test="${!fn:contains(listaOpzioniUtenteAbilitate, 'ou243#')}">
	<gene:set name="titoloMenu">
		<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
	</gene:set>
</c:if>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="codiceImpresa" value='${gene:getValCampo(key, "IMPR.CODIMP")}' scope="request"/>

<c:set var="wsUrlRichiestaDurc" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "richiesta.durc.ws.client.url")}'/>
<c:set var="wsUrlVerificaRegolarita" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "verifica.regolarita.ws.client.url")}'/>

<c:choose>
	<c:when test="${!empty wsUrlRichiestaDurc && !empty wsUrlVerificaRegolarita}">
		<c:set var="integrazioneWsDurc" value='1'/>
	</c:when>
	<c:otherwise>
		<c:set var="integrazioneWsDurc" value='0'/>
	</c:otherwise>
</c:choose>

<c:set var="isRiattivaDURC" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.IsRiattivaDURCFunction", pageContext, codiceImpresa)}'/>

<gene:redefineInsert name="addToAzioni" >
	<c:if test='${isRiattivaDURC eq "1"}' >
		<tr>
			<td class="vocemenulaterale" >
				<a href="javascript:riattivaVerificaDURC('${contextPath}','${codiceImpresa}')" title="Riattiva verifica DURC" tabindex="1505">Riattiva verifica DURC</a>
			</td>
		</tr>
	</c:if>
</gene:redefineInsert>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="IMPDURC" where="IMPDURC.CODIMP = #IMPR.CODIMP#"  sortColumn="-4" pagesize="20" tableclass="datilista" gestisciProtezioni="true" > 
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu" onClick="chiaveRiga='${chiaveRigaJava}'"/>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>
				<c:if test='${integrazioneWsDurc eq "1"}' >
					<gene:campoLista campo="TIPO_RICHIESTA" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
				</c:if>
				<gene:campoLista campo="NPROTO" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
				<gene:campoLista campo="DATRIC" headerClass="sortable"/>
				<gene:campoLista campo="DATSCA" headerClass="sortable" />
				<gene:campoLista campo="TIPESI" headerClass="sortable" />
			</gene:formLista>

			<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.ImpcaseScheda"  )}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.visualizza" title="Visualizza richiesta DURC on line"/>
			</c:if>
			<c:if test='${gene:checkProtFunz(pageContext, "MOD", "MOD"  ) && !fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.modifica" title="Modifica richiesta DURC on line" />
			</c:if>
			<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL"  ) && !fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.elimina" title="Elimina richiesta DURC on line" />
			</c:if>
		</td>
	</tr>
	<tr>
	<% // Ridefinizione dei pulsanti specificati nel file /WEB-INF/pages/commons/pulsantiListaPage.jsp %>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteListaInserisci">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO") && !fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="javascript:listaNuovo()">
				</c:if>
			</gene:insert>
			<gene:insert name="pulsanteListaEliminaSelezione">
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && !fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
				</c:if>
			</gene:insert>
			&nbsp;
		</td>
	</tr>
	<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou243#")}'>
		<gene:redefineInsert name="listaNuovo"/>
		<gene:redefineInsert name="listaEliminaSelezione"/>
	</c:if>
</table>


<gene:javaScript>

	function riattivaVerificaDURC(contextPath,codiceImpresa) {
		if(confirm("Confermi di procedere con la nuova richiesta DURC?")){
			_wait();
			$.ajax({
				url: contextPath+'/RiattivaVerificaDURC.do',
				type: 'POST',
				async: false,
				dataType: 'json',
				data: {
					codImp: codiceImpresa
				},
				success: function(data) {
					if(data.Esito != null && data.Esito == '0'){
						alert("Errore nella riattivazione verifica Durc:",data.Messaggio);
					} else {
						historyReload();
					}
				},
				error: function(e) {
					_nowait();
					alert("Errore nella riattivazione verifica Durc");
					console.log(e);
				},
				complete: function () {
					_nowait();
				}
			});
		}
	}

	function _wait() {
		document.getElementById('bloccaScreen').style.visibility = 'visible';
		$('#bloccaScreen').css("width", $(document).width());
		$('#bloccaScreen').css("height", $(document).height());
		document.getElementById('wait').style.visibility = 'visible';
		$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
	}

	function _nowait() {
		document.getElementById('bloccaScreen').style.visibility = 'hidden';
		document.getElementById('wait').style.visibility = 'hidden';
	}	
</gene:javaScript>