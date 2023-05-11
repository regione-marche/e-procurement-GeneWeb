<%/*
   * Created on 08-05-13
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:choose>
	<c:when test='${not empty param.discriminante}'>
		<c:set var="discriminante" value="${param.discriminante}" />
	</c:when>
	<c:otherwise>
		<c:set var="discriminante" value="${discriminante}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.entitaPartenza}'>
		<c:set var="entitaPartenza" value="${param.entitaPartenza}" />
	</c:when>
	<c:otherwise>
		<c:set var="entitaPartenza" value="${entitaPartenza}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.ScadenzarioModificabile}'>
		<c:set var="ScadenzarioModificabile" value="${param.ScadenzarioModificabile}" />
	</c:when>
	<c:otherwise>
		<c:set var="ScadenzarioModificabile" value="${ScadenzarioModificabile}" />
	</c:otherwise>
</c:choose>



<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_SCADENZ-Scheda" >

	<jsp:useBean id="now" class="java.util.Date" scope="page"/>
	<fmt:formatDate value='${now}' type='both' pattern='dd/MM/yyyy' var="now" scope="page"/>

	<c:choose>
		<c:when test='${modo eq "NUOVO"}'>
			<gene:setString name="titoloMaschera" value='Nuova attività' />
		</c:when>
		<c:otherwise>
			<gene:setString name="titoloMaschera" value='Attività' />
		</c:otherwise>
	</c:choose>
			
	<gene:redefineInsert name="corpo">
		<gene:formScheda entita="G_SCADENZ" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreG_SCADENZ" >
			
			<c:if test="${ScadenzarioModificabile ne 1 }" >
				<gene:redefineInsert name="schedaNuovo" />
				<gene:redefineInsert name="schedaModifica" />
				<gene:redefineInsert name="pulsanteModifica" />
				<gene:redefineInsert name="pulsanteNuovo" />
			</c:if>
			
			
			<c:set var="KEY1" value='${gene:getValCampo(param.keyAdd, "KEY1")}' />
			<c:set var="KEY2" value='${gene:getValCampo(param.keyAdd, "KEY2")}' />
			<c:set var="KEY3" value='${gene:getValCampo(param.keyAdd, "KEY3")}' />
			<c:set var="KEY4" value='${gene:getValCampo(param.keyAdd, "KEY4")}' />
			<c:set var="KEY5" value='${gene:getValCampo(param.keyAdd, "KEY5")}' />
			<c:set var="idAttivCorrente" value='${datiRiga.G_SCADENZ_ID}' />
			
			<gene:campoScheda campo="ID" visibile="false" />
			<gene:campoScheda campo="PRG" visibile="false" defaultValue='${sessionScope.moduloAttivo}' />
			<gene:campoScheda campo="ENT" visibile="false" defaultValue="${entitaPartenza}" />
			<gene:campoScheda campo="KEY1" visibile="false" defaultValue='${KEY1}' />
			<gene:campoScheda campo="KEY2" visibile="false" defaultValue='${KEY2}' />
			<gene:campoScheda campo="KEY3" visibile="false" defaultValue='${KEY3}' />
			<gene:campoScheda campo="KEY4" visibile="false" defaultValue='${KEY4}' />
			<gene:campoScheda campo="KEY5" visibile="false" defaultValue='${KEY5}' />
			<gene:campoScheda campo="PREV" visibile="false" defaultValue='0' />
			<gene:campoScheda campo="DISCR" visibile="false" defaultValue='${discriminante }' />
			
			<gene:gruppoCampi>
				<gene:campoScheda>
					<td colspan="2">
						<b>Definizione dell'attivit&agrave;</b>
					</td>
				</gene:campoScheda>
				<gene:campoScheda campo="TIT" obbligatorio="true"/>
				<gene:campoScheda campo="DESCR"/>
				<gene:campoScheda campo="TIPOEV"/>
				<gene:campoScheda campo="TIPOIN" defaultValue='1' visibile="false"/>
				<gene:campoScheda campo="DATAIN" visibile="false" />
				<gene:campoScheda campo="DURATA" visibile="false"/> 
				<gene:campoScheda campo="TIPOFI" obbligatorio="true" />
				<gene:campoScheda campo="IDATTIV" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoIdAttivita"/>
				<gene:campoScheda campo="DATAFI" />
				<gene:campoScheda campo="FINEDOPO" />
			</gene:gruppoCampi>
			<gene:gruppoCampi>
				<gene:campoScheda>
					<td colspan="2"><b>Esecuzione attivit&agrave;</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="TIPOCONS" obbligatorio="true"/>
				<gene:archivio titolo="Eventi per scadenzario" 
                        campi="G_EVENTISCADENZ.COD;G_EVENTISCADENZ.TIT"
                        lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.G_EVENTISCADENZ.COD") and gene:checkProt(pageContext, "COLS.MOD.GENE.G_EVENTISCADENZ.TIT"),"gene/g_eventiscadenz/g_eventiscadenz-lista-popup.jsp","")}'
                        chiave=""
                        scheda=""
                        schedaPopUp=""
                        functionId="default"
						parametriWhere="T:${discriminante};T:${entitaPartenza}"
                        >
                  <gene:campoScheda campo="CODEVENTO"  />
                  <gene:campoScheda campo="TIT" entita="G_EVENTISCADENZ" where="G_EVENTISCADENZ.COD=G_SCADENZ.CODEVENTO"/>
                </gene:archivio>
				<gene:campoScheda campo="DATASCAD" modificabile="false"/>
				<gene:campoScheda campo="DATACONS"/>
			</gene:gruppoCampi>
			<gene:gruppoCampi >
				<gene:campoScheda>
					<td colspan="2"><b>Promemoria ed altre informazioni</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="GGPROMEM" title='Invia promemoria N giorni prima della scadenza' defaultValue='0'/>
				<gene:campoScheda campo="REFPROMEM" title="Destinatari promemoria" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoRefpromem"/>
				<gene:campoScheda campo="DESTPROMEM" title="Altri destinatari promemoria" />

				<gene:campoScheda campo="NOTE"/>
				<gene:campoScheda campo="STPROMEM" visibile="false"/>
			</gene:gruppoCampi>
						

			<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>

			<gene:fnJavaScriptScheda funzione='modifyTIPOIN("#G_SCADENZ_TIPOIN#")' elencocampi="G_SCADENZ_TIPOIN" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyTIPOFI("#G_SCADENZ_TIPOFI#")' elencocampi="G_SCADENZ_TIPOFI" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyTIPOCONS("#G_SCADENZ_TIPOCONS#")' elencocampi="G_SCADENZ_TIPOCONS" esegui="true" />
			<gene:fnJavaScriptScheda funzione='resetSTPROMEM()' elencocampi="G_SCADENZ_GGPROMEM; G_SCADENZ_DESTPROMEM; G_SCADENZ_REFPROMEM" esegui="false" />
			<input type="hidden" name="discriminante" id="discriminante" value="${discriminante}" />
			<input type="hidden" name="entitaPartenza" id="entitaPartenza" value="${entitaPartenza}" />
			<input type="hidden" name="ScadenzarioModificabile" id="ScadenzarioModificabile" value="${ScadenzarioModificabile}" />
		</gene:formScheda>

		<gene:javaScript>
		
			function resetSTPROMEM() {
				setValue("G_SCADENZ_STPROMEM", "0");
			}
		
			function modifyTIPOIN(tipoin){
				if(tipoin==1){
					showObj("rowG_SCADENZ_DURATA", false);
					setValue("G_SCADENZ_DURATA", "");
				}else if(tipoin==2){
					showObj("rowG_SCADENZ_DURATA", true);
				}
			}
			
			function modifyTIPOFI(TIPOFI){
				if(TIPOFI==1){
					showObj("rowG_SCADENZ_FINEDOPO", false);
					showObj("rowG_SCADENZ_IDATTIV", false);
					<c:if test='${modo ne "VISUALIZZA"}'>
						showObj("rowG_SCADENZ_DATAFI", true);
					</c:if>
					setValue("G_SCADENZ_FINEDOPO", "");
					setValue("G_SCADENZ_IDATTIV", "");
				}else if(TIPOFI==2){
					showObj("rowG_SCADENZ_FINEDOPO", true);
					showObj("rowG_SCADENZ_IDATTIV", true);
					<c:if test='${modo ne "VISUALIZZA"}'>
						showObj("rowG_SCADENZ_DATAFI", false);
						setValue("G_SCADENZ_DATAFI", "");
					</c:if>
				}
			}
			
			function modifyTIPOCONS(tipocons){
				if(tipocons==1){
					showObj("rowG_SCADENZ_CODEVENTO", false);
					showObj("rowG_EVENTISCADENZ_TIT", false);
					setValue("G_SCADENZ_CODEVENTO", "");
					setValue("G_EVENTISCADENZ_TIT", "");
					showObj("rowG_SCADENZ_DATACONS", true);
				}else if(tipocons==2){
					showObj("rowG_SCADENZ_CODEVENTO", true);
					showObj("rowG_EVENTISCADENZ_TIT", true);
					showObj("rowG_SCADENZ_DATACONS", false);
				}
				<c:if test="${modo eq 'VISUALIZZA'}">
				showObj("rowG_SCADENZ_DATACONS", true);
				</c:if>
			}
			
					
			
			function schedaConferma_Custom(){
				var tipoin= getValue("G_SCADENZ_TIPOIN");
				if(tipoin==2){
					var durata=getValue("G_SCADENZ_DURATA");
					if(durata==null || durata==""){
						alert("La durata attività è obbligatoria");
						return;
					}
				}
				var TIPOFI= getValue("G_SCADENZ_TIPOFI");
				if(TIPOFI==2){
					var scadoppo=getValue("G_SCADENZ_FINEDOPO");
					if(scadoppo==null || scadoppo==""){
						alert("Il numero di giorni della scadenza sono obbligatori");
						return;
					}
					var idattiv=getValue("G_SCADENZ_IDATTIV");
					if(idattiv==null || idattiv==""){
						alert("L'attività dopo cui si ha la scadenza è obbligatoria");
						return;
					}
				} else {
					var fine=getValue("G_SCADENZ_DATAFI");
					if(fine==null || fine==""){
						alert("Data termine è obbligatoria");
						return;
					}
				}
					
				var tipocons= getValue("G_SCADENZ_TIPOCONS");
				if(tipocons==2){
					var codevento=getValue("G_SCADENZ_CODEVENTO");
					if(codevento==null || codevento==""){
						alert("Il codice evento è obbligatorio");
						return;
					}
				}
				
				var destpromem = getValue("G_SCADENZ_DESTPROMEM");
				if(destpromem!=null && destpromem!=""){
					var listaMail = trimStringa(destpromem).split(',');
					var esitoMail1 = true;
					for(var i = 0; i < listaMail.length; i++){
					  	listaMail[i] = trimStringa(listaMail[i]);
					  	esitoMail1 = isFormatoEmailValido(trimStringa(listaMail[i]));
					  	if(!esitoMail1){
					  		if(listaMail.length > 1)
					  			alert("La lista degli indirizzi email nel campo 'Altri destinatari' presenta degli errori di sintassi.\nVerificare la sintassi di tutti gli indirizzi.");
					  		else
					  			alert("L'indirizzo email nel campo 'Altri destinatari' non e' sintatticamente valido.");
					  		return;
					  		
					  	}
					  }
				}
				
				
				var promem = getValue("G_SCADENZ_GGPROMEM");
				if(promem==null || promem=="")
					setValue("G_SCADENZ_GGPROMEM","0");
				schedaConferma_Default();
	
			}
			
			var schedaConferma_Default = schedaConferma;
			schedaConferma = schedaConferma_Custom;
			
			
		</gene:javaScript>
	</gene:redefineInsert>
</gene:template>