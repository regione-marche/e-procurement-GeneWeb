<%
			/*
 
				Descrizione:
					Interno delle scheda di documenti_verifichea
				Creato da:
					Cristian Febas
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>


<c:set var="idVerifica" value='${gene:getValCampo(keyParent,"ID")}' scope="request" />
<c:set var="modal" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GestioneVerificheFunction", pageContext, idVerifica)}'/>
<c:set var="idDocumentoVerifica" value='${gene:getValCampo(key,"ID")}' scope="request" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:choose>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou228#")}'>
		<c:set var="opzVerifiche" value="ou228" />
	</c:when>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou227#")}'>
		<c:set var="opzVerifiche" value="ou227" />
	</c:when>
	<c:otherwise>
		<c:set var="opzVerifiche" value="" />
	</c:otherwise>
</c:choose>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="DOCUMENTI_VERIFICHE-scheda">
<gene:setString name="titoloMaschera" value="Dettaglio documento verifica (ID DOC VERIFICA = ${idDocumentoVerifica})" />

<gene:redefineInsert name="corpo">
	<gene:formScheda entita="DOCUMENTI_VERIFICHE" gestisciProtezioni="true"  gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreDocumentiVerifiche">
		<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
		<c:if test='${opzVerifiche ne "ou228"}'>
			<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		</c:if>
		<gene:campoScheda campo="ID" visibile="false" />
		<gene:campoScheda campo="ID_VERIFICA" visibile="false" />
		<gene:campoScheda campo="TIPO" modificabile="false"/>
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
			chiave="DOCUMENTI_VERIFICHE_CODTIM"
			where="V_SOGGETTI_VERIFICHE.CODIMP='${requestScope.codimp}'"
			inseribile="false">
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
		<gene:campoScheda campo="ISARCHI" visibile="${datiRiga.DOCUMENTI_VERIFICHE_ISARCHI eq '1'}"/>
		
		<gene:campoScheda campo="IDPRG" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" />
		<gene:campoScheda campo="IDDOCDIG" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" />
		<gene:campoScheda campo="DIGDESDOC" visibile="false" entita="W_DOCDIG"
		 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" />
		 
		<c:if test='${opzVerifiche eq "ou227" || opzVerifiche eq "ou228"}' >
			<gene:campoScheda campo="DIGNOMDOC" entita="W_DOCDIG" visibile='${!(empty datiRiga.W_DOCDIG_IDDOCDIG && modo eq "MODIFICA")}' modificabile="false"
		 	where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" href="javascript:visualizzaFileDIGOGG('${datiRiga.DOCUMENTI_VERIFICHE_ID}', '${datiRiga.W_DOCDIG_DIGNOMDOC}','${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}');" />
			<gene:campoScheda title="Nome file"  visibile='${empty datiRiga.W_DOCDIG_IDDOCDIG && modo eq "MODIFICA"}'>
				<input type="file" name="selezioneFile" id="selezioneFile" onchange="javascript:scegliFile(this.value);" class="file" size="70" onkeydown="return bloccaCaratteriDaTastiera(event);" value=''/>
			</gene:campoScheda>
			<gene:campoScheda title="File da allegare" campo="FILEDAALLEGARE" campoFittizio="true" visibile="false" definizione="T70;0" />
		</c:if>
			
		<c:if test='${opzVerifiche eq "ou228"}'>
			<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
		</c:if>
		
		<gene:fnJavaScriptScheda funzione='calcoloDataScadenza("#DOCUMENTI_VERIFICHE_TIPO#","#DOCUMENTI_VERIFICHE_GG_VALIDITA#","#DOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA#","#DOCUMENTI_VERIFICHE_DATA_EMISSIONE#","#DOCUMENTI_VERIFICHE_ESITO_VERIFICA_DOC#")'
		elencocampi='DOCUMENTI_VERIFICHE_GG_VALIDITA;DOCUMENTI_VERIFICHE_TIPO;DOCUMENTI_VERIFICHE_DATA_INVIO_RICHIESTA;DOCUMENTI_VERIFICHE_DATA_EMISSIONE;DOCUMENTI_VERIFICHE_ESITO_VERIFICA_DOC' esegui="true" />
		
	</gene:formScheda>
</gene:redefineInsert>
<gene:javaScript>

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
			showObj("rowDOCUMENTI_VERIFICHE_DATA_SILENZIO_ASSENSO", false);
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


	function visualizzaFileDIGOGG(c0acod, dignomdoc,idprg,iddocdig) {
		var uffint= "${sessionScope.uffint}";
		if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verrà apportata alla copia locale ma non all\'originale. Continuare?"))
		{
			var href = "${pageContext.request.contextPath}/VisualizzaFileDIGOGG.do";
			document.location.href = href+"?"+csrfToken+"&c0acod=" + c0acod + "&dignomdoc=" + dignomdoc + "&digent=" + "DOCUMENTI_VERIFICHE" + "&uffint=" + uffint;
		}
	}

</gene:javaScript>

</gene:template>
