<%/*
   * Created on 24-lug-2007
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */

  // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI EDIT
  // DEL DETTAGLIO DI UN DOCUMENTO ASSOCIATO RELATIVA AI DATI EFFETTIVI
%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:choose>
	<c:when test="${!empty param.id}">
		<c:set var="id" value='${param.id}' scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="id" value='${id}' scope="request" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.ent}">
		<c:set var="ent" value='${param.ent}' scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="ent" value='${ent}' scope="request" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${!empty param.readOnly}">
		<c:set var="readOnly" value='${param.readOnly}' scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="readOnly" value='${readOnly}' scope="request" />
	</c:otherwise>
</c:choose>

<tiles:insert definition=".dettaglioNoTabDef" flush="true">

	<tiles:put name="head" type="string">
	
		<c:if test="${esito eq true}">
			<script type="text/javascript" >
				historyVaiIndietroDi(1);
			</script>
		</c:if>
	
		<script type="text/javascript"
			src="${pageContext.request.contextPath}/js/controlliFormali.js"></script>

		<c:set var="contextPath" value="${pageContext.request.contextPath}"
			scope="request" />
		<c:if test="${readOnly ne 'true' }">
			<c:set var="isNavigazioneDisabilitata" value="1" scope="request" />
		</c:if>

		<script type="text/javascript">
	
		function exitForm() {
			historyVaiIndietroDi(1);
		}

		function setFormLocked(locked) {
			if(locked) _wait();
			else _nowait();
		}

		function _wait() {
			document.getElementById('bloccaScreen').style.visibility = 'visible';
			$('#bloccaScreen').css("width", $(document).width());
			$('#bloccaScreen').css("height", $(document).height());
			document.getElementById('wait').style.visibility = 'visible';
			$("#wait").offset({ top: $(window).height() / 2, left: ($(window).width() / 2) - 200 });
		}

		/*
			* Nasconde l'immagine di attesa
			*/
		function _nowait() {
			document.getElementById('bloccaScreen').style.visibility = 'hidden';
			document.getElementById('wait').style.visibility = 'hidden';
		}

		function annulla() {
			historyVaiIndietroDi(1);
		}

		function salva() {
			//Get Json from angular
			let json = window.surveyRootComponent.saveSurvey();
			//Check if not empty, which means there was an error or validation failed
			if(json) {
				//Store in hidden text
				document.getElementById('jsonFile').value = json;
				//Submit the form
				formQeditor.submit();
			}
		}
		
		function exportExcel() {
			//Call angular export
			window.surveyRootComponent.exportSurvey();
		}
		
		function importExcel() {
			//Call angular import
			window.surveyRootComponent.importSurvey();
		}
		
		<%/* 
		//function carica() {
			// var id = "${id}";
			// var ent = "${ent}";
			// _wait();
			// $.ajax({
			// 	type: "POST",
			// 	dataType: "json",
			// 	async: true,
			// 	beforeSend: function (x) {
			// 		if (x && x.overrideMimeType) {
			// 			x.overrideMimeType("application/json;charset=UTF-8");
			// 		}
			// 	},
			// 	url: contextPath + "/LoadQFormEditor.do",
			// 	data: {
			// 		id: id,
			// 		ent: ent
			// 	},
			// 	success: function (json) {
			// 		if (json) {
			// 			if (json.esito)
			// 				$("#testoQform").val(JSON.stringify(json.dato));
			// 			else
			// 				alert(json.messaggio);

			// 		}
			// 	},
			// 	error: function (e) {
			// 		alert("Errore durante la lettura del modello");
			// 	},
			// 	complete: function () {
			// 		_nowait();
			// 	}
			// });
		//}

		// function ping() {

		// 	$.ajax({
		// 		type: "POST",
		// 		dataType: "json",
		// 		async: true,
		// 		beforeSend: function (x) {
		// 			if (x && x.overrideMimeType) {
		// 				x.overrideMimeType("application/json;charset=UTF-8");
		// 			}
		// 		},
		// 		url: contextPath + "/PingQFormEditor.do",
		// 		data: {},
		// 		success: function (data) { },
		// 		error: function (e) { },
		// 		complete: function () { }
		// 	});

		// }
		*/%>
		</script>
	</tiles:put>



	<tiles:put name="azioniContesto" type="string">
		<gene:template file="menuAzioni-template.jsp">
			<% /* Inseriti i tag per la gestione dell' history: * il
											template 'menuAzioni-template.jsp' e' un file vuoto, ma e' stato definito *
											solo perche' i tag <gene:insert>, <gene:historyAdd> richiedono di essere
												* definiti all'interno del tag <gene:template>
													*/
													%>
		</gene:template>
		<tr>
			<td class="titolomenulaterale">Dettaglio: Azioni</td>
		</tr>
		<c:if test="${readOnly ne 'true' }">
			<tr>
				<td class="vocemenulaterale"><a href="javascript:salva();"
					tabindex="1502" title="Salva">Salva</a></td>
			</tr>
			<tr>
				<td class="vocemenulaterale"><a href="javascript:annulla();"
					tabindex="1503" title="Annulla">Annulla</a></td>
			</tr>
		</c:if>
		<tr>
			<td class="vocemenulaterale"><a href="javascript:exportExcel();"
				tabindex="1504" title="Esporta in Excel">Esporta in Excel</a></td>
		</tr>
		<c:if test="${readOnly ne 'true' }">
		<tr>
			<td class="vocemenulaterale"><a href="javascript:importExcel();"
				tabindex="1505" title="Importa da Excel">Importa da Excel</a></td>
		</tr>
		</c:if>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<jsp:include page="/WEB-INF/pages/commons/torna.jsp" />
	</tiles:put>

	<c:if test="${param.firstTimer}">
		<gene:historyAdd titolo="qeditor" id="qeditor" />
	</c:if>

	<jsp:include page="./titoloQeditor.jsp" />

	<tiles:put name="titoloMaschera" type="string"
		value="${requestScope.titolo}" />

	<tiles:put name="dettaglio" type="string">

		<script>

			// App Parameters
			window.APP_DATA = {
				modeDevLocal: false,
				language: 'it',
				editorId: '${id}',
				editorEnt: '${ent}',
				isReadOnly: ${readOnly},
				methods: {
					setFormLocked: setFormLocked,
					exitForm: exitForm
				},
				urls: {
					assets: '${pageContext.request.contextPath}/js/qeditor/assets',
					ping: '${pageContext.request.contextPath}/PingQFormEditor.do',
					saveForm: '${pageContext.request.contextPath}/SaveQFormEditor.do',
					loadForm: '${pageContext.request.contextPath}/LoadQFormEditor.do'					
				}
			};

		</script>

		<link rel="stylesheet"
			href="${pageContext.request.contextPath}/js/qeditor/styles.css">
		
		<app-root></app-root>

		<script src="${pageContext.request.contextPath}/js/qeditor/runtime.js"
			defer></script>
		<script
			src="${pageContext.request.contextPath}/js/qeditor/polyfills.js"
			defer></script>
		<script src="${pageContext.request.contextPath}/js/qeditor/main.js"
			defer></script>
		</body>

		<form action="${pageContext.request.contextPath}/SaveQFormEditor.do" name="formQeditor" method="post" accept-charset="utf-8">

			<table class="dettaglio-notab">
				 
				<tr>
					<td class="comandi-dettaglio" colspan="2">
					<c:choose>
						<c:when test="${readOnly eq 'true' }">
							<INPUT type="button"
								class="bottone-azione" value="Indietro" title="Indietro"
								onclick="javascript:annulla()"> 
						</c:when>
						<c:otherwise>
							<INPUT type="button"
								class="bottone-azione" value="Salva" title="Salva modifiche"
								onclick="javascript:salva()"> 
							<INPUT type="button"
								class="bottone-azione" value="Annulla" title="Annulla modifiche"
								onclick="javascript:annulla()">
						</c:otherwise>
					</c:choose>
					 &nbsp;
					</td>
				</tr>
			</table>
			<input type="hidden" name="id" id='id' value="${id}"> 
			<input type="hidden" name="ent" id='ent' value="${ent}">
			<input type="hidden" name="jsonFile" id='jsonFile' value="">
			<input type="hidden" name="readOnly" id='readOnly' value="${readOnly}">
				
		</form>
	</tiles:put>

</tiles:insert>

