<%
/*
 * Created on: 17-lug-2008
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
/* Lista Durc On Line */
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%/*Imposto il menu nel titolo*/%>
<gene:set name="titoloMenu">
	<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
</gene:set>

<table class="dettaglio-tab-lista">
	<tr>
		<td>
			<gene:formLista entita="IMPDURC" where="IMPDURC.CODIMP = #IMPR.CODIMP#"  sortColumn="-4" pagesize="20" tableclass="datilista" gestisciProtezioni="true" > 
				<!-- Se il nome del campo è vuoto non lo gestisce come un campo normale -->
				<gene:campoLista title="Opzioni<center>${titoloMenu}</center>" width="50">
					<gene:PopUp variableJs="rigaPopUpMenu" onClick="chiaveRiga='${chiaveRigaJava}'"/>
					<c:if test='${gene:checkProtFunz(pageContext,"DEL","LISTADELSEL")}'>
						<input type="checkbox" name="keys" value="${chiaveRiga}"  />
					</c:if>
				</gene:campoLista>
				<% // Campi veri e propri %>
				<gene:campoLista campo="NPROTO" headerClass="sortable" href="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();"/>
				<gene:campoLista campo="DATRIC" headerClass="sortable"/>
				<gene:campoLista campo="DATSCA" headerClass="sortable" />
				<gene:campoLista campo="TIPESI" headerClass="sortable" />
			</gene:formLista>

			<c:if test='${gene:checkProtObj(pageContext, "MASC.VIS", "GENE.ImpcaseScheda"  )}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.visualizza" title="Visualizza richiesta DURC on line"/>
			</c:if>
			<c:if test='${gene:checkProtFunz(pageContext, "MOD", "MOD"  )}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.modifica" title="Modifica richiesta DURC on line" />
			</c:if>
			<c:if test='${gene:checkProtFunz(pageContext, "DEL", "DEL"  )}' >
				<gene:PopUpItemResource variableJs="rigaPopUpMenu" resource="popupmenu.tags.lista.elimina" title="Elimina richiesta DURC on line" />
			</c:if>
		</td>
	</tr>
	<tr>
	<% // Ridefinizione dei pulsanti specificati nel file /WEB-INF/pages/commons/pulsantiListaPage.jsp %>
		<td class="comandi-dettaglio" colSpan="2">
			<gene:insert name="addPulsanti"/>
			<gene:insert name="pulsanteListaInserisci">
				<c:if test='${gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
					<INPUT type="button"  class="bottone-azione" value='${gene:resource("label.tags.template.lista.listaPageNuovo")}' title='${gene:resource("label.tags.template.lista.listaPageNuovo")}' onclick="javascript:listaNuovo()">
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