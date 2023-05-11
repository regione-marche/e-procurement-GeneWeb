<%
/*
 * Created on 19-lug-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA LISTA DOCUMENTI
 // ASSOCIATI CONTENENTE LA SEZIONE JAVASCRIPT
%>

<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>

<fmt:setBundle basename="AliceResources" />

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
<!-- 

<gene:setIdPagina schema="GENE" maschera="C0OGGASS-Lista" />

	function generaPopupListaOpzioniRecord(id) {
	<elda:jsBodyPopup varJS="linkset" contextPath="${pageContext.request.contextPath}">
	<elda:jsVocePopup functionJS="visualizza('\"+id+\"')" descrizione="Visualizza documento associato"/>
<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1"}'>
	<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.C0OGGASS-Scheda.MOD")}'>
		<elda:jsVocePopup functionJS="modifica('\"+id+\"')" descrizione="Modifica documento associato"/>
	</c:if>
	<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL")}'>
		<elda:jsVocePopup functionJS="apriConfermaElimina('\"+id+\"')" descrizione="Elimina documento associato"/>
	</c:if>
</c:if>
	</elda:jsBodyPopup>
		return linkset;
	}

  function download(id){
		document.location.href='DocumentoAssociato.do?'+csrfToken+'&metodo=download&id=' + id;
  }

<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1"}'>
<c:if test='${gene:checkProtFunz(pageContext, "MOD","MOD")}'>
  function modifica(id){
	  document.location.href='DocumentoAssociato.do?'+csrfToken+'&metodo=modifica&id=' + id;
  }
</c:if>

<c:if test='${gene:checkProtFunz(pageContext, "INS","LISTANUOVO")}'>
  function inserisciDocumento(){
  	document.documentoAssociatoForm.metodo.value="nuovo";
  	document.documentoAssociatoForm.submit();
  }
</c:if>
</c:if>

  function visualizza(id){
  	var href = 'DocumentoAssociato.do?'+csrfToken+'&metodo=visualizza&id=' + id;
  	if(document.documentoAssociatoForm.keyParent.value != '')
  		href += '&keyParent='+document.documentoAssociatoForm.keyParent.value;
  	if(document.documentoAssociatoForm.key.value != '')
  		href += '&keyParent='+document.documentoAssociatoForm.key.value;

		document.location.href = href;
  }

<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1"}'>
<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}'>
  function apriConfermaElimina(id) {
	  var fileSelezionatiPresentiInCondivisa = true;
	  var i = 0;
	  for(var i=0; i < arrayDocInCondivisa.length; i++){
	  	if(arrayId[i] == id){
				if(! arrayDocInCondivisa[i]){
  				fileSelezionatiPresentiInCondivisa = false;
  			}
  		}
  	}
  	var action = "${contextPath}/EliminaDocAss.do";
		var href = new String("numDoc=1&id=" + id  + "&shared=" + fileSelezionatiPresentiInCondivisa);
		openPopUpActionCustom(action, href, "confermaElimDocAss", 315, 215, false, false);
  }

	function eseguiCancellazione(id){
   	bloccaRichiesteServer();
   	if(document.getElementById("cancellazioneFile").value == 1)
			document.location.href='ListaDocumentiAssociati.do?'+csrfToken+'&metodo=elimina&delete=1&id=' + id;
		else
			document.location.href='ListaDocumentiAssociati.do?'+csrfToken+'&metodo=elimina&id=' + id;
	}
</c:if>
</c:if>

<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1"}'>
<c:if test='${gene:checkProtFunz(pageContext, "DEL","LISTADELSEL")}'>
	function apriConfermaEliminaMultipla(){
    var numeroOggetti = contaCheckSelezionati(document.listaDocAssForm.id);
    if (numeroOggetti == 0) {
      alert("Nessun elemento selezionato nella lista");
    } else {
    	var fileSelezionatiPresentiInCondivisa = true;
   		if(document.listaDocAssForm.id.length != null){
	    	for(var i=0; i < document.listaDocAssForm.id.length && fileSelezionatiPresentiInCondivisa; i++){
    			if(document.listaDocAssForm.id[i].checked)
    				if(! arrayDocInCondivisa[i])
    					fileSelezionatiPresentiInCondivisa = false;
  	  	}
   		} else {
   			if(document.listaDocAssForm.id.checked)
	   			if(! arrayDocInCondivisa[0])
   					fileSelezionatiPresentiInCondivisa = false;
   		}
   		var action = "${contextPath}/EliminaDocAss.do";
			var href = new String("numDoc=" + numeroOggetti + "&shared=" + fileSelezionatiPresentiInCondivisa);
			openPopUpActionCustom(action, href, "confermaElimDocAssSelez", 330, 235, false, false);
		}
	}
	
	function eseguiCancellazioneMultipla(){
   	bloccaRichiesteServer();
 	  document.listaDocAssForm.submit();
	}
</c:if>
</c:if>

<c:if test='${not empty listaDocAss and fn:length(listaDocAss) > 0}'>
	<c:out value='var arrayId = new Array();'/>
	<c:forEach items="${listaDocAss}" var="docAssForm" varStatus="stato" >
		<c:out value='arrayId['/>${stato.index}<c:out value='] = '/>${docAssForm.id}<c:out value=";"/>
	</c:forEach>
	
	<c:out value='var arrayPath = new Array();'/>
	<c:forEach items="${listaDocAss}" var="docAssForm" varStatus="stato" >
	<c:set var="prefissoUrl" value="" />
	<c:if test='${fn:indexOf(docAssForm.pathDocAss, "\\\\") eq 0}' >
		<c:set var="prefissoURL" value="\\" />
	</c:if>
		<c:out value='arrayPath[' escapeXml="false"/>${stato.index}<c:out value='] = "' escapeXml="false"/>${prefissoURL}${docAssForm.pathDocAss}${docAssForm.nomeDocAss}<c:out value='";' escapeXml="false"/>
	</c:forEach>

	<c:out value='var arrayDocInCondivisa = new Array();' />
	<c:forEach items="${listaDocAss}" var="docAssForm" varStatus="stato" >
	<c:set var="docInCondivisa" value="${docAssForm.documentoInAreaShared}" />
		<c:out value='arrayDocInCondivisa['/>${stato.index}<c:out value='] = '/>${docAssForm.documentoInAreaShared}<c:out value=';' escapeXml="false"/>
	</c:forEach>
</c:if>

	function mostraDocumento(id){
		var nomeCompleto = null;
		var j = 0;
		while(nomeCompleto == null && arrayId[j] != null){
			if(arrayId[j] == id){
			  nomeCompleto = arrayPath[j];
			}
			j++;
		}
<c:choose>
	<c:when test='${!empty download or !gene:checkProt(pageContext, "FUNZ.VIS.MOD.GENE.C0OGGASS-Scheda.MOD") or sessionScope.entitaPrincipaleModificabile ne "1"}'>

	if(confirm('<fmt:message key="info.download.confirm"/>'))
			document.location.href='DocumentoAssociato.do?'+csrfToken+'&metodo=download&id=' + id;
	</c:when>
	<c:otherwise>
		apriDocumento(nomeCompleto);
	</c:otherwise>
</c:choose>
	}

-->
</script>