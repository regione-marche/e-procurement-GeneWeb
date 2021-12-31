<%
/*
 * Created on: 16/05/2011
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<c:choose>
<c:when test="${param.tipoDettaglio eq 1}">
	<gene:archivio titolo="Tecnici" 
		lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.TECNI.CODTEC"),"gene/tecni/tecni-lista-popup.jsp","")}' 
		scheda=''
		schedaPopUp=''
		campi="TECNI.CODTEC;TECNI.NOMTEC"
		chiave="TECNI.CODTEC_${param.contatore}"
		formName="formTecniAccount${param.contatore}"
		where = " TECNI.SYSCON IS NULL or TECNI.SYSCON = ${param.chiave}"
		inseribile="false">
			<gene:campoScheda campo="CODTEC_${param.contatore}" entita="TECNI" title = "Codice del tecnico" campoFittizio="true"  visibile="true" definizione="T10;1;;;CODTEC1" value="${item[0]}" />
			<gene:campoScheda campo="NOMTEC_${param.contatore}" entita="TECNI" campoFittizio="true"  title="Nome" visibile="true" definizione="T61;0;;;NOMTEC1" value="${item[1]}" />
	</gene:archivio>
</c:when>
<c:otherwise>
	<gene:archivio titolo="Tecnici" 
		lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.TECNI.CODTEC"),"gene/tecni/tecni-lista-popup.jsp","")}' 
		scheda=''
		schedaPopUp=''
		campi="TECNI.CODTEC;TECNI.NOMTEC"
		chiave="TECNI.CODTEC_${param.contatore}"
		formName="formTecniAccount${param.contatore}"
		where = " TECNI.SYSCON IS NULL or TECNI.SYSCON = ${param.chiave}"
		inseribile="false">
			<gene:campoScheda campo="CODTEC_${param.contatore}" entita="TECNI" title = "Codice del tecnico" campoFittizio="true" visibile="true" definizione="T10;1;;;CODTEC1" />
			<gene:campoScheda campo="NOMTEC_${param.contatore}" entita="TECNI" campoFittizio="true" title="Nome" visibile="true" definizione="T61;0;;;NOMTEC1" />
	</gene:archivio>
</c:otherwise>
</c:choose>




