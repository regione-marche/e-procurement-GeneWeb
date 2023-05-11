<%
/*
 * Created on 06-mar-2007
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

 // PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DATI GENERALI 
 // CONTENENTE IL FORM PER IMPOSTARE I DATI DELLA RICERCA CON PROSPETTO
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<script type="text/javascript">   
<%
   /* Generazione array delle estensioni ammesse per la composizione in PDF */
   String[] listaEstPdf = (String[]) request.getAttribute("listaEstPdf");
   String   elemlistaEstPdf = "";
   String dichiarazioneArrayPdf = "var listaEstPdf =  [";
   for (int i = 0; i< listaEstPdf.length; i++){
     if (i != 0) {
	   dichiarazioneArrayPdf += ",";
	 }
	 elemlistaEstPdf = listaEstPdf[i];
	 dichiarazioneArrayPdf += '\"' + elemlistaEstPdf + '\"';
   }
   dichiarazioneArrayPdf += "];";
%>

<%=dichiarazioneArrayPdf%>
	
	function estensionePdfValida(estensione){
		var res = false;
		if (listaEstPdf.indexOf(estensione)!=-1 || (listaEstPdf.length == 1 && "*".equals(listaEstPdf[0]))){
			res = true;
		}
		return res;
	}
	
	function estensioneFile(file){
		var fileName = file;
		if(file.indexOf("#")!=-1){
			fileName = $(file).val().replace(/C:\\fakepath\\/i, '');
		}
		var n = fileName.lastIndexOf(".");
		var estensione = fileName.substring(n+1).toUpperCase();
		return estensione;
	}

	function ctrCheckPdf(){
		var estensione= estensioneFile('#fileInput');
		var cbObj = document.datiGenProspettoForm.pdf;
		if(estensione == ""){
			estensione=estensioneFile("${datiGenProspettoForm.nomeFile}");
		}
		if (cbObj.checked){
			if(estensione==""){
				alert("Per procedere allegare prima un file");
				cbObj.checked = false;
			}
			else{
				if(!estensionePdfValida(estensione)){
				alert("Estensione non valida per compositore PDF");
				cbObj.checked = false;
				}
			}
			}
	    }

</script>

<html:form action="/SalvaDatiGenProspetto" method="post" enctype="multipart/form-data" >
	<table class="dettaglio-tab">
	  <tr>
      <td class="etichetta-dato">Tipo report</td>
      <td class="valore-dato">
      	<html:select property="tipoRicerca" styleId="listaTipoRicerca">
	      	<html:options collection="listaTipoRicerca" property="tipoTabellato" labelProperty="descTabellato" />
      	</html:select>
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Titolo (*)</td>
      <td class="valore-dato">
      	<html:text property="nome" styleId="nome" size="50" maxlength="50" />
      </td>
    </tr>
    <tr>
      <td class="etichetta-dato">Descrizione</td>
      <td class="valore-dato"> 
	      <html:text property="descrRicerca" styleId="descrRicerca" size="80" maxlength="200" /> 
      </td>
    </tr>
    
<c:choose>
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
    <tr>
      <td class="etichetta-dato" >Report personale</td>
      <td class="valore-dato"> 
	      <html:checkbox property="personale" />
      </td>
    </tr>
  </c:when>
	<c:otherwise>
	  <html:hidden property="personale" value="1"/>
	</c:otherwise>
</c:choose>
<c:choose>
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#") && fn:contains(listaOpzioniDisponibili, "OP119#")}'>
    <tr>
      <td class="etichetta-dato" >Pubblica il report come servizio con codice</td>
      <td class="valore-dato">
      	<html:text property="codReportWS" styleId="codReportWS" size="30" maxlength="30"  onchange="if (this.value.length>0) this.value=this.value.toUpperCase();"/> 
      </td>
    </tr>
  </c:when>
	<c:otherwise>
	  <html:hidden property="codReportWS"/>
	</c:otherwise>
</c:choose>
 <!-- F.D. se c'è l'ou48 è giusto dare la possibilità di scegliere 
 se invece non c'è l'ou48 vuol dire che se si è qui c'è l'ou49 quindi non si visualizza-->
 <c:choose>
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou48#")}'>
    <tr>
      <td class="etichetta-dato" >Mostra nel menù Report</td>
      <td class="valore-dato"> 
	      <html:checkbox property="ricercaDisponibile" />
      </td>
    </tr>
 </c:when>
	    <c:otherwise>
		    <html:hidden property="ricercaDisponibile" value="1"/>
		</c:otherwise>
	</c:choose>
		<tr>
	      <td class="etichetta-dato" >File <c:if test="${datiGenProspettoForm.idProspetto eq 0}">(*)</c:if></td>
	      <td class="valore-dato"> 
		  <c:if test="${not empty datiGenProspettoForm.idModello}">
				<!-- a href="DettaglioProspetto.do?metodo=downloadModello&nomeFile=${datiGenProspettoForm.nomeFile}&idProspetto=${datiGenProspettoForm.idRicerca}" title="Download modello" --><c:out value="${datiGenProspettoForm.nomeFile}" /><!-- /a -->
		  </c:if>
		    <html:file property="selezioneFile" styleId="fileInput" styleClass="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange="javascript:ctrCheckPdf()"/>
				<% //Campi hidden per l'update %>
 				<html:hidden property="idProspetto"/>
				<html:hidden property="codApp"/>
				<html:hidden property="owner"/>
				<html:hidden property="nomeFile"/>
				<html:hidden property="profiloOwner"/>
	    <c:if test="${datiGenProspettoForm.idProspetto ne 0}">
				<% //Ulteriori campi hidden per l'insert %>
				<html:hidden property="idRicerca"/>
				<html:hidden property="famiglia"/>
				<html:hidden property="idModello"/>
				<html:hidden property="tipoModello"/>
				<html:hidden property="prospetto"/>
			</c:if>
		  </td>
    </tr>
		<tr>

			<td class="etichetta-dato">Componi in formato PDF</td>
			<td class="valore-dato"><html:checkbox property="pdf"
					onclick="javascript:ctrCheckPdf()" /></td>
		</tr>
		<tr>
		<td colspan="2"><b>Sorgente dati</b></td>
  </tr>
	<tr>
  	<td class="etichetta-dato">Sorgente</td>
  	<td class="valore-dato">
  	<c:if test="${datiGenProspettoForm.idProspetto eq 0}">
  		<html:select property="tipoFonteDati" styleId="listaTipoFonte" onchange="initSorgenteDati();">
  			<html:options collection="listaTipoFonte" property="tipoTabellato" labelProperty="descTabellato" />
  		</html:select>
  	</c:if>
  	<c:if test="${datiGenProspettoForm.idProspetto gt 0}">
	      <c:if test="${datiGenProspettoForm.tipoFonteDati == 0}">
	      	Base dati
	      </c:if>
	      <c:if test="${datiGenProspettoForm.tipoFonteDati == 1}">
	      	Report
	      </c:if>
	      <html:hidden property="tipoFonteDati"/>
  	</c:if>
  	</td>
  </tr>
	<tr id="rowReport">
    <td class="etichetta-dato" >Report origine dati (*)</td>
      <td class="valore-dato">
      <html:select property="idRicercaSrc" styleId="listaRicerche">
      		<html:option value="">&nbsp;</html:option>
  			<html:options collection="listaRicerche" property="idRicerca" labelProperty="nomeRicerca" />
  		</html:select>
      </td>
    </tr>
		<tr id="rowSchema">
      <td class="etichetta-dato">Schema principale (*)</td>
      <td class="valore-dato">
        <html:select property="schemaPrinc" onchange="javascript:aggiornaOpzioniSelectTabella(document.datiGenProspettoForm.schemaPrinc.selectedIndex);">
          <html:option value="">&nbsp;</html:option>
          <c:forEach items="${elencoSchemi}" var="schema">
          	<option value="${schema.codice}" <c:if test='${schema.codice eq datiGenProspettoForm.schemaPrinc}'>selected="true"</c:if>>${schema.codice} - ${schema.descrizione}</option>
          </c:forEach>
        </html:select>
      </td>
    </tr>
    <tr id="rowArgomento">
      <td class="etichetta-dato">Argomento principale (*)</td>
      <td class="valore-dato"> 
      	<html:select property="entPrinc">
          <html:option value="">&nbsp;</html:option>
        </html:select>
      </td>
    </tr>
    <tr>
      <td class="comandi-dettaglio" colSpan="2">
		 		<c:choose>
					<c:when test="${datiGenProspettoForm.idProspetto eq 0}">
						<% //Siamo nella situazione di inserimento %>
						<INPUT type="button" class="bottone-azione" value="Salva" title="Salva" onclick="javascript:gestisciSubmit();" />
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();"/>
						<html:hidden property="metodo" value="insertProspetto"/>
					</c:when>
					<c:otherwise>
						<% //Siamo nella situazione di modifica %>
						<INPUT type="button" class="bottone-azione" value="Salva" title="Conferma delle modifiche" onclick="javascript:gestisciSubmit();" />
						<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:annullaModifiche(${datiGenProspettoForm.idRicerca});"/>
						<html:hidden property="metodo" value="updateProspetto"/>
					</c:otherwise>
				</c:choose>
        &nbsp;
      </td>
    </tr>
	</table>
</html:form>
<script type="text/javascript">
<!-- 
<c:if test='${empty datiGenProspettoForm.idModello}'>
	document.getElementById("listaTipoRicerca").selectedIndex=0;
</c:if>

<c:if test="${datiGenProspettoForm.idProspetto ne 0}">
aggiornaOpzioniSelectTabella(document.datiGenProspettoForm.schemaPrinc.selectedIndex);
initComboTabella();
</c:if>
-->
</script>