<%
/*
 * Created on: 20/02/2020
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
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>


<c:set var="idVerifica" value='${gene:getValCampo(key, "VERIFICHE.ID")}' scope="request"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />
<c:choose>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou228#")}'>
		<c:set var="opzVerifiche" value="ou228" />
	</c:when>
	<c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou227#")}'>
		<c:set var="opzVerifiche" value="ou227" />
	</c:when>
	<c:otherwise>
		<c:set var="opzVerifiche" value="" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${param.archiviati eq 1}'>
		<c:set var="whereDocVerifiche" value="DOCUMENTI_VERIFICHE.ID_VERIFICA=#VERIFICHE.ID# AND (ISARCHI = '1')" />
	</c:when>
	<c:otherwise>
		<c:set var="whereDocVerifiche" value="DOCUMENTI_VERIFICHE.ID_VERIFICA=#VERIFICHE.ID# AND (ISARCHI IS NULL OR ISARCHI <> '1')" />
	</c:otherwise>
</c:choose>

	<table class="dettaglio-tab-lista">
	<tr>
		<td>
		<gene:formLista entita="DOCUMENTI_VERIFICHE" where="${whereDocVerifiche}"
		tableclass="datilista" sortColumn='2' gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreDocumentiVerifiche" pagesize="25" >
			<c:if test='${!(opzVerifiche eq "ou228")}'>
				<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>				
				<gene:redefineInsert name="pulsanteListaEliminaSelezione"></gene:redefineInsert>
			</c:if>							
			<gene:redefineInsert name="listaNuovo"></gene:redefineInsert>
			<gene:redefineInsert name="pulsanteListaInserisci"></gene:redefineInsert>
					
			<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
				<gene:PopUp variableJs="rigaPopUpMenu" onClick="chiaveRiga='${chiaveRigaJava}'"/>
				<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") && fn:contains(listaOpzioniUtenteAbilitate, "ou228#")}'>
					<input type="checkbox" name="keys" value="${chiaveRiga}"  />
				</c:if>
			</gene:campoLista>

			<gene:campoLista campo="ID" visibile="false" />
			<gene:campoLista campo="ID_VERIFICA" visibile="false" />
			<gene:campoLista campo="TIPO" />
			<gene:campoLista campo="ESITO_VERIFICA_DOC" />
			<gene:campoLista campo="DESCRIZIONE" />
			<gene:campoLista campo="PROTOCOLLO_RICHIESTA" visibile="false"/>
			<gene:campoLista campo="NUMERO_PRATICA" visibile="false"/>
			<gene:campoLista campo="GG_VALIDITA" visibile="false"/>
			<gene:campoLista campo="DATA_INVIO_RICHIESTA" />
			<gene:campoLista campo="DATA_SILENZIO_ASSENSO" />
			<gene:campoLista campo="DATA_EMISSIONE" />
			<gene:campoLista campo="DATA_SCADENZA" />
			
			<gene:campoLista campo="IDPRG" visibile="false" />
			<gene:campoLista campo="IDDOCDG" visibile="false" />
						
			<gene:campoLista campo="IDPRG" visibile="false" entita="W_DOCDIG"
			 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" />
			<gene:campoLista campo="IDDOCDIG" visibile="false" entita="W_DOCDIG"
			 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" />
			<gene:campoLista campo="DIGDESDOC" visibile="false" entita="W_DOCDIG"
			 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" />
			<c:if test='${opzVerifiche eq "ou227" || opzVerifiche eq "ou228"}' >
				<gene:campoLista campo="DIGNOMDOC" entita="W_DOCDIG" visibile="true"
				 where="W_DOCDIG.IDPRG=DOCUMENTI_VERIFICHE.IDPRG AND W_DOCDIG.IDDOCDIG=DOCUMENTI_VERIFICHE.IDDOCDG" href="javascript:visualizzaFileDIGOGG('${datiRiga.DOCUMENTI_VERIFICHE_ID}', '${datiRiga.W_DOCDIG_DIGNOMDOC}','${sessionScope.moduloAttivo}','${datiRiga.W_DOCDIG_IDDOCDIG}');" />
			</c:if>	
			<c:if test='${(param.archiviati ne 1) && (opzVerifiche eq "ou228")}'>
				<gene:campoLista title="&nbsp;" width="20">
					<a href="javascript:chiaveRiga='${chiaveRigaJava}';archiviaDocVerifica();" title="Archivia documento" >
						<img width="16" height="16" title="Archivia documento verifica" alt="Archivia documento verifica" src="${pageContext.request.contextPath}/img/com_ric.png"/>
					</a>
				</gene:campoLista>
			</c:if>	
			
		</gene:formLista>
		
		
		<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.ImpcaseScheda"  )}' >
			<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.visualizza" title="Visualizza documento"/>
		</c:if>
		<c:if test='${gene:checkProtFunz(pageContext, "MOD", "MOD"  ) && (opzVerifiche eq "ou228")}' >
			<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.modifica" title="Modifica documento" />
		</c:if>
		<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL"  ) && (opzVerifiche eq "ou228")}' >
			<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.elimina" title="Elimina documento" />
		</c:if>
		</td>
	</tr>
	<tr>
		<td class="comandi-dettaglio"  colSpan="2">

		<c:if test='${(opzVerifiche eq "ou228")}'>
			<c:if test='${autorizzatoModifiche ne 2 and gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") }'>
				<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione();">
			</c:if>
			<c:if test='${param.archiviati ne 1}'>
				<INPUT type="button"  class="bottone-azione" value='Aggiungi documento' title='Aggiungi documento' onclick="javascript:aggiungi();">
			</c:if>	
		</c:if>
			&nbsp;
		</td>
	</tr>
		
		
					
	</table>
		
		
<gene:javaScript>

	function visualizzaFileDIGOGG(c0acod, dignomdoc,idprg,iddocdig) {
		var uffint= "${sessionScope.uffint}";
		if (confirm("Si sta per scaricare (download) una copia del file in locale. Ogni modifica verrà apportata alla copia locale ma non all\'originale. Continuare?"))
		{
			var href = "${pageContext.request.contextPath}/VisualizzaFileDIGOGG.do";
			document.location.href = href+"?"+csrfToken+"&c0acod=" + c0acod + "&dignomdoc=" + dignomdoc + "&digent=" + "DOCUMENTI_VERIFICHE" + "&uffint=" + uffint;
		}
	}
		
	function aggiungi(){
		var idVerifica="${idVerifica}";
		var href = "href=gene/verifiche/verifiche-schedaPopup-insertdoc.jsp";
		href += "&modo=NUOVO&idVerifica=" + idVerifica+"&codimp=" + "'000024'";
		openPopUpCustom(href, "aggiungiDocumentazione", 800, 500, "yes", "yes");
	}
	
	function listaElimina(){
		var message = "Attenzione: eliminando la verifica si elimineranno tutti i documenti collegati.Confermi l'eliminazione?";
		if(confirm(message)){
			document.forms[0].key.value = chiaveRiga;
			document.forms[0].metodo.value = "elimina";
			document.forms[0].submit();
		}
	}
	
	
	function archiviaDocVerifica(){
		var href = "href=gene/verifiche/popup-archivia-docverifica.jsp&key="+chiaveRiga;
		openPopUpCustom(href, "archiviaDocVerifica", 500, 350, "no", "yes");
	}	

</gene:javaScript>
