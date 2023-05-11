<%/*
       * Created on 25-gen-2007
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA APRE LA POPUP CON IL MODELLO APPENA COMPOSTO E RIAPRE LA LISTA
      // DEI MODELLI COMPONIBILI
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="codiceLavoro" value="${componiModelloForm.valChiavi[0]}"/>

<c:set var="entita" value="${componiModelloForm.entita}"/>
<c:set var="documentiAssociatiDB" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.documentiAssociatiDB")}'/>

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript" src="${contextPath}/js/general.js"></script>
</HEAD>

<BODY>
	<table style="border: thin; position: absolute; top: 30%; left: 5%;" align="left" width="90%">
		<tr>
			<td align="center">
				<div class="titolomaschera">Associazione del documento avvenuta con successo</div>
				<div class="contenitore-dettaglio">
					<table class="lista">
						<tr>
							<td>
					<c:choose>
						<c:when test='${empty download and sessionScope.entitaPrincipaleModificabile eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.C0OGGASS-Scheda.MOD")}'>
							<br>
							&nbsp;<a href="javascript:apri();" title="Apri documento"><img src="${contextPath}/img/open_folder.gif" /></a>
							&nbsp;<a href="javascript:apri();" title="Apri documento"><b>Apri documento</b></a><br>
							Il documento viene aperto in una nuova finestra, dalla quale &egrave; possibile modificarlo<br>
							Il documento &egrave; comunque accessibile dalla scheda dati mediante la funzione Documenti associati.<br>
							<br>
						</c:when>
						<c:when test='${documentiAssociatiDB eq "1"}'>
							<br>
							Il documento &egrave; stato salvato ed &egrave; accessibile dalla scheda dati mediante la funzione Documenti associati.
							<br>
							<br>
						</c:when>
						<c:otherwise>
							<a href="javascript:downloadFile();" title="Apri o salva il documento"><img src="${contextPath}/img/open_folder.gif" /></a>
							&nbsp;<a href="javascript:downloadFile();" title="Apri o salva il documento"><b>Apri o salva il documento</b></a><br>
							Il documento prodotto viene scaricato (download) nel proprio PC client.<br>
						</c:otherwise>
					</c:choose>
							</td>
						</tr>
						<tr>
							<td class="comandi-dettaglio">
							<INPUT type="button" class="bottone-azione"	value="Indietro" title="Torna alla lista" 
							onclick="javascript:tornaAllaLista();">&nbsp;
							<td>
						</tr>
					</table>
				</div>
			</td>
		</tr>
	</table>

<html:form action="/ApriElencoModelli.do" >
	<html:hidden property="idModello"/>
	<html:hidden property="nomeModello" value=""/>
	<html:hidden property="entita"/>
	<html:hidden property="nomeChiavi"/>
	<html:hidden property="valori"/>
	<html:hidden property="fileComposto" value=""/>
	<html:hidden property="noFiltroEntitaPrincipale"/>
	<html:hidden property="paginaSorgente"/>
	<c:forEach items="${componiModelloForm.valChiavi}" var="chiave">
		<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
	<html:hidden property="metodo" value="tornaElenco"/>
</html:form>

<script type="text/javascript">
	function apri(){
		apriDocumento("${nomeFileDocAss}");
	}
	
<c:set var="tmp" value="${fn:split(nomeFileDocAss, '/')}"/>
<c:set var="nomeFile" value="${tmp[fn:length(tmp)-1]}"/>
	
	function downloadFile(){
	  if (navigator.appName == "Microsoft Internet Explorer") {
	  	window.onblur = null;
	  }
	  document.componiModelloForm.action = "${contextPath}/geneGenmod/ComponiModello.do";
	  document.componiModelloForm.fileComposto.value = "${nomeFile}";
  	document.componiModelloForm.metodo.value="download";
    document.componiModelloForm.submit();
  }
	
	function tornaAllaLista(){
	  document.componiModelloForm.action = "${contextPath}/geneGenmod/ComponiModello.do";
	  document.componiModelloForm.fileComposto.value = "${nomeFile}";
		document.componiModelloForm.metodo.value="eliminaComposizione";
		document.componiModelloForm.submit();
	}
</script>
</BODY>
</HTML>