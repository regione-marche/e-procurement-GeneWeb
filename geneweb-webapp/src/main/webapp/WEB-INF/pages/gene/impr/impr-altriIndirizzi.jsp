<%
/*
 * Created on: 172-giu-2009
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
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="CODIMP5_${param.contatore}" visibile="false" campoFittizio="true" definizione="T10;1;;;CODIMP5" value="${codiceImpresaPadre}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDCON_${param.contatore}" visibile="false"  campoFittizio="true" definizione="N10;1;;;INDCON" value="${indcon[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDTIP_${param.contatore}" title="Tipo"      campoFittizio="true" definizione="T30;0;Ag030;;INDTIP" value="${indtip[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDIND_${param.contatore}" title="Indirizzo" campoFittizio="true" definizione="T60;0;;;INDIND" value="${indind[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDNC_${param.contatore}"  title="N.Civico"  campoFittizio="true" definizione="T10;0;;;INDNC" value="${indnc[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDPRO_${param.contatore}" title="Provincia" campoFittizio="true" definizione="T4040;0;Agx15;;INDPROI" value="${indpro[param.contatore - 1]}">
            <c:set var="functionId" value="default_${!empty indpro[param.contatore - 1]}" />
            <c:if test="${!empty indpro[param.contatore - 1]}">
                <c:set var="parametriWhere" value="T:${indpro[param.contatore - 1]}" />
            </c:if>
        </gene:campoScheda>
		<gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.INDCAP") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.INDPRO") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.INDLOC") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
				scheda=""
				schedaPopUp=""
				campi="G_COMUNI.PROVINCIA;G_COMUNI.CAP;G_COMUNI.DESCRI;G_COMUNI.CODISTAT"
				functionId="${functionId}"
				parametriWhere="${parametriWhere}"
				chiave=""
				formName="formIstatIndirizzo${param.contatore}"
				inseribile="false" >
			<gene:campoScheda campoFittizio="true" campo="COM_INDPRO_${param.contatore}" definizione="T9" visibile="false"/>
			<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDCAP_${param.contatore}" title="C.A.P." campoFittizio="true" definizione="T5;0;;;INDCAP" value="${indcap[param.contatore - 1]}" />
			<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDLOC_${param.contatore}" title="Comune" campoFittizio="true" definizione="T32;0;;;INDLOCAL" value="${indloc[param.contatore - 1]}" />
			<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="CODCIT_${param.contatore}" title="Codice ISTAT del comune" campoFittizio="true" definizione="T9;0;;;G_CODCITIN" value="${codcit[param.contatore - 1]}" />
		</gene:archivio>
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="NAZIMP_${param.contatore}" title="Nazione" campoFittizio="true" definizione="N1;0;Ag010;;G_NAZIMPIN" value="${nazimp[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDTEL_${param.contatore}" title="Telefono" campoFittizio="true" definizione="T50;0;;;INDTEL" value="${indtel[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDFAX_${param.contatore}" title="FAX" campoFittizio="true" definizione="T20;0;;;INDFAX" value="${indfax[param.contatore - 1]}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="REGDIT_${param.contatore}" title="Numero R.E.A." campoFittizio="true" definizione="T20;0;;;G_REGDITIN" value="${regdit[param.contatore - 1]}" />
		<gene:fnJavaScriptScheda funzione='changeComuneIndirizzo("#IMPIND_INDPRO_${param.contatore}#", "COM_INDPRO_${param.contatore}","${param.contatore}")' elencocampi='IMPIND_INDPRO_${param.contatore}' esegui="false" />
		<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("IMPIND_INDPRO_${param.contatore}", "#COM_INDPRO_${param.contatore}#")' elencocampi='COM_INDPRO_${param.contatore}' esegui="false"/>
		<gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#IMPIND_INDLOC_${param.contatore}#", "${valoreItalia}", "IMPIND_NAZIMP_${param.contatore}")' elencocampi='IMPIND_INDLOC_${param.contatore}' esegui="false"/>
	</c:when>
	<c:otherwise>
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="CODIMP5_${param.contatore}" visibile="false" campoFittizio="true" definizione="T10;1;;;CODIMP5" value="${datiRiga.IMPR_CODIMP}" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDCON_${param.contatore}"  visibile="false" campoFittizio="true" definizione="N10;1;;;INDCON"  />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDTIP_${param.contatore}" title="Tipo" campoFittizio="true"      definizione="T30;0;Ag030;;INDTIP"  />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDIND_${param.contatore}" title="Indirizzo" campoFittizio="true" definizione="T60;0;;;INDIND" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDNC_${param.contatore}"  title="N.Civico" campoFittizio="true"  definizione="T10;0;;;INDNC" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDPRO_${param.contatore}" title="Provincia" campoFittizio="true" definizione="T40;0;Agx15;;INDPROI">
            <c:set var="functionId" value="default_${!empty indpro[param.contatore - 1]}" />
            <c:if test="${!empty indpro[param.contatore - 1]}">
                <c:set var="parametriWhere" value="T:${indpro[param.contatore - 1]}" />
            </c:if>
        </gene:campoScheda>
		<gene:archivio titolo="Comuni" obbligatorio="false" scollegabile="true"
				lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.INDCAP") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.INDPRO") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.INDLOC") and gene:checkProt(pageContext, "COLS.MOD.GENE.IMPIND.CODCIT"),"gene/commons/istat-comuni-lista-popup.jsp","")}' 
				scheda="" 
				schedaPopUp="" 
				campi="G_COMUNI.PROVINCIA;G_COMUNI.CAP;G_COMUNI.DESCRI;G_COMUNI.CODISTAT"
				functionId="${functionId}"
				parametriWhere="${parametriWhere}"
				chiave="" 
				formName="formIstatIndirizzo${param.contatore}" 
				inseribile="false" >
			<gene:campoScheda campoFittizio="true" campo="COM_INDPRO_${param.contatore}" definizione="T9" visibile="false"/>
			<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDCAP_${param.contatore}" title="C.A.P" campoFittizio="true"  definizione="T5;0;;;INDCAP" />
			<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDLOC_${param.contatore}" title="Comune" campoFittizio="true" definizione="T32;0;;;INDLOCAL"  />
			<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="CODCIT_${param.contatore}" title="Codice ISTAT del comune" campoFittizio="true" definizione="T9;0;;;G_CODCITIN"  />
		</gene:archivio>
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="NAZIMP_${param.contatore}" title="Nazione" campoFittizio="true" definizione="N1;0;Ag010;;G_NAZIMPIN"  />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDTEL_${param.contatore}" title="Telefono" campoFittizio="true" definizione="T50;0;;;INDTEL" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="INDFAX_${param.contatore}" title="FAX" campoFittizio="true" definizione="T20;0;;;INDFAX" />
		<gene:campoScheda entita="IMPIND" where="IMPR.CODIMP = IMPIND.CODIMP5" campo="REGDIT_${param.contatore}" title="Numero R.E.A." campoFittizio="true" definizione="T20;0;;;G_REGDITIN" value="${regdit[param.contatore - 1]}" />
		<gene:fnJavaScriptScheda funzione='setValueIfNotEmpty("IMPIND_INDPRO_${param.contatore}", "#COM_INDPRO_${param.contatore}#")' elencocampi='COM_INDPRO_${param.contatore}' esegui="false"/>
		<gene:fnJavaScriptScheda funzione='changeComuneIndirizzo("#IMPIND_INDPRO_${param.contatore}#", "COM_INDPRO_${param.contatore}","${param.contatore}")' elencocampi='IMPIND_INDPRO_${param.contatore}' esegui="false"/>
		<gene:fnJavaScriptScheda funzione='aggiornaNazionalita("#IMPIND_INDLOC_${param.contatore}#", "${valoreItalia}", "IMPIND_NAZIMP_${param.contatore}")' elencocampi='IMPIND_INDLOC_${param.contatore}' esegui="false"/>	
	</c:otherwise>
</c:choose>	