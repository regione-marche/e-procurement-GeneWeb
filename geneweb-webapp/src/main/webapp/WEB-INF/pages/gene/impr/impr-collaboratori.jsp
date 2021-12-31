<%
/*
 * Created on: 24-04-2012
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Sezione legali rappresentanti nella scheda dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<c:set var="codiceImpresaPadre" value='${param.chiave}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda visibile="false" entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="CODIMP_${param.contatore}" campoFittizio="true" definizione="T10;1;;;G_CODIMPCO" value="${item[0]}" />
		<gene:campoScheda visibile="false" entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="NUMCOL_${param.contatore}" campoFittizio="true" definizione="N3;1;;;G_NUMCOL" value="${item[1]}" />
		<gene:archivio titolo="Tecnici dell'impresa" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.G_IMPCOL.CODTEC"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			chiave="G_IMPCOL_CODTEC_${param.contatore}"
			formName="formTeimColl${param.contatore}">
			<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="CODTEC_${param.contatore}" campoFittizio="true" definizione="T10;0;;;G_CODTEC_C" value="${item[2]}" obbligatorio="true"/>
			<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="NOMTEC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;G_NOMTEC_C" value="${item[3]}" />
		</gene:archivio>
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="INCTIP_${param.contatore}" campoFittizio="true" definizione="N7;0;G_039;;G_INCTIP" value="${item[4]}" obbligatorio="true"/>
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="INCINI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_INCINI" value="${item[5]}" />
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="INCFIN_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_INCFIN" value="${item[6]}" />
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="NOTCOL_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;G_NOTCOL" value="${item[7]}" />
		
		</c:when>
	<c:otherwise>
		<gene:campoScheda visibile="false" entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="CODIMP_${param.contatore}" campoFittizio="true" definizione="T10;1;;;G_CODIMPCO" value="${codiceImpresaPadre}" />
		<gene:campoScheda visibile="false" entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="NUMCOL_${param.contatore}" campoFittizio="true" definizione="N3;1;;;G_NUMCOL"  />
		<gene:archivio titolo="Tecnici dell'impresa"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.G_IMPCOL.CODTEC"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			chiave="G_IMPCOL_CODTEC${param.contatore}"
			formName="formTeimColl${param.contatore}">
			<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="CODTEC_${param.contatore}" campoFittizio="true" definizione="T10;0;;;G_CODTEC_C" obbligatorio="true" />
			<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="NOMTEC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;G_NOMTEC_C"  />
		</gene:archivio>
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="INCTIP_${param.contatore}" campoFittizio="true" definizione="N7;0;G_039;;G_INCTIP" obbligatorio="true"/>
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="INCINI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_INCINI" />
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="INCFIN_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_INCFIN" />
		<gene:campoScheda entita="G_IMPCOL" where="IMPR.CODIMP = G_IMPCOL.CODIMP" campo="NOTCOL_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;G_NOTCOL"  />
	</c:otherwise>
</c:choose>	

	