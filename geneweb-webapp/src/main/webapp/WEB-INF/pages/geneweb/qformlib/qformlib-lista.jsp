
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="nomeContainerFiltri" value="deftrovaQFORMLIB-${empty param.numeroPopUp ? 0 : param.numeroPopUp}"/> 

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENEWEB" idMaschera="QformLista" >
	<gene:setString name="titoloMaschera" value="Lista modelli Q-form"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENEWEB.QformScheda")}'/>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
			<gene:formLista entita="QFORMLIB" sortColumn="2" pagesize="20" tableclass="datilista" gestisciProtezioni="true" 
				gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreQFORMLIB"> 
				
				<input type="hidden" name="filtroLista" value="${empty sessionScope[nomeContainerFiltri].trovaAddWhere or !fn:contains(sessionScope[nomeContainerFiltri].trovaAddWhere, 'QFORMLIB.STATO')}" />
								
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					
					<c:set var="chiaveRigaQformlib" value="${chiaveRigaJava};QFORMLIB.CODMODELLO=T:${datiRiga.QFORMLIB_CODMODELLO}"/>
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						
						<% //Aggiunta dei menu sulla riga %> 
						<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENEWEB.QformScheda")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza modello Q-form"/>
						</c:if>
						<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENEWEB.QformScheda") and gene:checkProtFunz(pageContext, "MOD", "MOD") and (datiRiga.QFORMLIB_STATO eq 1 or datiRiga.QFORMLIB_STATO eq 3) and datiRiga.QFORMLIB_MODINTERNO ne 1}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica modello Q-form" />
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL") and datiRiga.QFORMLIB_STATO eq 1 and datiRiga.QFORMLIB_MODINTERNO ne 1}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.elimina" title="Elimina modello Q-form" href="listaEliminaCustom('${chiaveRigaJava};QFORMLIB.CODMODELLO=T:${datiRiga.QFORMLIB_CODMODELLO}')"/>
						</c:if>
						<gene:PopUpItem title="Copia modello Q-form" href="listaCopia(${datiRiga.QFORMLIB_ID},'${datiRiga.QFORMLIB_CODMODELLO}')"/>
					</gene:PopUp>
								
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")  and datiRiga.QFORMLIB_STATO eq 1 and datiRiga.QFORMLIB_MODINTERNO ne 1}'>
						<input type="checkbox" name="keys" value="${chiaveRigaQformlib}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>

				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="CODMODELLO" headerClass="sortable" width="90"/>
				<gene:campoLista campo="TITOLO" headerClass="sortable"  href="${gene:if(visualizzaLink, link, '')}"/>
				<gene:campoLista campo="DESCRIZIONE" headerClass="sortable" />
				<gene:campoLista campo="STATO" headerClass="sortable" width="100"/>
				<gene:campoLista campo="MODINTERNO" title="Interno?" headerClass="sortable" width="80"/>
				<gene:campoLista campo="ID" visibile="false"/>
				
			</gene:formLista>
		</td></tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<gene:insert name="pulsanteListaInserisci">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovo()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteListaEliminaSelezione">
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					</c:if>
				</gene:insert>
			
				&nbsp;
			</td>
		</tr>
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
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
		
		function listaCopia(id, cod){
			var messaggio="Confermi di volere procedere con la copia?";
			if(confirm(messaggio)){
			_wait();
			$.ajax({
				type: "GET",
				dataType: "text",
				async: false,
				beforeSend: function (x) {
					if (x && x.overrideMimeType) {
						x.overrideMimeType("application/text");
					}
				},
				url: contextPath + "/CopyQformlib.do",
				data: {
					id: id,
					cod: cod
				},
				success: function (data) {
					if (data) {
						if(data.esito="true")
							historyReload();
					}
				},
				error: function (e) {
					alert("Errore durante la cancellazione del qform");
				},
				complete: function () {
					_nowait();
				}
			});
		  }
		}
		
		function listaEliminaCustom(chiave){
			chiaveRiga=chiave;
			listaElimina();
		}
	</gene:javaScript>	
</gene:template>
