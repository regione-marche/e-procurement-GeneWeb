<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Form di ricerca dei tecnici */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>

<gene:template file="ricerca-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="TrovaImprese" >

	<gene:setString name="titoloMaschera" value="Ricerca imprese"/>
	<c:set var="esisteElenchiOperatori" value='${gene:callFunction("it.eldasoft.gene.tags.functions.EsisteElenchiOperatoriFunction", pageContext)}' />

	<gene:redefineInsert name="corpo">
  	<gene:formTrova entita="IMPR" gestisciProtezioni="true" >
			<gene:gruppoCampi idProtezioni="Gen">
				<tr><td colspan="3"><b>Dati generali</b></td></tr>
				<gene:campoTrova campo="CODIMP"/>
				<gene:campoTrova campo="NOMEST"/>
				<gene:campoTrova campo="CFIMP"/>
				<gene:campoTrova campo="PIVIMP"/>
				<gene:campoTrova campo="TIPIMP"/>
				<gene:campoTrova campo="ISMPMI"/>
				<gene:campoTrova campo="PROIMP"/>
				<gene:campoTrova campo="LOCIMP"/>
				<gene:campoTrova campo="EMAIIP"/>
				<gene:campoTrova campo="EMAI2IP"/>
				<gene:campoTrova campo="NOMLEG" entita="IMPLEG" 
					where="IMPR.CODIMP = IMPLEG.CODIMP2" title="Legale rappresentante"/>
				<gene:campoTrova campo="NOMDTE" entita="IMPDTE" 
					where="IMPR.CODIMP = IMPDTE.CODIMP3" title="Direttore tecnico"/>
				<gene:campoTrova campo="CATISC" entita="CATE"
					where="IMPR.CODIMP = CATE.CODIMP1" title="Codice categoria SOA"/>
				<gene:campoTrova campo="NUMCLA" entita="CATE"
					where="IMPR.CODIMP = CATE.CODIMP1" title="Classifica categoria SOA"/>
				<c:if test='${esisteElenchiOperatori eq "true"}' >
					<gene:campoTrova campo="CODCAT" entita="V_CATE_ELENCHI"
						where="IMPR.CODIMP = V_CATE_ELENCHI.CODIMP" title="Codice categoria iscrizione elenchi operatori"/>
					<gene:campoTrova campo="DESCAT" entita="V_CATE_ELENCHI"
						where="IMPR.CODIMP = V_CATE_ELENCHI.CODIMP" title="Descrizione categoria iscrizione elenchi operatori"/>
					<gene:campoTrova campo="NUMCLA" entita="V_CATE_ELENCHI"
						where="IMPR.CODIMP = V_CATE_ELENCHI.CODIMP" title="Classifica categoria iscrizione elenchi operatori" gestore="it.eldasoft.gene.tags.gestori.decoratori.GestoreCampoClassificaCategoriaRicerca"/>
					<gene:fnJavaScriptTrova funzione="gestioneNumcla('#Campo15#')" elencocampi="Campo15" esegui="true" />
				</c:if>				
				<c:if test='${fn:contains(listaOpzioniDisponibili, "OP127#")}'>
					<gene:campoTrova campo="CGENIMP"/>
				</c:if>
			</gene:gruppoCampi>
			<gene:gruppoCampi idProtezioni="NOTE" visibile='${gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.G_NOTEAVVISI") }'>
				<tr><td colspan="3"><b>Note e avvisi</b></td></tr>
				<gene:campoTrova campo="TIPONOTA" entita="G_NOTEAVVISI" 
					where="IMPR.CODIMP = G_NOTEAVVISI.NOTEKEY1 and G_NOTEAVVISI.NOTEPRG = 'PG' and G_NOTEAVVISI.NOTEENT='IMPR'" />
				<gene:campoTrova campo="STATONOTA" entita="G_NOTEAVVISI" 
					where="IMPR.CODIMP = G_NOTEAVVISI.NOTEKEY1 and G_NOTEAVVISI.NOTEPRG = 'PG' and G_NOTEAVVISI.NOTEENT='IMPR'" />
				<gene:campoTrova campo="TITOLONOTA" entita="G_NOTEAVVISI" 
					where="IMPR.CODIMP = G_NOTEAVVISI.NOTEKEY1 and G_NOTEAVVISI.NOTEPRG = 'PG' and G_NOTEAVVISI.NOTEENT='IMPR'" />
				<gene:campoTrova campo="DATANOTA" entita="G_NOTEAVVISI" 
					where="IMPR.CODIMP = G_NOTEAVVISI.NOTEKEY1 and G_NOTEAVVISI.NOTEPRG = 'PG' and G_NOTEAVVISI.NOTEENT='IMPR'" />
				<gene:campoTrova campo="DATACHIU" entita="G_NOTEAVVISI" 
					where="IMPR.CODIMP = G_NOTEAVVISI.NOTEKEY1 and G_NOTEAVVISI.NOTEPRG = 'PG' and G_NOTEAVVISI.NOTEENT='IMPR'" />
			</gene:gruppoCampi>
		</gene:formTrova>

		<gene:javaScript>
			function gestioneNumcla(numcla){

			<c:if test='${gene:checkProt(pageContext, "COLS.VIS.GARE.V_CATE_ELENCHI.NUMCLA")}'>
				var tipoAppalto = "";
				var filtroClassificaNew = "";

				var index = document.getElementById("Campo15").selectedIndex;
				if (index>0){
					tipoAppalto = document.getElementById("Campo15").options[index].text.substr(0,1);
				}
								
				var filtroClassificaOld = "IMPR.CODIMP = V_CATE_ELENCHI.CODIMP";
				if (tipoAppalto!=null && tipoAppalto!=""){
					filtroClassificaNew=filtroClassificaOld+" and V_CATE_ELENCHI.TIPCAT="+tipoAppalto;
				} else {
					filtroClassificaNew=filtroClassificaOld;
				}
				setValue("Campo16_where",filtroClassificaNew);
				setValue("Campo15_where",filtroClassificaNew);
				setValue("Campo14_where",filtroClassificaNew);
			</c:if>
			}
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
