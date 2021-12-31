<%/*
       * Created on 09-mar-2016
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" idMaschera="W_CONFIG-scheda" schema="GENE">

	<c:set var="entita" value="W_CONFIG"/>
	<gene:setString name="titoloMaschera" value='Configurazione'/>
	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>

	<c:set var="risultato" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetCaratteristicheProprietaFunction",pageContext,gene:getValCampo(key, "CHIAVE"))}'/>
	<c:if test="${esisteProprietaDB eq 'false'}">
		<gene:redefineInsert name="schedaModifica"></gene:redefineInsert>
		<gene:redefineInsert name="pulsanteModifica"></gene:redefineInsert>
	</c:if>
	
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true">
			<gene:pagina title="Configurazione" idProtezioni="W_CONFIG">
				<gene:formScheda entita="W_CONFIG" gestisciProtezioni="true" gestore="it.eldasoft.gene.tags.gestori.submit.GestoreW_CONFIG" >
					
					<gene:campoScheda>
						<td colspan="2">
							<div style="border-bottom: 1px dotted #808080; padding: 5px; background-color: #EFEFEF;"><b>Legenda</b></div>
							<div style="background-color: #EFEFEF; padding: 5px;">
								<b>Valore effettivo:</b> &egrave; il valore effettivamente utilizzato dall'applicativo. 
								<br>
								<b>Provenienza:</b> indica la provenienza del valore effettivo della configurazione.
								<br>
								<br>
								Se la configurazione &egrave; definita in un <b>file</b> di propriet&agrave; (global.properties) il suo valore 
								ha priorit&agrave; sull'analoga configurazione definita in <b>database</b> (tabella w_config),
								inoltre il suo valore &egrave; modificabile solamente agendo sul file di provenienza.
								<br>
							</div>
						</td>
					</gene:campoScheda>
					
					<gene:campoScheda campo="CODAPP" modificabile="false" />
					<gene:campoScheda campo="SEZIONE" modificabile="false"/>
					<gene:campoScheda title="Configurazione" campo="CHIAVE" modificabile="false"/>
					<gene:campoScheda campo="DESCRIZIONE" gestore="it.eldasoft.gene.tags.decorators.campi.gestori.GestoreCampoNote" modificabile="false"/>
					<gene:campoScheda title="Valore criptato?" campo="CRIPTATO" modificabile="false"/>
					
					<gene:campoScheda title="Valore in chiaro" 
						campo="PLAINTEXT" visibile="${datiRiga.W_CONFIG_CRIPTATO eq '1' && modo ne 'VISUALIZZA'}" campoFittizio="true" definizione="T500;0">
						<div style="padding-top: 3px; padding-right: 20px;">
							Il valore in chiaro, indicato in questa campo, verrà memorizzato in forma criptata al salvataggio della configurazione.
						</div>
					</gene:campoScheda>
					
					<c:choose>
						<c:when test="${datiRiga.W_CONFIG_CRIPTATO eq '1' && empty valoreEffettivoProprieta}">
							<gene:campoScheda title="Valore effettivo" campo="VALORE" value="" visibile="${modo eq 'VISUALIZZA'}"/>
						</c:when>
						<c:when test="${datiRiga.W_CONFIG_CRIPTATO eq '1' && !empty valoreEffettivoProprieta}">
							<gene:campoScheda title="Valore effettivo" campo="VALORE" value="**********" visibile="${modo eq 'VISUALIZZA'}"/>
						</c:when>
						<c:otherwise>
							<gene:campoScheda title="Valore effettivo" campo="VALORE" value="${valoreEffettivoProprieta}"/>						
						</c:otherwise>
					</c:choose>
					
					<gene:campoScheda title="Provenienza" campoFittizio="true">
						<c:choose>
							<c:when test="${esisteProprieta eq 'false'}">
								<span style="border-left: 8px solid #FFAA00">&nbsp;</span>Database<br>
								La configurazione e' stata inserita nella tabella W_CONFIG successivamente all'avvio dell'applicativo, &egrave; necessario riavviare l'applicativo.
							</c:when>
							<c:when test="${esisteProprieta eq 'true' && esisteProprietaDB eq 'true'}">
								<span style="border-left: 8px solid #00C621">&nbsp;</span>Database
							</c:when>
							<c:otherwise>
								<span style="border-left: 8px solid #FF0000">&nbsp;</span>File
							</c:otherwise>
						</c:choose>
					</gene:campoScheda>
					<gene:campoScheda>
						<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
					</gene:campoScheda>
				</gene:formScheda>
			</gene:pagina>
		</gene:formPagine>
	</gene:redefineInsert>
	
	<gene:javaScript>
		$("#jsPopUpW_CONFIG_CODAPP").hide();
		$("#jsPopUpW_CONFIG_SEZIONE").hide();
		$("#jsPopUpW_CONFIG_CHIAVE").hide();
		$("#jsPopUpW_CONFIG_DESCRIZIONE").hide();
		$("#jsPopUpW_CONFIG_VALORE").hide();
		$("#jsPopUpW_CONFIG_CRIPTATO").hide();
	</gene:javaScript>
	
</gene:template>




