<%
/*
 * Created on: 13-04-2013
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
		Finestra per la funzione di salvataggio come modello
*/
%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<div style="width:97%;">
<gene:template file="popup-template.jsp">
<gene:setString name="titoloMaschera" value='Nuovo modello di scadenzario' />

<c:choose>
	<c:when test='${not empty param.ent}'>
		<c:set var="ent" value="${param.ent}" />
	</c:when>
	<c:otherwise>
		<c:set var="ent" value="${ent}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.discriminante}'>
		<c:set var="discriminante" value="${param.discriminante}" />
	</c:when>
	<c:otherwise>
		<c:set var="discriminante" value="${discriminante}" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${not empty param.chiave}'>
		<c:set var="chiave" value="${param.chiave}" />
	</c:when>
	<c:otherwise>
		<c:set var="chiave" value="${chiave}" />
	</c:otherwise>
</c:choose>

<c:set var="IsG_MODSCADENZPopolata" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.IsG_MODSCADENZPopolataFunction",pageContext,moduloAttivo,ent,discriminante)}' />

<c:set var="modo" value="NUOVO" scope="request" />
	
	<gene:redefineInsert name="corpo">
	<gene:formScheda entita="G_MODSCADENZ" gestisciProtezioni="false" gestore="it.eldasoft.gene.web.struts.tags.gestori.GestorePopupSalvaComeModello">
	
	<c:if test='${(empty requestScope.salvataggioModelloEseguito or requestScope.salvataggioModelloEseguito ne "1") and IsG_MODSCADENZPopolata eq "true"}' >
	<table class="dettaglio-notab">
		<tr>
	  	<td>
	  		<table class="dettaglio-notab" id="Modello">
	  			<tr><br><b>Selezionare "Nuovo modello" per creare un nuovo modello, altrimenti selezionare un modello esistente per aggiornarlo:</b><br></tr>
				<tr>
					<br>&nbsp;<input type="radio" name="numModello" value="0" checked="CHECKED" onclick="javascript:visualizzaCodice(1,'');"/>&nbsp;
					Nuovo modello<br><br>
					
				</tr>	
				<c:forEach items="${listaModelliAttivita}" var="Modello" varStatus="stato" >
					<tr>
						&nbsp;<input type="radio" name="numModello" value="${Modello[0]}" onclick="javascript:visualizzaCodice(2,'${Modello[0]}');"/>&nbsp;
						Modello ${Modello[1]}<br>
						&nbsp;&nbsp;<i>${Modello[2]}</i><br><br>
					</tr>	
				</c:forEach>
						
			</table>
		</td>
	   </tr>
	</table>
	</c:if>
	
	<c:if test='${not empty requestScope.salvataggioModelloEseguito and requestScope.salvataggioModelloEseguito eq "1"}' >
	<gene:campoScheda>
		<td colSpan="2">
			<br>
				Salvataggio del modello eseguito con successo
			<br>&nbsp;
			<br>&nbsp;
		</td>
	</gene:campoScheda>
	</c:if>
	<c:if test='${empty requestScope.salvataggioModelloEseguito or requestScope.salvataggioModelloEseguito ne "1"}' >
		<gene:campoScheda campo="COD" campoFittizio="true" definizione="T30;0;;;G_MODSCADCOD" obbligatorio="true"/>
		<gene:campoScheda campo="TIT" campoFittizio="true" definizione="T50;0;;;G_MODSCADTIT" obbligatorio="true"/>
		<gene:campoScheda campo="DESCR" campoFittizio="true" definizione="T2000;0;;NOTE;G_MODSCADDESCR" />
	</c:if>
	
	
		<input type="hidden" name="ent" id="ent" value="${ent}" />
		<input type="hidden" name="discriminante" id="discriminante" value="${discriminante}" />
		<input type="hidden" name="chiave" id="chiave" value="${chiave}" />
		
		<gene:campoScheda>
			<td class="comandi-dettaglio" colSpan="2">
				<c:choose>
					<c:when test='${empty requestScope.salvataggioModelloEseguito or requestScope.salvataggioModelloEseguito ne "1"}' >
						<INPUT type="button" class="bottone-azione" value="Conferma" title="Conferma" onclick="javascript:conferma();">
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</c:when>
					<c:otherwise>
						<INPUT type="button" class="bottone-azione" value="Chiudi" title="Chiudi" onclick="javascript:chiudi();">&nbsp;&nbsp;
					</c:otherwise>
				</c:choose>
				
				
			</td>
		</gene:campoScheda>
		
	</gene:formScheda>
  </gene:redefineInsert>


	
	<gene:javaScript>
		
		arrayModelli = new Array();
		<c:forEach items="${listaModelliAttivita}" var="modello" varStatus="indice" >
			arrayModelli[${indice.index}] = new Array("${modello[0]}", "${modello[3]}","${modello[4]}");
		</c:forEach>
						
		function conferma() {
			//Se si sta inserendo un nuovo modello, si controlla che il codice inserito non sia già adoperato
			var newCod = getValue("COD");
			var radioModello = document.forms[0].numModello;			
			var esitoControlli="ok";
			if (radioModello != null) {
				if(radioModello.length!=null && radioModello[0].checked){
					for(var i = 0; i < radioModello.length; i++) { 
						if(newCod == radioModello[i].value){
							alert("Non è possibile procedere, si è inserito il codice di un modello già esistente");
							esitoControlli="nok";
							break; // esco dal cliclo
						} 
					}	
				}
			}
			if(esitoControlli=="ok"){
				document.forms[0].jspPathTo.value="gene/g_scadenz/popup-salva-modello.jsp";
				schedaConferma();
			}
			
			
		}
		
		function chiudi(){
			window.close();
		}
		
		function visualizzaCodice(campoVisibile, valore){
			if(campoVisibile==1){
				showObj("rowCOD", true);
			}else{
				showObj("rowCOD", false);
			}
			setValue("COD",valore);
			
			if(valore!=null && valore!=''){
				for(var i=0; i < arrayModelli.length; i++){
					if(valore == arrayModelli[i][0]){
						setValue("TIT",arrayModelli[i][1]);
						setValue("DESCR",arrayModelli[i][2]);
					}
				}
			}else{
				setValue("TIT","");
				setValue("DESCR","");
			}	
		}
	</gene:javaScript>
</gene:template>
</div>

	