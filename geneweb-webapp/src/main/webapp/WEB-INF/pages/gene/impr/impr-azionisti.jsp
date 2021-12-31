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
		<gene:campoScheda visibile="false" entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="CODIMP4_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIMP4" value="${item[0]}" />
		<gene:archivio titolo="Tecnici dell'impresa" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPAZI.CODTEC"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			chiave="IMPAZI_CODTEC_${param.contatore}"
			formName="formTeimAzi${param.contatore}">
			<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="CODTEC_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODTEC2" value="${item[1]}" obbligatorio="true"/>
			<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="NOMTEC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMTEC2" value="${item[2]}" />
		</gene:archivio>
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="INCAZI_${param.contatore}" campoFittizio="true" definizione="N7;0;Ag007;;INCAZI" value="${item[3]}" obbligatorio="true"/>
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="QUOAZI_${param.contatore}" campoFittizio="true" definizione="T20;0;;;QUOAZI" value="${item[4]}" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="INIAZI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;IMPAZI" value="${item[5]}" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="FINAZI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;FINAZI" value="${item[6]}" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="RESPDICH_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2;0;;SN;G_RESDICAC" value="${item[9]}" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="NOTAZI_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;NOTAZI" value="${item[7]}" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="NUMAZI_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;G_NUMAZI" value="${item[8]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda visibile="false" entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="CODIMP4_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIMP4" value="${codiceImpresaPadre}" />
		<gene:archivio titolo="Tecnici dell'impresa"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPAZI.CODTEC"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			chiave="IMPAZI_CODTEC${param.contatore}"
			formName="formTeimAzi${param.contatore}">
			<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="CODTEC_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODTEC2" obbligatorio="true" />
			<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="NOMTEC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMTEC2"  />
		</gene:archivio>
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="INCAZI_${param.contatore}" campoFittizio="true" definizione="N7;0;Ag007;;INCAZI" obbligatorio="true"/>
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="QUOAZI_${param.contatore}" campoFittizio="true" definizione="T20;0;;;QUOAZI" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="INIAZI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;IMPAZI" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="FINAZI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;FINAZI"/>
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="RESPDICH_${param.contatore}" campoFittizio="true" visibile="true" definizione="T2;0;;SN;G_RESDICAC" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="NOTAZI_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;NOTAZI" />
		<gene:campoScheda entita="IMPAZI" where="IMPR.CODIMP = IMPAZI.CODIMP4" campo="NUMAZI_${param.contatore}" campoFittizio="true" visibile="false" definizione="N3;1;;;G_NUMAZI"  />
	</c:otherwise>
</c:choose>	

	