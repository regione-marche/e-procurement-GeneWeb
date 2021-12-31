
<%
	/*
	 * Created on 28-Apr-2008
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

<c:set var="isCodificaAutomatica" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.IsCodificaAutomaticaFunction", pageContext, "UTENT", "CODUTE")}'/>
<c:set var="valoreItalia" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.GetCodiceTabellatoDaDescrFunction", pageContext, "Ag010","Italia")}'/>

<c:set var="obbligatorioCodFisc" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloObbligatorietaCodFiscPivaFunction", pageContext, "UTENT","CFUTE")}'/>
<c:set var="obbligatorioPIVA" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloObbligatorietaCodFiscPivaFunction", pageContext, "UTENT","PIVAUTE")}'/>

<c:set var="obbligatoriaCorrettezzaCodFisc" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloCorrettezzaFunction", pageContext, "UTENT","CFUTE")}'/>
<c:set var="obbligatoriaCorrettezzaPIVA" value='${gene:callFunction3("it.eldasoft.gene.tags.functions.EsisteControlloCorrettezzaFunction", pageContext, "UTENT","PIVAUTE")}'/>
<gene:formScheda entita="UTENT"	gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreUTENT"	gestisciProtezioni="true">

	<gene:campoScheda campo="CODUTE" keyCheck="true" modificabile='${modoAperturaScheda eq "NUOVO"}' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" obbligatorio="${isCodificaAutomatica eq 'false'}" />
	<gene:campoScheda campo="TPPERS" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoPersona" />
	<gene:campoScheda campo="ENTEPUBB" />
	<gene:campoScheda campo="COGUTE" />
	<gene:campoScheda campo="NOMEUTE" />
	<gene:campoScheda campo="NOMUTE" obbligatorio="true">
		<gene:calcoloCampoScheda
			funzione='( ( "#UTENT_NOMUTE#"=="" || "#UTENT_NOMUTE#"==" #UTENT_NOMEUTE#" || "#UTENT_NOMUTE#"=="#UTENT_COGUTE#" || "#UTENT_NOMUTE#"=="#UTENT_COGUTE# ") ? "#UTENT_COGUTE# #UTENT_NOMEUTE#" : "#UTENT_NOMUTE#")'
			elencocampi="UTENT_COGUTE;UTENT_NOMEUTE" />
	</gene:campoScheda>
	<gene:campoScheda campo="INDUTE" />
	<gene:campoScheda campo="NCIUTE" />
	<gene:campoScheda campo="CAPUTE" />
	<gene:campoScheda campo="INDCOR" />
	<gene:campoScheda campo="PROUTE" />
	<gene:archivio titolo="Comuni" 
		obbligatorio="true"
		lista="gene/commons/istat-comuni-lista-popup.jsp" 
		scheda=""
		schedaPopUp="" campi="TB1.TABCOD3;TABSCHE.TABDESC"
		chiave=""
		where='${gene:if(!empty datiRiga.UTENT_PROUTE, gene:concat(gene:concat("TB1.TABCOD3 = \'", datiRiga.UTENT_PROUTE), "\'"), "")}'
		formName="formIstat" inseribile="false">
		<gene:campoScheda campoFittizio="true" campo="COM_PROOPE" definizione="T9" visibile="false" />
		<gene:campoScheda campo="LOCUTE" />
	</gene:archivio>
	<gene:campoScheda campo="NAZUTE" />
	<gene:campoScheda campo="SEXUTE" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSesso" />
	<gene:campoScheda campo="LNAUTE" />
	<gene:campoScheda campo="DNAUTE" />
	<gene:campoScheda campo="CFUTE" obbligatorio='${obbligatorioCodFisc}' title="Codice fiscale" >
		<gene:checkCampoScheda funzione='checkCodFisNazionalita("##",document.getElementById("UTENT_NAZUTE"))' obbligatorio="${obbligatoriaCorrettezzaCodFisc}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
	</gene:campoScheda>	
	<gene:campoScheda campo="PIVAUTE" obbligatorio='${obbligatorioPIVA}' title ='Partita I.V.A.' >
		<gene:checkCampoScheda funzione='checkPivaNazionalita("##",document.getElementById("UTENT_NAZUTE"))' obbligatorio="${obbligatoriaCorrettezzaPIVA}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
	</gene:campoScheda>
	
	<gene:campoScheda campo="TELUTE" />
	<gene:campoScheda campo="FAXUTE" />
	<gene:campoScheda campo="TELCEL" />
	<gene:campoScheda campo="EMAIL" />
	<gene:campoScheda campo="EMAIL2" />
	<gene:campoScheda campo="DOFUTE" />
	<gene:campoScheda campo="LEGRAP" title="Legale Rappresentante (*)"/>
	<gene:campoScheda campo="NOT_UTE" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" />
	<gene:campoScheda campo="WEBUSR" />
	<gene:campoScheda campo="WEBPWD" />
	<gene:campoScheda campo="SYSCON" visibile="false"/>
	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>

	<gene:fnJavaScriptScheda funzione="visualizzaTPPERS('#UTENT_TPPERS#')" elencocampi="UTENT_TPPERS" esegui="true" />
	<gene:fnJavaScriptScheda funzione="changeTPPERS('#UTENT_TPPERS#')" elencocampi="UTENT_TPPERS" esegui="false" />
	<gene:fnJavaScriptScheda funzione='changeProvincia("#UTENT_PROUTE#", "COM_PROOPE")' elencocampi='UTENT_PROUTE' esegui="false" />
	<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("UTENT_PROUTE", "#COM_PROOPE#")' elencocampi='COM_PROOPE' esegui="false" />
	<gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#UTENT_LOCUTE#", "${valoreItalia}", "UTENT_NAZUTE")' elencocampi='UTENT_LOCUTE' esegui="false"/>
	
	<gene:javaScript>
		function changeProvincia(provincia, nomeUnCampoInArchivio) {
			changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
			setValue("UTENT_LOCUTE", "", false);
		}
	</gene:javaScript>
</gene:formScheda>
<c:if test="${param.metodo == 'nuovo'}">
	<gene:javaScript>
		document.forms[0].UTENT_TPPERS.value="F";
	</gene:javaScript>
</c:if>
<gene:javaScript>
	function visualizzaTPPERS(tpers){
		document.getElementById("rowUTENT_NOMEUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_COGUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_SEXUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_DNAUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_LNAUTE").style.display = (tpers=='G' ? 'none':'');
		document.getElementById("rowUTENT_LEGRAP").style.display = (tpers=='G' ? '':'none');
		
		showObj("rowUTENT_ENTEPUBB", tpers=='G');
	}
	function changeTPPERS(tpers){
		if(tpers=='G'){
			document.forms[0].UTENT_NOMEUTE.value='';
			document.forms[0].UTENT_COGUTE.value='';
			document.forms[0].UTENT_SEXUTE.value='';
			document.forms[0].UTENT_DNAUTE.value='';
			document.forms[0].UTENT_LNAUTE.value='';
		}
		if(tpers=='F'){
			document.forms[0].UTENT_LEGRAP.value='';
			showObj("rowUTENT_ENTEPUBB", false);
			setValue("UTENT_ENTEPUBB", "");
		}
	}

	activeForm.addCheck("UTENT_LEGRAP","\"##\".length>0 || \"#UTENT_TPPERS#\" == \"F\"", "Il Campo \"Legale rappresentante\" è obbligatorio",true);

<c:if test='${!(modo eq "VISUALIZZA")}'>
 	var schedaConferma_Default = schedaConferma;
 	
 	function schedaConferma_Custom(){
 	 var obbligatoriaCorrettezzaCodFisc="${obbligatoriaCorrettezzaCodFisc }";
 	 var controlloOkCodFisc=true;
 	 clearMsg();
 	 
 	 if (obbligatoriaCorrettezzaCodFisc== "true"){
 	 	var selectNazionalita= document.getElementById("UTENT_NAZUTE");
 		var isItalia= isNazionalitaItalia(selectNazionalita);
 		
 		if(isItalia == "si"){
 			var codfisc=getValue("UTENT_CFUTE");
	 	 	controlloOkCodFisc=checkCodFis(codfisc);
	 	 	if(!controlloOkCodFisc){
	 	 		outMsg("Il valore del codice fiscale specificato non è valido", "ERR");
				onOffMsg();
	 	 	}
 		}
 	 	
 	 }
 	 
 	 var obbligatoriaCorrettezzaPIVA="${obbligatoriaCorrettezzaPIVA }";
 	 var controlloOkPIVA=true;
 	 if (obbligatoriaCorrettezzaPIVA=="true"){
 	 	var selectNazionalita= document.getElementById("UTENT_NAZUTE");
 		var isItalia= isNazionalitaItalia(selectNazionalita);
 		var piva=getValue("UTENT_PIVAUTE");
 		if(isItalia == "si"){
 			controlloOkPIVA=checkParIva(piva);
	 	}else{
 			controlloOkPIVA = true;
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
</gene:javaScript>