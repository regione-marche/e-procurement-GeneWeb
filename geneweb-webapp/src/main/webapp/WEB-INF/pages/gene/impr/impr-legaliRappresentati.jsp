<%
/*
 * Created on: 29-mag-2008
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
		<gene:campoScheda visibile="false" entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="ID_${param.contatore}" campoFittizio="true" definizione="N12;1;;;G_IDLEG" value="${item[0]}" />
		<gene:campoScheda visibile="false" entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="CODIMP2_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODIMP2" value="${codiceImpresaPadre}" />
		<gene:archivio titolo="Tecnici dell'impresa" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPLEG.CODLEG"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			chiave="IMPLEG_CODLEG_${param.contatore}"
			formName="formTeimLeg${param.contatore}">
			<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="CODLEG_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODLEG" value="${item[1]}" obbligatorio="true"/>
			<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="NOMLEG_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMLEG" value="${item[2]}" />
		</gene:archivio>
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="LEGINI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_LEGINI" value="${item[3]}" />
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="LEGFIN_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_LEGFIN" value="${item[4]}" />
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="RESPDICH_${param.contatore}" campoFittizio="true" definizione="T2;0;;SN;G_RESDICDT" value="${item[5]}" />
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="NOTLEG_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;G_NOTLEG" value="${item[6]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda visibile="false" entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="ID_${param.contatore}" campoFittizio="true" definizione="N12;1;;;G_IDLEG" value="" />
		<gene:campoScheda visibile="false" entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="CODIMP2_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODIMP2" value="" />
		<gene:archivio titolo="Tecnici dell'impresa"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPLEG.CODLEG"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			chiave="IMPLEG_CODLEG_${param.contatore}"
			formName="formTeimLeg${param.contatore}">
			<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="CODLEG_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODLEG" obbligatorio="true" />
			<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="NOMLEG_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMLEG"  />
		</gene:archivio>
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="LEGINI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_LEGINI" />
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="LEGFIN_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_LEGFIN"/>
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="RESPDICH_${param.contatore}" campoFittizio="true" definizione="T2;0;;SN;G_RESDICDT"  />
		<gene:campoScheda entita="IMPLEG" where="IMPR.CODIMP = IMPLEG.CODIMP2" campo="NOTLEG_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;G_NOTLEG"  />
	</c:otherwise>
</c:choose>	

	