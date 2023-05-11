<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />



<c:choose> 
	<c:when test="${!empty param.id}" >
		<c:set var="id" value='${param.id}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="id" value='${id}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose> 
	<c:when test="${!empty param.ent}" >
		<c:set var="ent" value='${param.ent}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="ent" value='${ent}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:choose> 
	<c:when test="${!empty param.idPreview}" >
		<c:set var="idPreview" value='${param.idPreview}' scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="idPreview" value='${idPreview}' scope="request"/>
	</c:otherwise>
</c:choose>

<c:set var="modo" value="MODIFICA" scope="request" />


<c:set var="whereQFORM" value="QFORMCONFI.ID=0" />

<gene:template file="scheda-template.jsp" gestisciProtezioni="false" >
	
	<gene:redefineInsert name="corpo">
	
	${gene:callFunction4("it.eldasoft.gene.tags.functions.GestionePreviewParametriQformFunction", pageContext,idPreview, id, ent)}
	
	<gene:formScheda entita="QFORMCONFI" gestisciProtezioni="false"  gestore="it.eldasoft.gene.tags.gestori.submit.GestoreQformPreview">
		
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		
		<gene:redefineInsert name="addHistory">
		
			<gene:historyAdd titolo="Anteprima Q-form" id="qform anteprima" />
		</gene:redefineInsert>	
	
		
		<gene:redefineInsert name="schedaConferma">
			<tr>
				<c:choose>
					<c:when test="${not empty idPreview  and ricaricaParametri ne 'SI'}">
						<td class="vocemenulaterale">
							<a href="javascript:ricaricaPagina();" title="Torna a imposta variabili" tabindex="1501">
								Torna a imposta variabili
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td class="vocemenulaterale">
							<a href="javascript:schedaConferma();" title="Genera anteprima" tabindex="1501">
								Genera anteprima
							</a>
						</td>
					</c:otherwise>
				</c:choose>
				
			</tr>
		</gene:redefineInsert>
		<gene:redefineInsert name="schedaAnnulla">
			<tr>
				<c:choose>
					<c:when test="${not empty idPreview  and ricaricaParametri ne 'SI'}">
						<td class="vocemenulaterale">
							<a href="javascript:schedaAnnulla();" title="Chiudi anteprima" tabindex="1502">
							Chiudi anteprima
							</a>
						</td>
					</c:when>
					<c:otherwise>
						<td class="vocemenulaterale">
							<a href="javascript:schedaAnnulla();" title="Chiudi anteprima" tabindex="1502">
							Chiudi anteprima
						</td>
					</c:otherwise>
				</c:choose>
				
			</tr>
		</gene:redefineInsert>
		
		<c:choose>
			<c:when test="${not empty idPreview  and ricaricaParametri ne 'SI'}">
				<gene:setString name="titoloMaschera" value="Anteprima Q-form"/>
			</c:when>
			<c:otherwise>
				<gene:setString name="titoloMaschera" value="Imposta variabili per anteprima Q-form"/>
			</c:otherwise>
		</c:choose>			
		
		
		<gene:campoScheda campo="ID"  visibile="false" />
		
		<c:choose>
			<c:when test="${not empty idPreview  and ricaricaParametri ne 'SI'}">
				<link rel="stylesheet" href="${pageContext.request.contextPath}/js/qcompiler/styles.css">
				<app-root></app-root>
				<script src="${pageContext.request.contextPath}/js/qcompiler/runtime.js" defer></script>
				<script src="${pageContext.request.contextPath}/js/qcompiler/polyfills.js" defer></script>
				<script src="${pageContext.request.contextPath}/js/qcompiler/main.js" defer></script>
			</c:when>
		</c:choose>
		
		</body>
		
		<c:choose>
			
			<c:when test="${not empty idPreview  and ricaricaParametri ne 'SI'}">
				<%--
				<gene:campoScheda >
					<td colspan="2" id="preview">
						<textarea id="testoQform" name="testoQform"
						rows="20" cols="100"></textarea>
					</td>
				</gene:campoScheda>
				--%>
			</c:when>
			<c:otherwise>
				<c:set var="contatore" value="1" scope="page"/>
				<c:forEach items="${parametriImpostati}" var="item" varStatus="stato">
					<gene:campoScheda nome="titoloParametro_${contatore}">
						<td colspan="2">
							<b>Variabile ${contatore}</b>
						</td>
					</gene:campoScheda>
					<gene:campoScheda campo="DESCRI_${contatore}"  campoFittizio="true" visibile="true" modificabile="false" definizione="T2000;;;;W_QFDESCRIC" value="${item[0]}" />
					<gene:campoScheda campo="CHIAVE_${contatore}"  campoFittizio="true" visibile="true" modificabile="false" definizione="T100;;;;W_QFCHIAVE" value="${item[1]}" />
					<gene:campoScheda campo="VALORE_${contatore}"  campoFittizio="true"  visibile="true" definizione="T2000;0;;;W_QFVALORE" value="${item[2]}"/>
					<input type="hidden"  name="DESCRI_NASCOSTO_${contatore}" id="N_DESCRI_${contatore}" value="${item[0]}">
					<input type="hidden"  name="CHIAVE_NASCOSTO_${contatore}" id="N_CHIAVE_${contatore}" value="${item[1]}">
					<input type="hidden"  name="VALARRAY_${contatore}" id="VALARRAY_${contatore}" value="${item[3]}">
					<c:set var="contatore" value="${contatore + 1}" />
				</c:forEach>
				<c:set var="numElementi" value="${contatore - 1}" />
			</c:otherwise>
		</c:choose>
		
		
		<gene:campoScheda>
			<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
					<c:when test="${not empty idPreview  and ricaricaParametri ne 'SI'}">
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Torna a imposta variabili" title="Torna a imposta variabili" onclick="javascript:ricaricaPagina();">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Chiudi anteprima" title="Chiudi anteprima" onclick="javascript:schedaAnnulla();">
						</gene:insert>
					</c:when>
					<c:otherwise>
						<gene:insert name="pulsanteSalva">
							<INPUT type="button" class="bottone-azione" value="Genera anteprima" title="Genera anteprima" onclick="javascript:schedaConferma();">
						</gene:insert>
						<gene:insert name="pulsanteAnnulla">
							<INPUT type="button" class="bottone-azione" value="Chiudi anteprima" title="Chiudi anteprima" onclick="javascript:schedaAnnulla();">
						</gene:insert>
					</c:otherwise>
				</c:choose>
				
			
				
				
				&nbsp;
			</td>
		</gene:campoScheda>
		
		<input type="hidden"  name="id" id="id" value="${id}">
		<input type="hidden"  name="ent" id="ent" value="${ent}">
		<input type="hidden"  name="idPreview" id="idPreview" value="${idPreview}">
		<input type="hidden"  name="numElementi" id="numElementi" value="${numElementi}">
		<input type="hidden"  name="salvataggioDati" id="salvataggioDati" value="SI">
		
	</gene:formScheda>
		
		
	</gene:redefineInsert>
	<gene:javaScript>
		function schedaConfermaCustom(){
			document.forms[0].jspPathTo.value="geneweb/qeditor/anteprimaQform.jsp";
			schedaConfermaDefault();
		}		
		
		var schedaConfermaDefault = schedaConferma;
		var schedaConferma = schedaConfermaCustom;		
		
		function schedaAnnulla(){
			<c:if test="${not empty idPreview }">
				cancellaDatiPreview();
			</c:if>
			historyVaiIndietroDi(1);
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
		
		function cancellaDatiPreview(){
			 var id = $("#idPreview").val();
			 _wait();
			 $.ajax({
			 	type: "POST",
			 	dataType: "json",
			 	async: false,
			 	beforeSend: function (x) {
			 		if (x && x.overrideMimeType) {
			 			x.overrideMimeType("application/json;charset=UTF-8");
			 		}
			 	},
			 	url: contextPath + "/DeleteDatiPreviweQForm.do",
			 	data: {
			 		id: id
			 	},
			 	complete: function () {
			 		_nowait();
			 	}
			 });
		}
		
		function setFormLocked(locked) {
			if(locked) _wait();
			else _nowait();
		}
		
		function preview(){
			 /*var id = $("#id").val();
			 var ent = $("#ent").val();
			 var idPreview = $("#idPreview").val();
			 _wait();
			 $.ajax({
			 	type: "POST",
			 	dataType: "json",
			 	async: false,
			 	beforeSend: function (x) {
			 		if (x && x.overrideMimeType) {
			 			x.overrideMimeType("application/json;charset=UTF-8");
			 		}
			 	},
			 	url: contextPath + "/LoadQFormPreview.do",
			 	data: {
			 		id: id,
			 		ent: ent,
			 		idPreview: idPreview
			 	},
			 	success: function (json) {
					if (json) {
			 			if (json.esito)
			 				$("#testoQform").val(JSON.stringify(json.dato));
			 			else
			 				alert(json.messaggio);

			 		}
				},
				error: function (e) {
					alert("Errore durante la creazione dell'anteprima");
				},
				complete: function () {
					_nowait();
				}
			 });*/
		}
		
		<c:if test="${not empty idPreview and ricaricaParametri ne 'SI'}">
			preview();
		</c:if>
		
		function ricaricaPagina(){
			$("#salvataggioDati").val("NO");
			schedaConferma();
		}
		
		// App Parameters
		window.APP_DATA = {
			modeDevLocal: false,
			modePreview: true,
			language: 'it',
			
			previewId: $("#id").val(),
			previewEnt: $("#ent").val(),
			previewIdPreview: $("#idPreview").val(),
			
			methods: {
				setFormLocked: setFormLocked
			},
			urls: {
				assets: '${pageContext.request.contextPath}/js/qcompiler/assets',
				ping: '${pageContext.request.contextPath}/PingQFormEditor.do',
				loadForm: '${pageContext.request.contextPath}/LoadQFormPreview.do',					
				saveForm: '',
				getUuid: '',
				attachmentUpload: '',
				attachmentDownload: '',
				attachmentDelete: '',
				generatePDF: '',
				actionNext: '',
				actionCancel: '',
			}					
		};
		
	</gene:javaScript>
</gene:template>
