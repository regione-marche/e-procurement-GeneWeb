<%/*
   * Created on 18-ago-2009
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

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_NOTEAVVISI-Lista">
	<gene:setString name="titoloMaschera" value="Lista note ed avvisi"/>
	<gene:redefineInsert name="corpo">

		<c:if test='${not gene:checkProtFunz(pageContext, "INS","LISTANUOVO") or sessionScope.entitaPrincipaleModificabile ne "1"}' >
			<gene:redefineInsert name="pulsanteListaInserisci" />
			<gene:redefineInsert name="listaNuovo" />
		</c:if>

		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>

        <c:if test='${not empty param.chiave}'>
    		<c:set var="campiKey" value='${fn:split(param.chiave,";")}' />
    		<c:set var="addKeyRiga" value="" />
    		<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
    			<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
    			<c:choose>
    				<c:when test="${indicekey.last}">
    					<c:set var="addKeyRiga" value='${addKeyRiga}G_NOTEAVVISI.NOTEKEY${indicekey.index}=T:${strTmp}' />
    				</c:when>
    				<c:otherwise>
    					<c:set var="addKeyRiga" value='${addKeyRiga}G_NOTEAVVISI.NOTEKEY${indicekey.index}=T:${strTmp};' />
    				</c:otherwise>
    			</c:choose>
    		</c:forEach>
    	</c:if>

		<table class="lista">
			<tr>
				<td>

		<gene:formLista entita="G_NOTEAVVISI" pagesize="20" tableclass="datilista" sortColumn="-7" gestisciProtezioni="true"
				gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreG_NOTEAVVISI" 
				plugin="it.eldasoft.gene.tags.gestori.plugin.GestoreNoteAvvisiPlugin" >
		<c:if test='${not empty param.chiave}'>
			<c:set var="key" value="${param.chiave}" />
			<c:set var="keyParent" value="${param.chiave}" />
		</c:if>
				
  		<gene:campoLista title="Opzioni<br><center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}" >
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_NOTEAVVISI-Scheda")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza nota o avviso"/>
						</c:if>
						<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" and (sessionScope.profiloUtente.id eq datiRiga.G_NOTEAVVISI_AUTORENOTA or datiRiga.G_NOTEAVVISI_TIPONOTA eq "1" or datiRiga.G_NOTEAVVISI_TIPONOTA eq "3") and gene:checkProt(pageContext, "MASC.VIS.GENE.G_NOTEAVVISI-Scheda") && gene:checkProt(pageContext, "MASC.MOD.GENE.G_NOTEAVVISI-Scheda") and gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica nota o avviso"/>
						</c:if>
						<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" and ((sessionScope.profiloUtente.id eq datiRiga.G_NOTEAVVISI_AUTORENOTA and datiRiga.G_NOTEAVVISI_TIPONOTA eq "2") or datiRiga.G_NOTEAVVISI_TIPONOTA eq "1")  and gene:checkProtFunz(pageContext, "DEL", "LISTADEL")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.elimina" title="Elimina nota o avviso" />
						</c:if>
						<c:if test='${sessionScope.entitaPrincipaleModificabile eq "1" and sessionScope.profiloUtente.id eq datiRiga.G_NOTEAVVISI_AUTORENOTA}' >
							<input type="checkbox" name="keys" value="${chiaveRigaJava}" />
						</c:if>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			
			<input type="hidden" name="keyAdd" value="${param.keyAdd}" />
			<% // Campi veri e propri %>
			<gene:campoLista campo="NOTECOD" visibile="false" />
			<gene:campoLista campo="NOTEPRG" visibile="false" />
			<gene:campoLista campo="AUTORENOTA" visibile="false" />
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="TIPONOTA" title="Tipo" ordinabile="true" href="${link}"/>
			<gene:campoLista campo="SYSUTE" entita="USRSYS" title="Autore" definizione="T" where="USRSYS.SYSCON=G_NOTEAVVISI.AUTORENOTA" width="100" ordinabile="true" />
			<gene:campoLista campo="DATANOTA" title="Data" ordinabile="true" />
			<gene:campoLista campo="STATONOTA" ordinabile="true" />
			<gene:campoLista campo="TITOLONOTA" ordinabile="true" />
		</gene:formLista>
				</td>
			</tr>
			<c:if test='${sessionScope.entitaPrincipaleModificabile ne "1" and gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL")}'>
				<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
				<gene:redefineInsert name="listaEliminaSelezione" />
			</c:if>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	</gene:redefineInsert>
	<gene:javaScript>
	<c:if test='${not empty param.chiave}'>
		document.forms[0].keyParent.value="${param.chiave}";
		document.forms[0].keyAdd.value="${sessionScope['addKeyRiga']}";
	</c:if>
	</gene:javaScript>
</gene:template>