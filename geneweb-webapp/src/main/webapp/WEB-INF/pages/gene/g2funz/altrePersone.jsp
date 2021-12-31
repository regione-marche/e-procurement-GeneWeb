<%
/*
 * Created on: 20-lug-2009
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

<c:set var="codiceUfficio" value='${param.chiave}' />
<c:set var="archiviFiltrati" value='${param.archiviFiltrati}' />

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="CODEI_${param.contatore}" visibile="false" campoFittizio="true" definizione="T16;1;;;CODEIN1" value="${item[0]}" />
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="NUMFUN_${param.contatore}" visibile="false"  campoFittizio="true" definizione="N10;1;;;NUMFUN" value="${item[1]}" />
		<gene:archivio titolo="Tecnici" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.G2FUNZ.CODFUN"),"gene/tecni/tecni-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			campi="TECNI.CODTEC;TECNI.NOMTEC"
			chiave="G2FUNZ_CODFUN_${param.contatore}"
			formName="formTecniPers${param.contatore}"
			inseribile="${empty sessionScope.uffint or !fn:contains(archiviFiltrati,'TECNI')}">
			<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="CODFUN_${param.contatore}" title="Codice del tecnico incaricato" campoFittizio="true" definizione="T10;0;;;CODFUN" value="${item[2]}" />
			<gene:campoScheda entita="TECNI" title="Nome tecnico" campo="NOMTEC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMTEC1" value="${nomeTecnico[param.contatore - 1]}" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.G2FUNZ.CODFUN")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.G2FUNZ.CODFUN")}' />
		</gene:archivio>
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="INCFUN_${param.contatore}" title="Tipo di incarico" campoFittizio="true" definizione="N2;0;A2144;;INCFUN" value="${item[3]}" />
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="DINFUN_${param.contatore}"  title="Data inizio incarico"  campoFittizio="true" definizione="T10;0;;DATA_ELDA;DINFUN" value="${item[4]}" />
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="DTEFUN_${param.contatore}" title="Data termine incarico" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DTEFUN" value="${item[5]}" />
	</c:when>
	<c:otherwise>
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="CODEI_${param.contatore}" visibile="false" campoFittizio="true" definizione="T16;1;;;CODEIN1" value="${codiceUfficio}" />
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="NUMFUN_${param.contatore}"  visibile="false" campoFittizio="true" definizione="N10;1;;;NUMFUN"  />
		<gene:archivio titolo="Tecnici" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.G2FUNZ.CODFUN"),"gene/tecni/tecni-lista-popup.jsp","")}' 
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.SchedaTecni"),"gene/tecni/tecni-scheda-popup.jsp","")}'
			campi="TECNI.CODTEC;TECNI.NOMTEC"
			chiave="G2FUNZ_CODFUN_${param.contatore}"
			formName="formTecniPers${param.contatore}"
			inseribile="${empty sessionScope.uffint or !fn:contains(archiviFiltrati,'TECNI')}">
			<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="CODFUN_${param.contatore}" title="Codice del tecnico incaricato" campoFittizio="true" definizione="T10;0;;;CODFUN" />
			<gene:campoScheda entita="TECNI" title="Nome tecnico" campo="NOMTEC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMTEC1" modificabile='${gene:checkProt(pageContext, "COLS.MOD.GENE.G2FUNZ.CODFUN")}' visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.G2FUNZ.CODFUN")}' />
		</gene:archivio>
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="INCFUN_${param.contatore}" title="Tipo di incarico" campoFittizio="true" definizione="N2;0;A2144;;INCFUN" />
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="DINFUN_${param.contatore}"  title="Data inizio incarico"  campoFittizio="true" definizione="T10;0;;DATA_ELDA;DINFUN" />
		<gene:campoScheda entita="G2FUNZ" where="UFFINT.CODEIN = G2FUNZ.CODEI" campo="DTEFUN_${param.contatore}" title="Data termine incarico" campoFittizio="true" definizione="T10;0;;DATA_ELDA;DTEFUN" />
	</c:otherwise>
</c:choose>	

<gene:javaScript>
<c:if test='${! empty sessionScope.uffint && fn:contains(archiviFiltrati,"TECNI") && modoAperturaScheda eq "MODIFICA"}'>
	document.formTecniPers${param.contatore}.archWhereLista.value="TECNI.CGENTEI='${codiceUfficio}'";
</c:if>
</gene:javaScript>