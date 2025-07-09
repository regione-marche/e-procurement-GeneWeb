<%
/*
 * Created on: 13-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Interno della scheda dell'ufficio intestatario */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<fmt:setBundle basename="AliceResources" />
<c:set var="nomeEntitaSingolaParametrizzata">
	<fmt:message key="label.tags.uffint.singolo" />
</c:set>
<c:set var="codein" value='${gene:getValCampo(key,"CODEIN")}' scope="request" />
<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "UFFINT", "CODEIN")}'/>
<c:set var="obbligatorioCodFisc" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloObbligatorietaCodFiscPivaFunction", pageContext, "UFFINT","CFEIN")}'/>
<c:set var="obbligatorioPIVA" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloObbligatorietaCodFiscPivaFunction", pageContext, "UFFINT","IVAEIN")}'/>
<c:set var="obbligatoriaCorrettezzaCodFisc" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloCorrettezzaFunction", pageContext, "UFFINT","CFEIN")}'/>
<c:set var="obbligatoriaCorrettezzaPIVA" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloCorrettezzaFunction", pageContext, "UFFINT","IVAEIN")}'/>
<c:set var="valoreItalia" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetCodiceTabellatoDaDescrFunction", pageContext, "Ag010","Italia")}' scope="request"/>

<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>

<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
		<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
</c:if>

<gene:formScheda entita="UFFINT" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreUFFINT" >

	<c:if test='${modo ne "NUOVO"}'>
		<c:set var='nonUsato' value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GestionePuntiDiContattoFunction", pageContext, key, "true")}' />
	</c:if>

	<gene:campoScheda campo="CODNAZ" visibile="false" />
	<gene:campoScheda campo="CODEIN" visibile="false" />

	<c:set var='nonUsato' value='${gene:callFunctionN("it.eldasoft.gene.tags.functions.GestionePuntiDiContattoFunction", pageContext, key, "false")}' />
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='PUNTICON'/>
		<jsp:param name="chiave" value='${codiceUfficio}'/>
		<jsp:param name="archiviFiltrati" value='${archiviFiltrati}'/>
		<jsp:param name="nomeAttributoLista" value='puntiContatto' />
		<jsp:param name="idProtezioni" value="PUNTI" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/punticon/puntiContatto.jsp" />
		<jsp:param name="arrayCampi" value="'PUNTICON_CODEIN_', 'PUNTICON_NUMPUN_','PUNTICON_NOMPUN_', 'PUNTICON_VIAEIN_','PUNTICON_NCIEIN_','PUNTICON_CITEIN_','PUNTICON_PROEIN_','PUNTICON_CAPEIN_','PUNTICON_CODCIT_','PUNTICON_CODNAZ_','PUNTICON_TELEIN_','PUNTICON_FAXEIN_','PUNTICON_EMAIIN_','PUNTICON_EMAI2IN_','PUNTICON_INDWEB_','PUNTICON_CODFE_','PUNTICON_CODRES_','TECNI_NOMTE_','COM_PROEIN_','PUNTICON_CODCONS_NSO_'" />
		<jsp:param name="titoloSezione" value="Punto di contatto" />
		<jsp:param name="titoloNuovaSezione" value="Punto di contatto" />
		<jsp:param name="descEntitaVociLink" value="punto di contatto" />
		<jsp:param name="msgRaggiuntoMax" value="i punti di contatto" />
		<jsp:param name="sezioneListaVuota" value="true"/>
		<jsp:param name="usaContatoreLista" value="true"/>
	</jsp:include>

	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="UFFINT"/>
	</jsp:include>
	
	<c:if test='${!fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
		<gene:campoScheda>	
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
	</c:if>

	<gene:fnJavaScriptScheda funzione='changeComuneUffint("#UFFINT_PROEIN#", "COM_PROEIN")' elencocampi='UFFINT_PROEIN' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("UFFINT_PROEIN", "#COM_PROEIN#")' elencocampi='COM_PROEIN' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#UFFINT_CITEIN#", "${valoreItalia}", "UFFINT_CODNAZ")' elencocampi='UFFINT_CITEIN' esegui="false"/>
	<input type="hidden" name="paginaAltriDati" value="true"/>
</gene:formScheda>

<gene:javaScript>
<c:if test='${! empty sessionScope.uffint && fn:contains(archiviFiltrati,"TECNI") && modoAperturaScheda eq "MODIFICA"}'>
	document.formResponsabile.archFunctionId.value = "tecniUffintFilter";
	document.formResponsabile.archWhereParametriLista.value = "T:${datiRiga.UFFINT_CODEIN}";
</c:if>

	function changeComuneUffint(provincia, nomeUnCampoInArchivio) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("UFFINT_CAPEIN", "");
		setValue("UFFINT_CITEIN", "");
		setValue("UFFINT_CODCIT", "");
	}
	
	function Utenti(){
		document.location.href = '${pageContext.request.contextPath}/ApriPagina.do?href=gene/uffint/uffint-lista-utenti.jsp&key=${key}';
	}
	
	function DatiStorico(){
		document.location.href = '${pageContext.request.contextPath}/ApriPagina.do?href=gene/sto_uffint/sto_uffint-lista.jsp&codiceUfficio=${codiceUfficio}';
	}
	
	<c:if test='${!(modo eq "VISUALIZZA")}'>
	 	var schedaConferma_Default = schedaConferma;
	 	
	 	function schedaConferma_Custom(){
	 	 var obbligatoriaCorrettezzaCodFisc="${obbligatoriaCorrettezzaCodFisc }";
	 	 var controlloOkCodFisc=true;
	 	 clearMsg();
	 	 
	 	 if (obbligatoriaCorrettezzaCodFisc== "true" ){
	 	 	var selectNazionalita= document.getElementById("UFFINT_CODNAZ");
	 		var isItalia= isNazionalitaItalia(selectNazionalita);
	 		
	 		if(isItalia == "si"){
	 			var codfisc=getValue("UFFINT_CFEIN");
		 	 	controlloOkCodFisc=checkCodFis(codfisc);
		 	 	if(!controlloOkCodFisc){
		 	 		outMsg("Il valore del codice fiscale specificato non è valido", "ERR");
					onOffMsg();
		 	 	}
	 		}
	 	 	
	 	 }
	 	 
	 	 var obbligatoriaCorrettezzaPIVA="${obbligatoriaCorrettezzaPIVA }";
	 	 var controlloOkPIVA=true;
	 	 if (obbligatoriaCorrettezzaPIVA=="true" ){
	 	 	var selectNazionalita= document.getElementById("UFFINT_CODNAZ");
	 		var isItalia= isNazionalitaItalia(selectNazionalita);
	 		var piva=getValue("UFFINT_IVAEIN");
	 		if(isItalia == "si"){
	 			controlloOkPIVA=checkParIva(piva);
		 	}else{
	 			controlloOkPIVA=true;
	 		}
	 	 	if(!controlloOkPIVA){
	 	 		outMsg("Il valore della Partita I.V.A. o V.A.T. specificato non è valido", "ERR");
				onOffMsg();
	 	 	}
	 	 }
	 	 
	 	 if(controlloOkCodFisc && controlloOkPIVA)
	 	 	schedaConferma_Default();
	 	}
	 	
	 	schedaConferma =   schedaConferma_Custom;

	 </c:if>
	 
	 function changeComune(provincia, nomeUnCampoInArchivio, progressivo) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("PUNTICON_CAPEIN_" + progressivo, "");
		setValue("PUNTICON_CITEIN_" + progressivo, "");
		setValue("PUNTICON_CODCIT_" + progressivo, "");
	}
	
	//In visualizzazione si assegna allo span che contiene il valore del tabellato A1092 associato al campo
	// UFFSET.NOMSET una ampiezza fissa, per garantire un allineamento verticale dei campi
	<c:if test='${modo eq "VISUALIZZA"}'>
		for(i=1;i <= lastIdUFFSETVisualizzata;i++){
			var indice = i.toString();
			var id="UFFSET_NOMSET_" + indice + "view";
			$("#"+id).css({
				"width":"400px", 
				"display":"inline-block"
			});
		}
		<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.UFFINT.Storico")}'>
			<c:if test='${!empty storicoUffint}'>
			openPopUpCustom("href=gene/sto_uffint/sto_uffint-scheda-popup.jsp&modo=NUOVO", "storicoUffint", 650, 600, 1, 1);
			</c:if>
		</c:if>
	</c:if>

</gene:javaScript>