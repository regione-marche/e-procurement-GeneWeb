<%
/*
     * Created on: 03/06/2011
     *
     * Copyright (c) EldaSoft S.p.A.
     * Tutti i diritti sono riservati.
     *
     * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
     * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
     * aver prima formalizzato un accordo specifico con EldaSoft.

     Descrizione:
		Selezione del dettaglio delle categoria dell'intervento
		Parametri:
			param.modo 
				Modo di apertura MODIFICA per modifica o VISUALIZZA per visualizzazione
			param.campo 
				Nome del campo in cui vi è il dettaglio
			param.valore 
				Valore del campo

*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<c:choose>
	<c:when test='${empty param.valore}'>
		<c:set var="valore" value="" />
	</c:when>
	<c:otherwise>
		<c:set var="valore" value="${param.valore}" />
	</c:otherwise>
</c:choose>

<gene:template file="popup-template.jsp">
	<gene:setString name="titoloMaschera" value="Dettaglio codice NUTS"/>
	<gene:redefineInsert name="corpo">
		<gene:formScheda gestisciProtezioni="false" entita="">
			<gene:campoScheda campo="CODICE" nome="CODICE" title="Codice NUTS" definizione="T5" value="${valore}"/>
			<gene:campoScheda><td colspan="2">&nbsp;</td></gene:campoScheda>
			<gene:campoScheda campo="PAESE" nome="PAESE" title="Paese" definizione="T2" gestore="it.eldasoft.gene.web.struts.tags.gestori.decoratori.GestoreCampoTABNUTS_PAESE" 
				value="${fn:substring( valore, 0,2 )}" />
			<gene:campoScheda campo="AREA" nome="AREA" title="Area" definizione="T1" gestore="it.eldasoft.gene.web.struts.tags.gestori.decoratori.GestoreCampoTABNUTS_AREA"
				value="${fn:substring( valore, 2,3 )}" />
			<gene:campoScheda campo="REGIONE" nome="REGIONE" title="Regione" definizione="T1" gestore="it.eldasoft.gene.web.struts.tags.gestori.decoratori.GestoreCampoTABNUTS_REGIONE" 
				value="${fn:substring( valore, 3,4 )}" />
			<gene:campoScheda campo="PROVINCIA" nome="PROVINCIA" title="Provincia" definizione="T1" gestore="it.eldasoft.gene.web.struts.tags.gestori.decoratori.GestoreCampoTABNUTS_PROVINCIA" 
				value="${fn:substring( valore, 4,5 )}" />
			<gene:campoScheda visibile='${param.modo eq "MODIFICA"}'>
				<td class="comandi-dettaglio" colSpan="2">	
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Salva modifiche" onclick="javascript:conferma()" />
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annulla()" />
						&nbsp;
				</td>			
			</gene:campoScheda>

			<gene:fnJavaScriptScheda funzione='modifyPaese()' elencocampi="PAESE" esegui="false" />
			<gene:fnJavaScriptScheda funzione='modifyArea()' elencocampi="AREA" esegui="false" />
			<gene:fnJavaScriptScheda funzione='modifyRegione()' elencocampi="REGIONE" esegui="false" />
			<gene:fnJavaScriptScheda funzione='modifyProvincia()' elencocampi="PROVINCIA" esegui="false" />
			<gene:fnJavaScriptScheda funzione='reloadCPVNUTS()' elencocampi="CODICE" esegui="false" />
			
		</gene:formScheda>
		
  </gene:redefineInsert>

	<gene:javaScript>

		function modifyPaese(){
			setValue("AREA","", false);
			setValue("REGIONE","", false);
			setValue("PROVINCIA","", false);
			calcolaCPVNUTS();
		}


		function modifyArea(){
			setValue("REGIONE","", false);
			setValue("PROVINCIA","", false);
			calcolaCPVNUTS();
		}
		
		function modifyRegione(){
			setValue("PROVINCIA","", false);
			calcolaCPVNUTS();
		}		
		
		function modifyProvincia(){
			calcolaCPVNUTS();
		}

		
		function calcolaCPVNUTS(){
			var valore="";
			if(getValue("PAESE")!=""){
				valore+=getValue("PAESE");
				valore+=getValue("AREA");
				valore+=getValue("REGIONE");
				valore+=getValue("PROVINCIA");
			}
			setValue("CODICE",valore);
		}
		
		function reloadCPVNUTS() {
			bloccaRichiesteServer();
			var valore=getValue("CODICE");
			if(valore!=null && valore!="")
				valore=valore.toUpperCase();
			document.location.href="${pageContext.request.contextPath}/ApriPagina.do?"+csrfToken+"&href=gene/tabnuts/dettaglio-codice-nuts.jsp&modo=MODIFICA&campo=${param.campo}&valore="+valore;
		}
		
		function conferma() {
			var codice = getValue("CODICE");
			var codiceRicalcolato = ricalcolaNUTS();
			if (codice == codiceRicalcolato) {
				opener.setValue("${param.campo}", codice);
				window.close();
			} else {
				outMsg("Codice NUTS non valido", "ERR");
				onOffMsg();
			}
		}
		
		function annulla(){
			window.close();
		}
		
		function ricalcolaNUTS() {
			var valore="";
			if(getValue("PAESE")!=""){
				valore+=getValue("PAESE");
				valore+=getValue("AREA");
				valore+=getValue("REGIONE");
				valore+=getValue("PROVINCIA");
			}
			return valore;
		}

	</gene:javaScript>
</gene:template>

