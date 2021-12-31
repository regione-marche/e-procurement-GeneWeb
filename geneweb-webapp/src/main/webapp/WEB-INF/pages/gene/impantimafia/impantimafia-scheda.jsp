<%
/*
 * Created on: 22-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Tab raggrupamento della scheda dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="IMPANTIMAFIA-scheda" >
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"IMPANTIMAFIA")}' />
	<gene:redefineInsert name="noteAvvisi" />
	<gene:redefineInsert name="corpo">

		<gene:formScheda entita="IMPANTIMAFIA" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPANTIMAFIA">

			<c:set var="codiceImpresaPadre" value='${fn:substringAfter(keyParent, ":")}' />

			<gene:gruppoCampi idProtezioni="GEN" >
				<gene:campoScheda  nome="GEN">
					<td colspan="2"><b>Dati generali</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="CODIMP" visibile="false" value="${codiceImpresaPadre}" />
				<gene:campoScheda campo="NUMANT" visibile="false" />
				<gene:campoScheda campo="NPRICH" />
				<gene:campoScheda campo="ENTRIC" />
				<gene:campoScheda campo="ENTALT" />
				<gene:campoScheda campo="DRICAC" />
				<gene:campoScheda campo="TIPRIC" />
				<gene:campoScheda campo="TIPGEN" />
				<gene:campoScheda campo="NGARA" />
				<gene:campoScheda campo="CODCIG" />
				<gene:campoScheda campo="NOT_GAR" />
				<gene:campoScheda campo="IMPAPP" />
				<gene:campoScheda campo="FORPOL"/>
				<gene:campoScheda campo="FORALT" />
				<gene:campoScheda campo="DRISFP"/>
				<gene:campoScheda campo="NRISFP" />
				<gene:campoScheda campo="DRIUFP"/>
				<gene:campoScheda campo="ESTGIA"/>
				<gene:campoScheda campo="ATTGIA"/>
				<gene:campoScheda campo="DATGIA"/>
				<gene:campoScheda campo="TIPPRO"/>
				<gene:campoScheda campo="DATPRO"/>
				<gene:campoScheda campo="NPROVV" />
			</gene:gruppoCampi>

			<gene:gruppoCampi idProtezioni="RIESAME" >
				<gene:campoScheda  nome="RIESAME">
					<td colspan="2"><b>Riesame</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="DRIESA"/>
				<gene:campoScheda campo="NRIESA" />
				<gene:campoScheda campo="DRIEFP"/>
				<gene:campoScheda campo="NRIEFP" />
				<gene:campoScheda campo="DACCAT"/>
				<gene:campoScheda campo="NACCAT" />
				<gene:campoScheda campo="DAUTAT"/>
				<gene:campoScheda campo="NAUTAT" />
			</gene:gruppoCampi>

			<gene:gruppoCampi idProtezioni="TAR" >
				<gene:campoScheda  nome="TAR">
					<td colspan="2"><b>Ricorso al TAR</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="RICTAR"/>
				<gene:campoScheda campo="DATTAR"/>
				<gene:campoScheda campo="NUMTAR"/>
				<gene:campoScheda campo="DCDEDU"/>
				<gene:campoScheda campo="NCDEDU"/>
				<gene:campoScheda campo="DRICATT"/>
				<gene:campoScheda campo="NRICATT"/>
				<gene:campoScheda campo="DTRAATT"/>
				<gene:campoScheda campo="NTRAATT"/>
				<gene:campoScheda campo="DPROTAR"/>
				<gene:campoScheda campo="NPROTAR"/>
				<gene:campoScheda campo="PROTAR"/>
				<gene:campoScheda campo="ALTTAR"/>
			</gene:gruppoCampi>

			<gene:gruppoCampi idProtezioni="CONS" >
				<gene:campoScheda  nome="CONS">
					<td colspan="2"><b>Ricorso al Consiglio di Stato</b></td>
				</gene:campoScheda>
				<gene:campoScheda campo="RICCON"/>
				<gene:campoScheda campo="DATCON"/>
				<gene:campoScheda campo="NUMCON"/>
				<gene:campoScheda campo="ATTCON"/>
				<gene:campoScheda campo="DPROCON"/>
				<gene:campoScheda campo="NPROCON"/>
				<gene:campoScheda campo="PROCON"/>
			</gene:gruppoCampi>

			<gene:campoScheda>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
			</gene:campoScheda>

			<gene:fnJavaScriptScheda funzione='modifyTIPRIC("#IMPANTIMAFIA_TIPRIC#")' elencocampi="IMPANTIMAFIA_TIPRIC" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyENTRIC("#IMPANTIMAFIA_ENTRIC#")' elencocampi="IMPANTIMAFIA_ENTRIC" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyFORPOL("#IMPANTIMAFIA_FORPOL#")' elencocampi="IMPANTIMAFIA_FORPOL" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyESTGIA("#IMPANTIMAFIA_ESTGIA#")' elencocampi="IMPANTIMAFIA_ESTGIA" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyTIPPRO("#IMPANTIMAFIA_TIPPRO#")' elencocampi="IMPANTIMAFIA_TIPPRO" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyRICTAR("#IMPANTIMAFIA_RICTAR#")' elencocampi="IMPANTIMAFIA_RICTAR" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyPROTAR("#IMPANTIMAFIA_PROTAR#")' elencocampi="IMPANTIMAFIA_PROTAR" esegui="true" />
			<gene:fnJavaScriptScheda funzione='modifyRICCON("#IMPANTIMAFIA_RICCON#")' elencocampi="IMPANTIMAFIA_RICCON" esegui="true" />
			
		</gene:formScheda>

		<gene:javaScript>
			//Visualizzazione campi relativi all'appalto
			function modifyTIPRIC(valore){
				var vis = (valore==1);
				showObj("rowIMPANTIMAFIA_TIPGEN",vis);
				showObj("rowIMPANTIMAFIA_NGARA",vis);
				showObj("rowIMPANTIMAFIA_CODCIG",vis);
				if (!vis) {
					setValue("IMPANTIMAFIA_TIPGEN", "");
					setValue("IMPANTIMAFIA_NGARA", "");
					setValue("IMPANTIMAFIA_CODCIG", "");
				}
			}			
		
			//Se 'Ente richiedente' non è SAUP, non visualizza il campo 'Data riunione forze di polizia' 
			//ma visualizza il flag 'Estensione a GIA?' 
			function modifyENTRIC(valore){
				if (valore == 1 || valore == "") {
					showObj("rowIMPANTIMAFIA_DRIUFP",true);
					showObj("rowIMPANTIMAFIA_ESTGIA",false);
					setValue("IMPANTIMAFIA_ESTGIA", "");
				} else {
					showObj("rowIMPANTIMAFIA_DRIUFP",false);
					showObj("rowIMPANTIMAFIA_ESTGIA",true);
					setValue("IMPANTIMAFIA_DRIUFP", "");
				}
				if (valore == 99) {
					showObj("rowIMPANTIMAFIA_ENTALT",true);
				} else {
					showObj("rowIMPANTIMAFIA_ENTALT",false);
					setValue("IMPANTIMAFIA_ENTALT", "");
				}
			}
					
			//Visualizzazione campo descrittivo forze di polizia
			function modifyFORPOL(valore){
				var vis = (valore==99);
				showObj("rowIMPANTIMAFIA_FORALT",vis);
				if (!vis) {
					setValue("IMPANTIMAFIA_FORALT", "");
				}
			}			

			// Visualizzazione dei campi relativi a 'Estensione GIA'
			function modifyESTGIA(valore){
				var vis = (valore==1);
				showObj("rowIMPANTIMAFIA_ATTGIA",vis);
				showObj("rowIMPANTIMAFIA_DATGIA",vis);
				if (!vis) {
					setValue("IMPANTIMAFIA_ATTGIA", "");
					setValue("IMPANTIMAFIA_DATGIA", "");
				}
			}
						
			// Visualizzazione dei campi relativi al provvedimento Interdittivo
			function modifyTIPPRO(valore){
				var vis = (valore==1);
				showObj("rowRIESAME",vis);
				showObj("rowIMPANTIMAFIA_DRIESA",vis);
				showObj("rowIMPANTIMAFIA_NRIESA",vis);
				showObj("rowIMPANTIMAFIA_DRIEFP",vis);
				showObj("rowIMPANTIMAFIA_NRIEFP",vis);
				showObj("rowIMPANTIMAFIA_DACCAT",vis);
				showObj("rowIMPANTIMAFIA_NACCAT",vis);
				showObj("rowIMPANTIMAFIA_DAUTAT",vis);
				showObj("rowIMPANTIMAFIA_NAUTAT",vis);
				showObj("rowTAR",vis);
				showObj("rowIMPANTIMAFIA_RICTAR",vis);
 				if (!vis) {
					setValue("IMPANTIMAFIA_DRIESA", "");
					setValue("IMPANTIMAFIA_NRIESA", "");
					setValue("IMPANTIMAFIA_DRIEFP", "");
					setValue("IMPANTIMAFIA_NRIEFP", "");
					setValue("IMPANTIMAFIA_DACCAT", "");
					setValue("IMPANTIMAFIA_NACCAT", "");
					setValue("IMPANTIMAFIA_DAUTAT", "");
					setValue("IMPANTIMAFIA_NAUTAT", "");
					setValue("IMPANTIMAFIA_RICTAR", "");
				}
			}
					
			// Visualizzazione dei campi relativi al ricorso al TAR
			function modifyRICTAR(valore){
				var vis = (valore==1);
				showObj("rowIMPANTIMAFIA_DATTAR",vis);
				showObj("rowIMPANTIMAFIA_NUMTAR",vis);
				showObj("rowIMPANTIMAFIA_DCDEDU",vis);
				showObj("rowIMPANTIMAFIA_NCDEDU",vis);
				showObj("rowIMPANTIMAFIA_DRICATT",vis);
				showObj("rowIMPANTIMAFIA_NRICATT",vis);
				showObj("rowIMPANTIMAFIA_DTRAATT",vis);
				showObj("rowIMPANTIMAFIA_NTRAATT",vis);
				showObj("rowIMPANTIMAFIA_DPROTAR",vis);
				showObj("rowIMPANTIMAFIA_NPROTAR",vis);
				showObj("rowIMPANTIMAFIA_PROTAR",vis);
				showObj("rowCONS",vis);
				showObj("rowIMPANTIMAFIA_RICCON",vis);
 				if (!vis) {
					setValue("IMPANTIMAFIA_DATTAR", "");
					setValue("IMPANTIMAFIA_NUMTAR", "");
					setValue("IMPANTIMAFIA_DCDEDU", "");
					setValue("IMPANTIMAFIA_NCDEDU", "");
					setValue("IMPANTIMAFIA_DRICATT", "");
					setValue("IMPANTIMAFIA_NRICATT", "");
					setValue("IMPANTIMAFIA_DTRAATT", "");
					setValue("IMPANTIMAFIA_NTRAATT", "");
					setValue("IMPANTIMAFIA_DPROTAR", "");
					setValue("IMPANTIMAFIA_NPROTAR", "");
					setValue("IMPANTIMAFIA_PROTAR", "");
					setValue("IMPANTIMAFIA_RICCON", "");
				}
			}
					
			// Visualizzazione del campo 'Altra pronuncia del TAR'
			function modifyPROTAR(valore){
				var vis = (valore==3);
				showObj("rowIMPANTIMAFIA_ALTTAR",vis);
				if (!vis) {
					setValue("IMPANTIMAFIA_ALTTAR", "");
				}
			}

			// Visualizzazione dei campi relativi al ricorso al Consiglio di Stato
			function modifyRICCON(valore){
				var vis = (valore==1);
				showObj("rowIMPANTIMAFIA_DATCON",vis);
				showObj("rowIMPANTIMAFIA_NUMCON",vis);
				showObj("rowIMPANTIMAFIA_ATTCON",vis);
				showObj("rowIMPANTIMAFIA_DPROCON",vis);
				showObj("rowIMPANTIMAFIA_NPROCON",vis);
				showObj("rowIMPANTIMAFIA_PROCON",vis);
 				if (!vis) {
					setValue("IMPANTIMAFIA_DATCON", "");
					setValue("IMPANTIMAFIA_NUMCON", "");
					setValue("IMPANTIMAFIA_ATTCON", "");
					setValue("IMPANTIMAFIA_DPROCON", "");
					setValue("IMPANTIMAFIA_NPROCON", "");
					setValue("IMPANTIMAFIA_PROCON", "");
				}
			}

		</gene:javaScript>

	</gene:redefineInsert>
</gene:template>

