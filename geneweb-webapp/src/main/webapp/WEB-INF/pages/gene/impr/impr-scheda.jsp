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
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:if test='${modo eq "VISUALIZZA" or modo eq "MODIFICA" or empty modo}' >
	<gene:sqlSelect nome="tipoImpresa" parametri='${key}' tipoOut="VectorString" >
		select TIPIMP from IMPR where CODIMP = #IMPR.CODIMP#
	</gene:sqlSelect>
	<gene:sqlSelect nome="flagSoggdurc" parametri='${key}' tipoOut="VectorString" >
		select SOGGDURC from IMPR where CODIMP = #IMPR.CODIMP#
	</gene:sqlSelect>
</c:if>
<c:set var="esisteElenchiOperatori" value='${gene:callFunction("it.eldasoft.gene.tags.functions.EsisteElenchiOperatoriFunction", pageContext)}' />

<c:set var="ag0087raggr" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "ag008.7.raggr")}'/>
<c:set var="ag0088raggr" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "ag008.8.raggr")}'/>
<c:set var="ag00812raggr" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "ag008.12.raggr")}'/>
<c:set var="isVerificaArt80" value='${gene:callFunction("it.eldasoft.gene.tags.functions.isVerificheInterneArt80Function", pageContext)}'/>

<c:set var="titleRaggruppamento" value="Raggruppamento"/>

<c:if test='${tipoImpresa[0] eq "7" or tipoImpresa[0] eq "8" or tipoImpresa[0] eq "12"}'>
	<c:set var="titleRaggruppamento" value="Professionisti"/>
</c:if>

<c:if test='${tipoImpresa[0] eq "2" or tipoImpresa[0] eq "11"}'>
	<c:set var="titleRaggruppamento" value="Consorziate"/>
</c:if>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ImprScheda" >
	<gene:setString name="titoloMaschera" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetTitleFunction",pageContext,"IMPR")}' />
	<gene:redefineInsert name="corpo">
		<gene:formPagine gestisciProtezioni="true" >
			<gene:pagina title="Dati generali" idProtezioni="DATIGEN">
				<jsp:include page="impr-datigen.jsp" />
			</gene:pagina>
			<gene:pagina title="Legali e altri soggetti" idProtezioni="LEGALI" selezionabile='${tipoImpresa[0] ne "6"}'>
				<jsp:include page="impr-legaliesoggetti.jsp" />
			</gene:pagina>
			<gene:pagina title="Categorie d'iscrizione elenchi operatori" visibile='${esisteElenchiOperatori eq "true"}' idProtezioni="CATEGARE" >
				<jsp:include page="impr-categorieIscrizioneElenchiGare.jsp" />
			</gene:pagina>
			<gene:pagina title="Attestazioni SOA" idProtezioni="CATE" selezionabile='${tipoImpresa[0] ne "3" and tipoImpresa[0] ne "10"}'>
				<jsp:include page="impr-categorieIscrizione.jsp" />
			</gene:pagina>
			<gene:pagina title="${titleRaggruppamento}" idProtezioni="RAGIMP" selezionabile='${tipoImpresa[0]=="2" or tipoImpresa[0]== "3" or tipoImpresa[0]=="4" or tipoImpresa[0]=="10" or tipoImpresa[0]=="11" or (tipoImpresa[0]=="7" && ag0087raggr=="1") or (tipoImpresa[0]=="8" && ag0088raggr=="1") or (tipoImpresa[0]=="12" && ag00812raggr=="1")}'>
				<jsp:include page="impr-raggruppamento.jsp" />
			</gene:pagina>
			<gene:pagina title="DURC on line" idProtezioni="DURC" selezionabile='${flagSoggdurc[0] eq "1" and tipoImpresa[0] ne "3" and tipoImpresa[0] ne "10"}'>
				<jsp:include page="impr-lista-durcOnLine.jsp" />
			</gene:pagina>
			<gene:pagina title="Casellario giudiziale" idProtezioni="CASE" selezionabile='${tipoImpresa[0] ne "3" and tipoImpresa[0] ne "10"}'>
				<jsp:include page="impr-lista-casellarioGiudiziale.jsp" />
			</gene:pagina>
			<gene:pagina title="Accertamenti antimafia" idProtezioni="IMPANTIMAFIA" selezionabile='${tipoImpresa[0] ne "3" and tipoImpresa[0] ne "10"}'>
				<jsp:include page="impr-lista-accertamentiAntimafia.jsp" />
			</gene:pagina>
			<c:if test='${isVerificaArt80 eq "1"}'>
				<gene:pagina title="Documenti art.80" idProtezioni="VART80" >
					<jsp:include page="impr-lista-verificheArt80.jsp" />
				</gene:pagina>
			</c:if>
		</gene:formPagine>
	</gene:redefineInsert>
</gene:template>