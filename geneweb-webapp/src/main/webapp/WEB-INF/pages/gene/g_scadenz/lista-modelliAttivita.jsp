<%
/*
 * Created on: 09-05-13
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

/*
	Descrizione:
		Finestra per la selezione di un modello per l'attività 
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />


<c:choose>
	<c:when test='${not empty param.discriminante}'>
		<c:set var="discriminante" value="${param.discriminante}" />
	</c:when>
	<c:otherwise>
		<c:set var="discriminante" value="${discriminante}" />
	</c:otherwise>
</c:choose>


<c:choose>
	<c:when test='${not empty param.ScadenzarioModificabile}'>
		<c:set var="ScadenzarioModificabile" value="${param.ScadenzarioModificabile}" />
	</c:when>
	<c:otherwise>
		<c:set var="ScadenzarioModificabile" value="${ScadenzarioModificabile}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.keyAdd}'>
		<c:set var="chiavi" value="${param.keyAdd}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiavi" value="${chiavi}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.entitaPartenza}'>
		<c:set var="entitaPartenza" value="${param.entitaPartenza}" />
	</c:when>
	<c:otherwise>
		<c:set var="entitaPartenza" value="${entitaPartenza}" />
	</c:otherwise>
</c:choose>



<c:set var="entita" value='${fn:substringBefore(keyParent,".")}' />

<c:set var="IsG_MODSCADENZPopolata" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.IsG_MODSCADENZPopolataFunction",pageContext,moduloAttivo,entitaPartenza,discriminante)}' />

<div style="width:97%;">
<gene:template file="popup-template.jsp">
<gene:setString name="titoloMaschera" value="Creazione attività da modello" />

	
<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G_SCADENZ" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestoreG_SCADENZ">
	
	<c:choose>
		<c:when test='${requestScope.modelloAssociato eq "1"}'>
			<br>
			Operazione conclusa con successo
			<br>
			<br>
		</c:when>
		<c:otherwise>
			<br>
			Selezionare il modello per la creazione delle attività
			<br>
			<br>
		</c:otherwise>
	</c:choose>
	
		
	<table class="dettaglio-notab">
	 
	 
	 <c:if test='${empty requestScope.modelloAssociato or requestScope.modelloAssociato ne "1"}' >
	 <tr>
	  	<td>
	  		<c:forEach items="${listaModelliAttivita}" var="Modello" varStatus="stato">
					<p>
				<input type="radio" name="numModello" value="${Modello[0]}" <c:if test="${stato.first}" >checked="CHECKED" </c:if> />&nbsp;Crea le attivit&agrave; a partire dal modello "<c:out value="${Modello[1]}" />"<c:if test="${! empty Modello[2]}" > - <c:out value="${Modello[2]}" /></c:if>
					</p>
			</c:forEach>
			<br>
	  	 </td>
	   </tr>
	  </c:if>
	  
	  <tr>
	   <gene:campoScheda>
		<td class="comandi-dettaglio" colSpan="2">
			<c:choose>
				<c:when test='${empty requestScope.modelloAssociato or requestScope.modelloAssociato ne "1"}' >
					<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
					<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();">&nbsp;&nbsp;
				</c:when>
				<c:otherwise>
					<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
				</c:otherwise>
			</c:choose>
			<input type="hidden" name="discriminante" id="discriminante" value="${discriminante}" />
				<input type="hidden" name="entita" id="entita" value="${entita}" />
				<input type="hidden" name="modello" id="modello" value="" />
				<input type="hidden" name="chiavi" id="chiavi" value="${chiavi }" />
				<input type="hidden" name="chiave" id="chiave" value="${param.chiave }" />
				<input type="hidden" name="entitaPartenza" id="entitaPartenza" value="${entitaPartenza }" />
				<input type="hidden" name="ScadenzarioModificabile" id="ScadenzarioModificabile" value="${ScadenzarioModificabile }" />
			
		</td>
	</gene:campoScheda>
	   
	   
	    </tr>	
	</table>
	
		
		
	</gene:formScheda>
  </gene:redefineInsert>
	
	
	
	
	<gene:javaScript>
				
		function chiudi(){
			window.opener.document.forms[0].pgSort.value = "";
			window.opener.document.forms[0].pgLastSort.value = "";
			window.opener.document.forms[0].pgLastValori.value = "";
			window.opener.bloccaRichiesteServer();
			window.opener.listaVaiAPagina(0);
			window.close();
		}
		
		function annulla(){
			window.close();
		}
		
		function conferma() {
			var radioModello = document.forms[0].numModello;
			var numModello;
			if (radioModello != null) {
				if(radioModello.length!=null){
					for(var i = 0; i < radioModello.length; i++) { 
						if(radioModello[i].checked) { // scorre tutti i vari radio button
							numModello = radioModello[i].value; // valore radio scelto
							break; // esco dal cliclo
						}
					}	
				}else{
					if(radioModello.checked) 
						numModello = radioModello.value; // valore radio scelto
				}
				
			}
			document.forms[0].modello.value=numModello;
			document.forms[0].jspPathTo.value="gene/g_scadenz/lista-modelliAttivita.jsp";
			schedaConferma();
		}
	</gene:javaScript>
</gene:template>
</div>

	