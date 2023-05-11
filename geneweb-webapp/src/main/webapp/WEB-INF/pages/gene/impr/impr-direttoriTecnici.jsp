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
		<gene:campoScheda visibile="false" entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="ID_${param.contatore}" campoFittizio="true" definizione="N12;1;;;G_IDDTE" value="${item[0]}" />
		<gene:campoScheda visibile="false" entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="CODIMP3_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODIMP3" value="${codiceImpresaPadre}" />
		<gene:archivio titolo="Tecnici dell'impresa"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPDTE.CODDTE"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			functionId="skip"
			chiave="IMPDTE_CODDTE_${param.contatore}"
			formName="formTeim${param.contatore}">
			<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="CODDTE_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODDTE" value="${item[1]}" obbligatorio="true" />
			<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="NOMDTE_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDTE" value="${item[2]}" />
		</gene:archivio>
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="DIRINI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_DIRINI" value="${item[3]}" />
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="DIRFIN_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_DIRFIN" value="${item[4]}" />
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="RESPDICH_${param.contatore}" campoFittizio="true" definizione="T2;0;;SN;G_RESDICLR" value="${item[5]}" />
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="NOTDTE_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;G_NOTDTE" value="${item[6]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda visibile="false" entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="ID_${param.contatore}" campoFittizio="true" definizione="N12;1;;;G_IDDTE" value="" />
		<gene:campoScheda visibile="false" entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="CODIMP3_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODIMP3" value="" />
		<gene:archivio titolo="Tecnici dell'impresa"
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPDTE.CODDTE"),"gene/teim/teim-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTeim"),"gene/teim/teim-scheda-popup.jsp","")}'
			campi="TEIM.CODTIM;TEIM.NOMTIM"
			functionId="skip"
			chiave="IMPDTE_CODDTE_${param.contatore}"
			formName="formTeim${param.contatore}">
			<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="CODDTE_${param.contatore}" campoFittizio="true" definizione="T10;0;;;CODDTE" obbligatorio="true" />
			<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="NOMDTE_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDTE" />
		</gene:archivio>
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="DIRINI_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_DIRINI"/>
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="DIRFIN_${param.contatore}" campoFittizio="true" definizione="D;0;;DATA_ELDA;G_DIRFIN"/>
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="RESPDICH_${param.contatore}" campoFittizio="true" definizione="T2;0;;SN;G_RESDICLR" />
		<gene:campoScheda entita="IMPDTE" where="IMPR.CODIMP = IMPDTE.CODIMP3" campo="NOTDTE_${param.contatore}" campoFittizio="true" definizione="T2000;0;;NOTE;G_NOTDTE"  />
	</c:otherwise>
</c:choose>	




