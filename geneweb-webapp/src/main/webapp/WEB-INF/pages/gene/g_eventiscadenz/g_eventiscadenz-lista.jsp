<%--
/*
 * Created on: 07-mag-2013
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
 Form di lista degli eventi associabili ad uno scadenzario

--%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="visualizzaLink" value='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_EVENTISCADENZ-scheda")}'/>

<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_EVENTISCADENZ-lista">

	<gene:setString name="titoloMaschera" value="Lista eventi per scadenzario"/>

	<gene:redefineInsert name="listaEliminaSelezione"></gene:redefineInsert>

	<gene:redefineInsert name="corpo">
		<table class="lista">
			<tr>
				<td>	
				  	<gene:formLista entita="G_EVENTISCADENZ" gestisciProtezioni="true" where="PRG='${moduloAttivo}'" sortColumn="5" tableclass="datilista"
				  		gestore="it.eldasoft.gene.tags.gestori.submit.GestoreEventoScadenzario">
						<gene:campoLista title="Opzioni" width="50">
							<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_EVENTISCADENZ-scheda")}' >
									<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.visualizza" title="Visualizza evento"/>
								</c:if>
								<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_EVENTISCADENZ-scheda") && gene:checkProtFunz(pageContext, "MOD","MOD")}' >
									<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.modifica" title="Modifica evento"/>
								</c:if>
								<c:if test='${gene:checkProtFunz(pageContext, "DEL","DEL")}' >
									<gene:PopUpItemResource variableJs="rigaPopUpMenu${currentRow}" resource="popupmenu.tags.lista.elimina" title="Elimina evento" />
								</c:if>
							</gene:PopUp>
						</gene:campoLista>
						<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
						<gene:campoLista campo="COD" href="${gene:if(visualizzaLink, link, '')}"/>
						<gene:campoLista campo="ENT"/>
						<gene:campoLista campo="DISCR"/>
						<gene:campoLista campo="TIT"/>
						<gene:campoLista campo="DESCR"/>
					</gene:formLista>
				</td>
			</tr>
			<tr>
				<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
	</gene:redefineInsert>
</gene:template>
