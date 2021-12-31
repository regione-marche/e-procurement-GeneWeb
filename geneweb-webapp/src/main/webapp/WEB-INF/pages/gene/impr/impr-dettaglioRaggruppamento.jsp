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

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />


<c:set var="codiceImpresaPadre" value='${param.chiave}' />

<c:set var="art80wsurl" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "art80.ws.url")}'/>

<c:choose>
	<c:when test="${param.tipoDettaglio eq 1}">
		<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" value="${item[0]}" />
		<gene:archivio titolo="Imprese" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gene/impr/impr-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
			campi="IMPR.CODIMP;IMPR.NOMIMP;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
			where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
			chiave="RAGIMP_CODDIC_${param.contatore}">
			<gene:campoScheda title="Codice impresa" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" value="${item[1]}" obbligatorio="true" />
			<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" value="${item[2]}" />
			<gene:campoScheda title="Codice fiscale" entita="IMPR" campo="CFIMP_${param.contatore}" campoFittizio="true" definizione="T16;;;;" value="${item[6]}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
			<gene:campoScheda title="Partita I.V.A." entita="IMPR" campo="PIVIMP_${param.contatore}" campoFittizio="true" definizione="T14;;;;" value="${item[7]}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
			<gene:campoScheda title="Codice dell'Anagrafico Generale" entita="IMPR" campo="CGENIMP_${param.contatore}" campoFittizio="true" definizione="T20;;;;CGENIMP" value="${item[5]}" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
		</gene:archivio>
		<gene:campoScheda title="Quota di partecipazione" visibile='${tipoImpr eq "3" or tipoImpr eq "10"}' entita="RAGIMP" campo="QUODIC_${param.contatore}" campoFittizio="true" definizione="F9.5;0;;PRC;QUODIC" value="${item[3]}" />
		<gene:campoScheda title="Mandataria?" 	visibile='${tipoImpr eq "3" or tipoImpr eq "10"}'	entita="RAGIMP" campo="IMPMAN_${param.contatore}" campoFittizio="true" definizione="T1;0;;SN;G_IMPMAN"    value="${item[4]}" />
		
		
		<gene:campoScheda visibile="${!empty art80wsurl}">
			<td colspan="2"><i>Verifiche Art. 80</i></td>
		</gene:campoScheda>
		<gene:campoScheda title="Stato documentale" entita="IMPR" campo="ART80_STATO_${param.contatore}" campoFittizio="true" definizione="T100;0;G_063" value="${item[8]}" modificabile="false" visibile="${!empty art80wsurl}">
			<c:if test="${modo eq 'VISUALIZZA'}">
				<c:choose>
					<c:when test="${empty item[8] || item[8] eq ''}">
						<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou226#")}'>
							<span style="float: right;">
								<a href="javascript:art80submit('${item[1]}','crea');" 
									title="Richiedi verifica art.80 per l'operatore economico">
									Richiedi verifica art.80 per l'operatore economico
								</a>
							</span>
						</c:if>
					</c:when>
					<c:otherwise>
						<c:if test='${fn:contains(listaOpzioniUtenteAbilitate, "ou225#")}'>
							<span style="float: right;">
								<a href="javascript:art80submit('${item[1]}','consulta');" 
									title="Consulta il dettaglio dei documenti">
									Consulta il dettaglio dei documenti
								</a>
							</span>
						</c:if>
					</c:otherwise>
				</c:choose>
			</c:if>
		</gene:campoScheda>
		
		<gene:campoScheda title="Data invio anagrafica" entita="IMPR" campo="ART80_DATA_RICHIESTA_${param.contatore}" campoFittizio="true" definizione="D;0" value="${item[9]}"  modificabile="false" visibile="${!empty art80wsurl}"/>
		<gene:campoScheda title="Data ultima lettura stato" entita="IMPR" campo="ART80_DATA_LETTURA_${param.contatore}" campoFittizio="true" definizione="D;0" value="${item[10]}"  modificabile="false" visibile="${!empty art80wsurl}"/>
		
	</c:when>
	<c:otherwise>
		<gene:campoScheda title="Cod.impr.raggruppam." visibile="false" entita="RAGIMP" campo="CODIME9_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODIME9" />
		<gene:archivio titolo="Imprese" 
			lista='${gene:if(gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.CODDIC") and gene:checkProt(pageContext, "COLS.MOD.GENE.RAGIMP.NOMDIC"),"gene/impr/impr-lista-popup.jsp","")}'
			scheda='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda.jsp","")}'
			schedaPopUp='${gene:if(gene:checkProtObj( pageContext, "MASC.VIS","GENE.ImprScheda"),"gene/impr/impr-scheda-popup.jsp","")}'
			campi="IMPR.CODIMP;IMPR.NOMIMP;IMPR.CFIMP;IMPR.PIVIMP;IMPR.CGENIMP"
			where="(IMPR.TIPIMP <>3 and IMPR.TIPIMP <>10) or IMPR.TIPIMP is null"
			chiave="CODDIC_${param.contatore}">
			<gene:campoScheda title="Codice impresa" entita="RAGIMP" campo="CODDIC_${param.contatore}" campoFittizio="true" definizione="T10;1;;;CODDIC" obbligatorio="true" />
			<gene:campoScheda title="Ragione sociale" entita="RAGIMP" campo="NOMDIC_${param.contatore}" campoFittizio="true" definizione="T61;0;;;NOMDIC" />
			<gene:campoScheda title="Codice fiscale" entita="IMPR" campo="CFIMP_${param.contatore}" campoFittizio="true" definizione="T16;;;;" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CFIMP")}'/>
			<gene:campoScheda title="Partita I.V.A." entita="IMPR" campo="PIVIMP_${param.contatore}" campoFittizio="true" definizione="T14;;;;" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.PIVIMP")}'/>
			<gene:campoScheda title="Codice dell'Anagrafico Generale" entita="IMPR" campo="CGENIMP_${param.contatore}" campoFittizio="true" definizione="T20;;;;CGENIMP" visibile='${gene:checkProt(pageContext, "COLS.VIS.GENE.IMPR.CGENIMP") && fn:contains(listaOpzioniDisponibili, "OP127#")}'/>
		</gene:archivio>
		<gene:campoScheda title="Quota di partecipazione" visibile='${tipoImpr eq "3" or tipoImpr eq "10"}' entita="RAGIMP" campo="QUODIC_${param.contatore}" campoFittizio="true" definizione="F9.5;0;;PRC;QUODIC" />
		<gene:campoScheda title="Mandataria?" visibile='${tipoImpr eq "3" or tipoImpr eq "10"}'	entita="RAGIMP" campo="IMPMAN_${param.contatore}" campoFittizio="true" definizione="T1;0;;SN;G_IMPMAN" />
	</c:otherwise>
</c:choose>	
<gene:javaScript>
	<c:if test='${(modoAperturaScheda eq "VISUALIZZA" && listaNoteAvvisi[param.contatore - 1]== 1)}'>
		var testo = "<img width='16' height='16' title=\"Nell'anagrafica dell'impresa sono presenti note o avvisi in stato 'aperto'\" alt=\"Nell'anagrafica dell'impresa sono presenti note o avvisi in stato 'aperto'\" src='${pageContext.request.contextPath}/img/noteAvvisiImpresa.png'/>";
		$('#tdTitoloDestra_${param.contatore}').html(testo);
	</c:if>
</gene:javaScript>