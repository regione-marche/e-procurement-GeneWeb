<%
/*
 * Created on: 22-mag-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Tab raggrupamento della scheda dell'impresa */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="AliceResources" />

<gene:formScheda entita="IMPR" gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreRAGIMPMultiplo" plugin="it.eldasoft.gene.web.struts.tags.gestori.plugin.GestoreRaggruppamento" >

	<gene:redefineInsert name="schedaNuovo"></gene:redefineInsert>
	<gene:redefineInsert name="pulsanteNuovo"></gene:redefineInsert>
	
	<c:set var="tipoImpr" value='${tipoImpr}' scope="request"/>
	
	<c:set var="codiceImpresaPadre" value='${fn:substringAfter(key, ":")}' />
	<gene:campoScheda campo="CODIMP" visibile="false" entita="IMPR" />
	
	<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, codiceImpresaPadre )}'/>
	
	<jsp:include page="/WEB-INF/pages/commons/interno-scheda-multipla.jsp" >
		<jsp:param name="entita" value='RAGIMP'/>
		<jsp:param name="chiave" value='${codiceImpresaPadre}'/>
		<jsp:param name="nomeAttributoLista" value='listaRaggruppamenti' />
		<jsp:param name="idProtezioni" value="RAGIMP" />
		<jsp:param name="jspDettaglioSingolo" value="/WEB-INF/pages/gene/impr/impr-dettaglioRaggruppamento.jsp" />
		<jsp:param name="arrayCampi" value="'RAGIMP_CODDIC_', 'RAGIMP_NOMDIC_','RAGIMP_QUODIC_', 'RAGIMP_IMPMAN_','IMPR_CGENIMP_','IMPR_CFIMP_','IMPR_PIVIMP_','IMPR_ART80_STATO_','IMPR_ART80_DATA_RICHIESTA_','IMPR_ART80_DATA_LETTURA_'" />
		<jsp:param name="titoloSezione" value="Impresa" />
		<jsp:param name="titoloNuovaSezione" value="Nuova impresa" />
		<jsp:param name="descEntitaVociLink" value="impresa" />
		<jsp:param name="msgRaggiuntoMax" value="e imprese" />
		<jsp:param name="usaContatoreLista" value="true"/>
	</jsp:include>
	

	<gene:campoScheda>
		<jsp:include page="/WEB-INF/pages/commons/pulsantiScheda.jsp" />
	</gene:campoScheda>
	
	<c:if test='${impresaRegistrata eq "SI"  and isIntegrazionePortaleAlice eq "true" }'>
		<gene:redefineInsert name="schedaModifica">
			<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.ModificaDatiRegistrati")}'>
				<tr>
					<td class="vocemenulaterale">
						<a href="javascript:schedaModifica();" title="Modifica dati registrati" tabindex="1501">
						Modifica dati registrati</a></td>
				</tr>
			</c:if>
		</gene:redefineInsert>
		<gene:redefineInsert name="pulsanteModifica" >
			<c:if test='${gene:checkProtFunz(pageContext,"MOD","SCHEDAMOD") && gene:checkProt(pageContext,"FUNZ.VIS.ALT.GENE.ModificaDatiRegistrati")}'>
				<INPUT type="button"  class="bottone-azione" value='Modifica dati registrati' title='Modifica dati registrati' onclick="javascript:schedaModifica()">
			</c:if>
		</gene:redefineInsert>
	</c:if>
	<c:if test='${impresaRegistrata eq "SI"  and isIntegrazionePortaleAlice ne "true"}'>
		<gene:redefineInsert name="schedaModifica"/>
		<gene:redefineInsert name="pulsanteModifica" />
	</c:if>
	
	<gene:redefineInsert name="pulsanteSalva">
		<INPUT type="button" class="bottone-azione" value="Salva" title="Salva modifiche" onclick="javascript:schedaConfermaImprese();">
	</gene:redefineInsert>

</gene:formScheda>

<gene:javaScript>

	

<c:if test='${(modoAperturaScheda ne "VISUALIZZA")}'>

		
  	function schedaConfermaImprese(){
		var continua = true;
		
		for(var i=1; i < maxIdRAGIMPVisualizzabile; i++){
			var codiceImpresa = getValue("RAGIMP_CODDIC_" + i);
			if(document.getElementById("rowtitoloRAGIMP_" + i).style.display != "none"){		
				if(codiceImpresa != null && codiceImpresa != ""){
				
					for(var jo=(i+1); jo <= maxIdRAGIMPVisualizzabile; jo++){
						if(document.getElementById("rowtitoloRAGIMP_" + jo).style.display != "none" && codiceImpresa == getValue("RAGIMP_CODDIC_" + jo)){
							continua = false;
							outMsg("Sono state definite pi&ugrave; imprese con lo stesso codice (" + codiceImpresa + ")", "ERR");
							onOffMsg();
					  }
				  }
			  }
		  }
	  }
	  if(continua){
		  schedaConferma();
	  }
  }

  //showObj("rowMsgUltimaImpresa", false);

</c:if>

function art80submit(codimp,operazione) {
	var _contextPath = "${pageContext.request.contextPath}";
	
	var href = "";
	if (operazione == 'crea') {
		href = "gene/impr/impr-art80-crea-oe.jsp";
	} else if (operazione == 'consulta') {
		href = "gene/impr/impr-art80-consulta-oe.jsp";
	}
	
	bloccaRichiesteServer();
	var _form = $("<form>", {
        "action": _contextPath + "/ApriPagina.do",
        "name": "formArt80CreaOE",
        "method": "post"
    }).append($('<input>', {
        "name": "href",
        "value": href,
        "type": "hidden"
    })).append($('<input>', {
        "name": "codimp",
        "value": codimp,
        "type": "hidden"
    })).append($('<input>', {
        "name": "_csrf",
        "value": "${param._csrf}",
        "type": "hidden"
    }));
    $("body").append(_form);
    _form.submit();
} 

</gene:javaScript>