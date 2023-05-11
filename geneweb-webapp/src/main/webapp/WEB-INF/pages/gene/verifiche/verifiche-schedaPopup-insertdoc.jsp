<%
/*
 * Created on: 05-08-2010
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 *
 * Popup per campi ulteriori relativi alla ditta presenta nella lista delle
 * fasi di ricezione in analisi
 */
%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>



<c:choose>
	<c:when test='${not empty param.idVerifica}'>
		<c:set var="idVerifica" value="${param.idVerifica}" />
	</c:when>
	<c:otherwise>
		<c:set var="idVerifica" value="${idVerifica}" />
	</c:otherwise>
</c:choose>

<c:set var="modal" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GestioneVerificheFunction", pageContext, param.idVerifica)}'/>

<div style="width:97%;">
<gene:template file="popup-template.jsp">

	<gene:redefineInsert name="gestioneHistory" />
	<gene:redefineInsert name="addHistory" />
	<gene:redefineInsert name="corpo">
		<gene:setString name="titoloMaschera" value='Inserisci documentazione verifica' />
		<gene:formScheda entita="DOCUMENTI_VERIFICHE" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreDocumentiVerifiche" >
			<gene:campoScheda campo="ID"  visibile="false"/>
			<gene:campoScheda campo="ID_VERIFICA" value="${idVerifica}" visibile="false"/>
			<gene:campoScheda campo="TIPO" obbligatorio="true"/>
			<gene:campoScheda campo="DESCRIZIONE" />
			<gene:campoScheda campo="PROTOCOLLO_RICHIESTA" />
			<gene:campoScheda campo="NUMERO_PRATICA" />
			<gene:campoScheda campo="GG_VALIDITA" defaultValue="${ggvalidita}" modificabile="false" />
			<gene:campoScheda campo="DATA_INVIO_RICHIESTA" />
			<gene:campoScheda campo="DATA_SILENZIO_ASSENSO" modificabile="false"/>
			<gene:campoScheda campo="DATA_EMISSIONE" />
			<gene:campoScheda campo="DATA_SCADENZA" modificabile="false"/>
			<gene:campoScheda campo="MODALITA" />
			
			<gene:archivio titolo="Soggetti"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.DOCUMENTI_VERIFICHE.CODTIM"),"gene/verifiche/soggetti-lista-popup.jsp","")}'
				scheda=''
				schedaPopUp=''
				campi="V_SOGGETTI_VERIFICHE.CODTIM;V_SOGGETTI_VERIFICHE.NOMTIM"
				functionId="default"
				parametriWhere="T:${requestScope.codimp}"
				chiave="DOCUMENTI_VERIFICHE_CODTIM" >
				<gene:campoScheda campo="CODTIM" title="Codice soggetto" entita="DOCUMENTI_VERIFICHE" />
				<gene:campoScheda campo="NOMTIM" title="Nome soggetto"
					entita="V_IMPR_SOGGETTI"
					where="DOCUMENTI_VERIFICHE.CODTIM=V_IMPR_SOGGETTI.CODTIM"
					modificabile='${gene:checkProt(pageContext, "COLS.MOD.GARE.TORN.CODRUP")}'
					visibile='${gene:checkProt(pageContext, "COLS.VIS.GARE.TORN.CODRUP")}' />
			</gene:archivio>
			<gene:campoScheda campo="NOTE_VERIFICA_DOC" />
			<gene:campoScheda campo="ESITO_VERIFICA_DOC" />
			<gene:campoScheda campo="NOTE_ESITO_VERIFICA_DOC" />
			<gene:campoScheda campo="ISARCHI" visibile="false"/>
			<gene:campoScheda title="Nome file" >
				<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" value=''/>
			</gene:campoScheda>
			<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />
			
			<gene:campoScheda>
				<td class="comandi-dettaglio" colSpan="2">
					<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:window.close();">&nbsp;
				</td>
			</gene:campoScheda>
			
			<input type="hidden" name="idVerifica" value="${idVerifica}"/>
			
			<gene:fnJavaScriptScheda funzione='calcoloDataScadenza("#DOCUMENTI_VERIFICHE_TIPO#","#DOCUMENTI_VERIFICHE_GG_VALIDITA#","#DOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA#","#DOCUMENTI_VERIFICHE_DATA_EMISSIONE#","#DOCUMENTI_VERIFICHE_ESITO_VERIFICA_DOC#")'
			elencocampi='DOCUMENTI_VERIFICHE_GG_VALIDITA;DOCUMENTI_VERIFICHE_TIPO;DOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA;DOCUMENTI_VERIFICHE_DATA_EMISSIONE;DOCUMENTI_VERIFICHE_ESITO_VERIFICA_DOC' esegui="true" />
			
		</gene:formScheda>
	</gene:redefineInsert>

	<gene:javaScript>
	document.forms[0].jspPathTo.value="gene/verifiche/verifiche-schedaPopup-insertdoc.jsp";
	var openerKeyParent = window.opener.document.forms[0].keyParent.value;
	
	var campiChiave = openerKeyParent.split(";");
	
	document.forms[0].encoding="multipart/form-data";
	
	function scegliFile(valore) {
		selezioneFile = document.getElementById("selezioneFile").value;
		var lunghezza_stringa = selezioneFile.length;
		var posizione_barra = selezioneFile.lastIndexOf("\\");
		var nome = selezioneFile.substring(posizione_barra+1,lunghezza_stringa).toUpperCase();
		if(nome.length>100){
			alert("Il nome del file non può superare i 100 caratteri!");
			document.getElementById("selezioneFile").value="";
			setValue("FILEDAALLEGARE","");
		}else{
			setValue("FILEDAALLEGARE" ,nome);
		}
		
	}
	
	function calcoloDataScadenza(tipo,giornidasommare,dataInvioRichiesta,dataEmissione,esito){
	 if(tipo==1){
			showObj("rowDOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA", true);	
			showObj("rowDOCUMENTI_VERIFICHE_DATA_SILENZIO_ASSENSO", true);
			showObj("rowDOCUMENTI_VERIFICHE_DATA_EMISSIONE", false);
			setValue("DOCUMENTI_VERIFICHE_DATA_EMISSIONE", "" );
			setValue("DOCUMENTI_VERIFICHE_DATA_SCADENZA", "" );
			if(dataInvioRichiesta!='' && dataInvioRichiesta!= null){
				//calcolo
				var dataSplittata = dataInvioRichiesta.split("/");
				var newData = (dataSplittata[1] + "/" + dataSplittata[0] + "/" + dataSplittata[2]);
			    var dataPartenza = new Date(newData);
				var operazione = "SOMMA";
				var dataFutura = ritornaData(dataPartenza,30,operazione);
				var giorno = dataFutura.getDate();
				if(giorno<10)
					giorno = "0" + giorno;
				var mese = parseInt(dataFutura.getMonth()+1);
				if (mese<10)
					mese = "0" + mese;
				var anno = dataFutura.getFullYear();
				setValue("DOCUMENTI_VERIFICHE_DATA_SILENZIO_ASSENSO", giorno + "/" + mese + "/" + anno);
				var dataFutura = ritornaData(dataFutura,giornidasommare,operazione);
				var giorno = dataFutura.getDate();
				if(giorno<10)
					giorno = "0" + giorno;
				var mese = parseInt(dataFutura.getMonth()+1);
				if (mese<10)
					mese = "0" + mese;
				var anno = dataFutura.getFullYear();
				if(esito==6){
					setValue("DOCUMENTI_VERIFICHE_DATA_SCADENZA", "");
				}else{
					setValue("DOCUMENTI_VERIFICHE_DATA_SCADENZA", giorno + "/" + mese + "/" + anno);
				}
			}
	 }
	 if(tipo==2){
			showObj("rowDOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA", false);
			setValue("DOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA", "" );
			showObj("rowDOCUMENTI_VERIFICHE_DATA_SILENZIO_ASSENSO", false);
			setValue("DOCUMENTI_VERIFICHE_DATA_SILENZIO_ASSENSO", "" );
			setValue("DOCUMENTI_VERIFICHE_DATA_SCADENZA", "" );
			showObj("rowDOCUMENTI_VERIFICHE_DATA_EMISSIONE", true);
			if(dataEmissione!='' && dataEmissione!= null){
				//calcolo
				var dataSplittata = dataEmissione.split("/");
				var newData = (dataSplittata[1] + "/" + dataSplittata[0] + "/" + dataSplittata[2]);
			    var dataPartenza = new Date(newData);
				var operazione = "SOMMA";
				var dataFutura = ritornaData(dataPartenza,giornidasommare,operazione);
				var giorno = dataFutura.getDate();
				if(giorno<10)
					giorno = "0" + giorno;
				var mese = parseInt(dataFutura.getMonth()+1);
				if (mese<10)
					mese = "0" + mese;
				var anno = dataFutura.getFullYear();
				if(esito==6){
					setValue("DOCUMENTI_VERIFICHE_DATA_SCADENZA", "");
				}else{
					setValue("DOCUMENTI_VERIFICHE_DATA_SCADENZA", giorno + "/" + mese + "/" + anno);
				}
			}
	 }
	}

	function ritornaData(dataPartenza, giornidasommare,operazione){
		// millisecondi trascorsi fino ad ora dal 1/1/1970
		var millisecondiPartenza = dataPartenza.getTime();
		
		// valore in millisecondi dei giorni da aggiungere o sottrarre
		var millisecondi = 24 * 60 * 60 * 1000 * giornidasommare;
		
		//millisecondi alla data finale
		var milliseTotali = 0;
		if(operazione == "SOMMA"){
			milliseTotali = millisecondi + millisecondiPartenza;
		}else{
			milliseTotali = millisecondiPartenza - millisecondi;
		}
		
		//data finale in millisecondi
		var dataFutura = new Date(milliseTotali- new Date().getTimezoneOffset()*60*1000);
		
		return dataFutura;
	}
	
	
	<c:if test='${not empty RISULTATO and RISULTATO eq "OK"}' >
		window.opener.document.forms[0].pgSort.value = "";
		window.opener.document.forms[0].pgLastSort.value = "";
		window.opener.document.forms[0].pgLastValori.value = "";
		window.opener.bloccaRichiesteServer();
		window.opener.listaVaiAPagina(0);
		window.close();
	</c:if>


	
	
	function showCampi(valore){
		if(valore == 1) {
			showObj("rowDOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA", true);
			setValue("DOCUMENTI_VERIFICHE_DATA_EMISSIONE","" );
			showObj("rowDOCUMENTI_VERIFICHE_DATA_EMISSIONE", false);
		}else if(valore == 2){
			setValue("DOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA","" );
			showObj("rowDOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA", false);	
			showObj("rowDOCUMENTI_VERIFICHE_DATA_EMISSIONE", true);
		}else{
			showObj("rowDOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA", true);
			showObj("rowDOCUMENTI_VERIFICHE_DATA_EMISSIONE", true);
		}
	}
	
		
	
	
	
	
	
	</gene:javaScript>
</gene:template>
</div>