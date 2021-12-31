<%
/*
 * Created on: 04-apr-2017
 *
 *
 * Copyright (c) Maggioli S.p.A. - Divisione ELDASOFT
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di Maggioli S.p.A. - Divisione ELDASOFT
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di
 * aver prima formalizzato un accordo specifico con EldaSoft.
 /* Dettaglio configurazione codifica automatica */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!--N.B.: E' POSSIBILE RIDEFINIRE CONTROLLI CUSTOMIZZATI IMPLEMENTANDO UNA g_confcod_schedaCustom A LIVELLO DI SINGOLO PROGETTO-->

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_CONFCOD-scheda">
	<%-- Settaggio delle stringhe utilizzate nel template --%>
	<gene:setString name="titoloMaschera" value="Configurazione codifica automatica" />
	
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="G_CONFCOD" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreG_CONFCOD">
			
			<gene:campoScheda campo="NOMENT" visibile="false"/>
			<gene:campoScheda campo="NOMCAM" visibile="false"/>
			<gene:campoScheda campo="TIPCAM" visibile="false"/>
			<gene:campoScheda campo="CODAPP" visibile="false"/>
			<gene:campoScheda campo="DESCAM" modificabile="false" />
			<!-- codifica automatica attiva? campo fittizio calcolato. Vale 'sì' se CODCAL.G_CONFCOD valorizzato, vale 'no' altrimenti -->
			<gene:campoScheda campo="CHKCODIFICAATTIVA" title="Codifica automatica attiva?" campoFittizio="true" value="${gene:if(empty datiRiga.G_CONFCOD_CODCAL,0,1)}" definizione="T1;;;SN;" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoSiNoSenzaNull" />			
			<gene:campoScheda campo="CODCAL" >
					<gene:checkCampoScheda funzione='controlloChkCodifica("##")' obbligatorio="true" messaggio="Specificare un criterio di calcolo." 
										onsubmit="true"/>
			</gene:campoScheda>
			<gene:campoScheda campo="CONTAT" />	
				
			<gene:fnJavaScriptScheda funzione='gestioneCampoCHKCODIFICA("#CHKCODIFICAATTIVA#")' elencocampi='CHKCODIFICAATTIVA' esegui="true" />
						
			<gene:campoScheda>	
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>
			
			<gene:redefineInsert name="pulsanteNuovo" />
			
		</gene:formScheda>
		
		<!--N.B.: E' POSSIBILE RIDEFINIRE CONTROLLI CUSTOMIZZATI IMPLEMENTANDO UNA g_confcod_schedaCustom A LIVELLO DI SINGOLO PROGETTO-->
		<jsp:include page="/WEB-INF/pages/gene/g_confcod/g_confcod-schedaCustom.jsp" />
		
		<gene:javaScript>
			function gestioneCampoCHKCODIFICA(valore){
				//Quando impostato a 'sì', i campi 'Criterio di calcolo' e 'Contatore' (CODCAL, CONTAT.G_CONFCOD) sono visibili. 
				if(valore==1 ){
					showObj("rowG_CONFCOD_CODCAL",true);
					showObj("rowG_CONFCOD_CONTAT",true);
				}else{ //Quando impostato a 'no', il 'Criterio di calcolo' viene sbiancato e sia il 'Criterio' che il 'Contatore' non sono visibili
					setValue("G_CONFCOD_CODCAL","");
					showObj("rowG_CONFCOD_CODCAL",false);
					showObj("rowG_CONFCOD_CONTAT",false);
				}				
			}	
			
			//Impedire il salvataggio se il flag è impostato a 'sì' e non è stato specificato un criterio di calcolo.
			function controlloChkCodifica(codcal){
				var chkcodificaattiva = getValue("CHKCODIFICAATTIVA");
				if((codcal == '' || codcal == null) && chkcodificaattiva == 1){
				 	return false;
				}else{
				 	return true;
			 	}
			}		
			
		</gene:javaScript>
		
		
	</gene:redefineInsert>
	
	
	
</gene:template>
