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
<c:set var="codiceIPA" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetCodiceIpaFunction", pageContext,codein)}'/>

<c:if test='${(! empty sessionScope.uffint && fn:contains(archiviFiltrati,"UFFINT")) || fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
		<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
</c:if>
<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
		<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
</c:if>

<gene:formScheda entita="UFFINT" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreUFFINT" >

	<c:if test='${modo eq "VISUALIZZA" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.SchedaUffint.Utenti") and fn:contains(listaOpzioniDisponibili, "OP101#") and (fn:contains(listaOpzioniUtenteAbilitate, "ou11#") and !fn:contains(listaOpzioniUtenteAbilitate, "ou12#"))}'>
		<gene:redefineInsert name="addToAzioni">
			<tr>
				<td class="vocemenulaterale" ><a 
				href="javascript:Utenti();" tabindex="1503"
				title="Utenti">Utenti</a></td>
			</tr>
		</gene:redefineInsert>
	</c:if>
	
	<gene:gruppoCampi idProtezioni="GEN">
		<gene:campoScheda>
			<td colspan="2"><b>Dati generali</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="CODEIN" keyCheck="true" modificabile='${modoAperturaScheda eq "NUOVO"}' gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoCodificaAutomatica" obbligatorio="${isCodificaAutomatica eq 'false'}" />
		<gene:campoScheda campo="NOMEIN" obbligatorio="true"/>
		<gene:campoScheda campo="TIPOIN"/>
		<gene:campoScheda campo="NATGIU"/>
		<gene:campoScheda campo="CFEIN" obbligatorio='${obbligatorioCodFisc}'>
			<gene:checkCampoScheda funzione='checkCodFisNazionalita("##",document.getElementById("UFFINT_CODNAZ"))' obbligatorio="${obbligatoriaCorrettezzaCodFisc}" messaggio='Il valore specificato non è valido.' onsubmit="false"/>
		</gene:campoScheda>	
		<gene:campoScheda campo="IVAEIN" obbligatorio='${obbligatorioPIVA}'>
			<gene:checkCampoScheda funzione='checkPivaNazionalita("##",document.getElementById("UFFINT_CODNAZ"))' obbligatorio="${obbligatoriaCorrettezzaPIVA}" messaggio='Il valore specificato non è valido (anteporre la sigla della nazione se estera).' onsubmit="false"/>
		</gene:campoScheda>
		<gene:campoScheda campo="VIAEIN"/>
		<gene:campoScheda campo="NCIEIN"/>
		<gene:campoScheda campo="PROEIN"/>
		<gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.UFFINT.CAPEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.UFFINT.PROEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.UFFINT.CITEIN") and gene:checkProt(pageContext, "COLS.MOD.GENE.TEIM.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="TB1.TABCOD3;TABSCHE.TABCOD4;TABSCHE.TABDESC;TABSCHE.TABCOD3" 
				chiave="" 
				where='${gene:if(!empty datiRiga.UFFINT_PROEIN, gene:concat(gene:concat("TB1.TABCOD3 = \'", datiRiga.UFFINT_PROEIN), "\'"), "")}'  
				formName="formIstat" 
				inseribile="false" >
			<gene:campoScheda campoFittizio="true" campo="COM_PROEIN" definizione="T9" visibile="false"/>
			<gene:campoScheda campo="CAPEIN"/>
			<gene:campoScheda campo="CITEIN"/>
			<gene:campoScheda campo="CODCIT"/>
		</gene:archivio>
		<gene:campoScheda campo="CODNAZ"/>
		<gene:campoScheda campo="TELEIN"/>
		<gene:campoScheda campo="FAXEIN"/>
		<gene:campoScheda campo="EMAIIN"/>
		<gene:campoScheda campo="EMAI2IN"/>
		<gene:campoScheda campo="INDWEB"/>
		<gene:campoScheda campo="PROFCO"/>
		<gene:campoScheda campo="IS_ORDINANTE_IPA" title="E' ufficio ordinante IPA?"  campoFittizio="true" definizione="T2;0;;SN"  value="${gene:if(!empty codiceIPA,'1','2')}"/>
		<gene:campoScheda campo="CODIPA"/>
		<gene:campoScheda campo="ENDPOINT_NSO" />
		<gene:campoScheda campo="CODCONS_NSO" />
		<gene:campoScheda campo="CODFE"/>
		<gene:campoScheda campo="ISCUC"/>
		<gene:campoScheda campo="CFANAC" />
		<gene:fnJavaScriptScheda funzione='visualizzaCFANAC("#UFFINT_ISCUC#")' elencocampi='UFFINT_ISCUC' esegui="true" />
	</gene:gruppoCampi>
	
	<c:if test='${modoAperturaScheda ne "NUOVO" || (modoAperturaScheda eq "NUOVO" && (empty sessionScope.uffint or !fn:contains(archiviFiltrati,"TECNI")))}'>
	
	<gene:gruppoCampi idProtezioni="RESP">
		<gene:campoScheda>
			<td colspan="2"><b>Responsabile ${fn:toLowerCase(nomeEntitaSingolaParametrizzata)}</b></td>
		</gene:campoScheda>
		<gene:archivio titolo="Tecnici" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.LAVO.UFFINT.CODRES"),"gene/tecni/tecni-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			campi="TECNI.CODTEC;TECNI.NOMTEC" 
			chiave="UFFINT_CODRES"
			formName="formResponsabile"
			inseribile="${empty sessionScope.uffint or !fn:contains(archiviFiltrati,'TECNI')}">
				<gene:campoScheda campo="CODRES" />
				<gene:campoScheda campo="NOMRES" />
		</gene:archivio>
		<gene:campoScheda campo="RESINI"/>
		<gene:campoScheda campo="RESFIN"/>
	</gene:gruppoCampi>
	
	<c:set var="codiceUfficio" value='${gene:getValCampo(key, "UFFINT.CODEIN")}' />
	<c:if test='${modo ne "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneAltrePersoneRiferimentoFunction" parametro="${key}" />
	</c:if>
		
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='G2FUNZ'/>
		<jsp:param name="chiave" value='${codiceUfficio}'/>
		<jsp:param name="archiviFiltrati" value='${archiviFiltrati}'/>
		<jsp:param name="nomeAttributoLista" value='personeRiferimento' />
		<jsp:param name="idProtezioni" value="APER" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/g2funz/altrePersone.jsp" />
		<jsp:param name="arrayCampi" value="'G2FUNZ_CODFUN_', 'G2FUNZ_INCFUN_','G2FUNZ_DINFUN_', 'G2FUNZ_DTEFUN_','TECNI_NOMTEC_'" />
		<jsp:param name="titoloSezione" value="Altra persona di riferimento" />
		<jsp:param name="titoloNuovaSezione" value="Altra persona di riferimento" />
		<jsp:param name="descEntitaVociLink" value="altra persona di riferimento" />
		<jsp:param name="msgRaggiuntoMax" value="e persone di riferimento" />
		<jsp:param name="sezioneListaVuota" value="false"/>
	</jsp:include>
	
	</c:if>
	
	<c:if test='${modo ne "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestionePuntiDiContattoFunction" parametro="${key}" />
	</c:if>
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
		<jsp:param name="sezioneListaVuota" value="false"/>
	</jsp:include>
	
	<c:if test='${modo ne "NUOVO"}'>
		<gene:callFunction obj="it.eldasoft.gene.tags.functions.GestioneSettoriFunction" parametro="${key}" />
	</c:if>
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='UFFSET'/>
		<jsp:param name="chiave" value='${codiceUfficio}'/>
		<jsp:param name="nomeAttributoLista" value='settori' />
		<jsp:param name="idProtezioni" value="UFFSET" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/uffset/settori.jsp" />
		<jsp:param name="arrayCampi" value="'UFFSET_CODEIN_', 'UFFSET_ID_','UFFSET_NOMSET_', 'UFFSET_DATFIN_'" />
		<jsp:param name="titoloSezione" value="Settore" />
		<jsp:param name="titoloNuovaSezione" value="Nuovo settore" />
		<jsp:param name="descEntitaVociLink" value="settore" />
		<jsp:param name="msgRaggiuntoMax" value="i settori" />
		<jsp:param name="sezioneListaVuota" value="false"/>
		<jsp:param name="usaContatoreLista" value="true"/>
	</jsp:include>
			
	<gene:gruppoCampi idProtezioni="ALTRI">
		<gene:campoScheda>
			<td colspan="2"><b>Altri dati</b></td>
		</gene:campoScheda>
		<gene:campoScheda campo="NUMICC"/>
		<gene:campoScheda campo="DATICC"/>
		<gene:archivio titolo="Province" obbligatorio="false" 
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.UFFINT.PROICC"),"gene/commons/istat-province-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="TABSCHE.TABCOD2;TABSCHE.TABDESC" 
				chiave="" 
				where="" 
				formName="formIstat1" 
				inseribile="false" >
			<gene:campoScheda campo="PROICC"/>
			<gene:campoScheda entita="TABSCHE" campo="TABDESC" title="Provincia" where="TABCOD='S2003' and TABSCHE.TABCOD1='07' and UFFINT.PROICC = TABSCHE.TABCOD2" modificabile='${gene:checkProt( pageContext, "COLS.MOD.GENE.UFFINT.PROICC")}' visibile='${gene:checkProt( pageContext, "COLS.VIS.GENE.UFFINT.PROICC")}'/>
		</gene:archivio>
		
		<gene:campoScheda campo="DOFEIN"/>
		<gene:campoScheda campo="IDAMMIN"/>
		<gene:campoScheda campo="NOTEIN" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote"/>
	</gene:gruppoCampi>
	
	<jsp:include page="/WEB-INF/pages/gene/attributi/sezione-attributi-generici.jsp">
		<jsp:param name="entitaParent" value="UFFINT"/>
	</jsp:include>
	
	<c:if test='${!fn:contains(listaOpzioniUtenteAbilitate, "ou214#")}' >
		<gene:campoScheda>	
			<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
		</gene:campoScheda>
	</c:if>

	<gene:fnJavaScriptScheda funzione='changeComune("#UFFINT_PROEIN#", "COM_PROEIN")' elencocampi='UFFINT_PROEIN' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("UFFINT_PROEIN", "#COM_PROEIN#")' elencocampi='COM_PROEIN' esegui="false"/>
	<gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#UFFINT_CITEIN#", "${valoreItalia}", "UFFINT_CODNAZ")' elencocampi='UFFINT_CITEIN' esegui="false"/>
	<gene:fnJavaScriptScheda funzione="gestioneOrdinanteIPA('#IS_ORDINANTE_IPA#')" elencocampi="IS_ORDINANTE_IPA" esegui="true" />
</gene:formScheda>

<gene:javaScript>
<c:if test='${! empty sessionScope.uffint && fn:contains(archiviFiltrati,"TECNI") && modoAperturaScheda eq "MODIFICA"}'>
	document.formResponsabile.archWhereLista.value="TECNI.CGENTEI='${datiRiga.UFFINT_CODEIN}'";
</c:if>

	function visualizzaCFANAC(valore){
		if(valore == "1"){
			showObj("rowUFFINT_CFANAC", true);
		} else {
			showObj("rowUFFINT_CFANAC", false);
			setValue("UFFINT_CFANAC","");
		}
	}

	function changeComune(provincia, nomeUnCampoInArchivio) {
		changeFiltroArchivioComuni(provincia, nomeUnCampoInArchivio);
		setValue("UFFINT_CAPEIN", "");
		setValue("UFFINT_CITEIN", "");
		setValue("UFFINT_CODCIT", "");
	}
	
	function Utenti(){
		document.location.href = '${pageContext.request.contextPath}/ApriPagina.do?'+csrfToken+'&href=gene/uffint/uffint-lista-utenti.jsp&key=${key}';
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
	 			controlloOkPIVA=checkPivaEuropea(piva);
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
		
	</c:if>
	
	function gestioneOrdinanteIPA(isoipa){
		if (isoipa == '1' ){
			$("#rowUFFINT_CODIPA").show();
			$("#rowUFFINT_ENDPOINT_NSO").show();
		} else {
			$("#rowUFFINT_CODIPA").hide();
			$("#UFFINT_CODIPA").val("");
			$("#rowUFFINT_ENDPOINT_NSO").hide();
			$("#UFFINT_ENDPOINT_NSO").val("");
		}
	}
	 
</gene:javaScript>