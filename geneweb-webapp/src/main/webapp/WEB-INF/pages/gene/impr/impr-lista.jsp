<%
/*
 * Created on: 08-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista dei tecnici progettisti */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="archiviFiltrati" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetPropertyFunction", "it.eldasoft.associazioneUffintAbilitata.archiviFiltrati")}'/>

<c:set var="filtroUffint" value=""/> 
<c:if test="${! empty sessionScope.uffint && fn:contains(archiviFiltrati,'IMPR')}">
	<c:set var="filtroUffint" value="CGENIMP = '${sessionScope.uffint}'"/>
</c:if>

<c:set var="isPopolatatW_PUSER"
	value='${gene:callFunction("it.eldasoft.gene.tags.functions.isPopolatatW_PUSERFunction", pageContext)}' />

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="ImprLista" >
	<gene:setString name="titoloMaschera" value="Lista anagrafica delle imprese"/>
	<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.ImprScheda")}'/>

	<gene:redefineInsert name="corpo">
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		<table class="lista">
		<tr><td >
			<gene:formLista entita="IMPR" sortColumn="3" pagesize="20" tableclass="datilista"
			where="${filtroUffint}" 
			gestisciProtezioni="true" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreIMPR"> 
				<gene:redefineInsert name="listaNuovo" />
				<gene:redefineInsert name="listaEliminaSelezione" />
				<gene:redefineInsert name="addToAzioni" >
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaNuovo();" title="Inserisci" tabindex="1502">
								${gene:resource("label.tags.template.lista.listaNuovo")}</a></td>
					</tr>
					</c:if>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
					<tr>
						<td class="vocemenulaterale">
							<a href="javascript:listaEliminaSelezione();" title="Elimina selezionati" tabindex="1503">
								${gene:resource("label.tags.template.lista.listaEliminaSelezione")}</a>
						</td>
					</tr>
					</c:if>
				</gene:redefineInsert>
				
				<c:set var="impresaRegistrata" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.ImpresaRegistrataSuPortaleFunction",  pageContext, fn:substringAfter(chiaveRigaJava, ":") )}'/>
				
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						
						<% //Aggiunta dei menu sulla riga %> 
						<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.ImprScheda")}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza anagrafica impresa"/>
						</c:if>
						<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.ImprScheda") and gene:checkProtFunz(pageContext, "MOD", "MOD")}' >
							<c:if test='${impresaRegistrata ne "SI"}'>
								<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica anagrafica impresa" />
							</c:if>
							
						</c:if>
						<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL") and (impresaRegistrata ne "SI" or (impresaRegistrata eq "SI" and isIntegrazionePortaleAlice eq "true" and  gene:checkProt(pageContext,"FUNZ.VIS.DEL.GENE.ImprLista.EliminaImpresaRegistrata")))}' >
							<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.elimina" title="Elimina anagrafica impresa" href="eliminaImpresa('${impresaRegistrata }')"/>
						</c:if>
					</gene:PopUp>
								
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL") and impresaRegistrata ne "SI"}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>

				<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
				<gene:campoLista campo="CODIMP" headerClass="sortable" width="90"/>
				<gene:campoLista campo="NOMEST" headerClass="sortable"  href="${gene:if(visualizzaLink, link, '')}"/>
				<gene:campoLista campo="CFIMP" headerClass="sortable" width="120"/>
				<gene:campoLista campo="PIVIMP" headerClass="sortable" width="100"/>
				<gene:campoLista campo="LOCIMP" headerClass="sortable" width="120"/>
				<gene:campoLista campo="INTERD" headerClass="sortable" width="80"/>
				<c:if test="${isPopolatatW_PUSER == 'SI'}">
					<gene:campoLista title="&nbsp;" width="20" >
						<c:if test="${impresaRegistrata == 'SI'}">
							<img width="16" height="16" title="Impresa registrata su portale" alt="Impresa registrata su portale" src="${pageContext.request.contextPath}/img/ditta_acquisita.png"/>
						</c:if>
						
					</gene:campoLista>
				</c:if>
			</gene:formLista>
		</td></tr>
		<tr>
			<td class="comandi-dettaglio" colSpan="2">
				<gene:insert name="addPulsanti"/>
				<gene:insert name="pulsanteListaInserisci">
					<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaNuovo")}' title='${gene:resource("label.tags.template.lista.listaNuovo")}' onclick="javascript:listaNuovo()">
					</c:if>
				</gene:insert>
				<gene:insert name="pulsanteListaEliminaSelezione">
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' title='${gene:resource("label.tags.template.lista.listaEliminaSelezione")}' onclick="javascript:listaEliminaSelezione()">
					</c:if>
				</gene:insert>
			
				&nbsp;
			</td>
		</tr>
		</table>
  </gene:redefineInsert>
	<gene:javaScript>
		function eliminaImpresa(impresaRegistrata){
			var msg="Procedere con l'eliminazione?";
			if(impresaRegistrata=="SI")
				msg="L'impresa selezionata è registrata su portale.\nProcedere ugualmente con l'eliminazione?";
			if(classePopUpElimina==null){
				// Eliminazione senza popup di conferma
				if(confirm(msg)){
					listaEliminaPopUp();
				}
			}else{
				showConfermaPopUp("elimina",classePopUpElimina,chiaveRiga,"listaEliminaPopUp");
			}
		}
	</gene:javaScript>	
</gene:template>
