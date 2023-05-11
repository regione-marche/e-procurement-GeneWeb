<%
/*
 * Created on 04-lug-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */
 
// PAGINA CHE CONTIENE L'ISTANZA DELLA SOTTOPARTE DELLA PAGINA DI DETTAGLIO GRUPPO 
// CONTENENTE I DATI DI UN GRUPPO E LE RELATIVE FUNZIONALITA' IN SOLA VISUALIZZAZIONE
%>

<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<%@ page import="java.util.List,
                it.eldasoft.utils.metadata.domain.Schema,
                it.eldasoft.utils.metadata.domain.Tabella" %>

<script type="text/javascript">   
<%
  /* 
   *Generazione degli array javascript contenenti elenco schemi e 
   * elenco tabelle relative a ciascuno schema.		
   */
   
   List<Schema> elencoSchemi = (List<Schema>) request.getAttribute("elencoSchemi");

		String listaSchemi = "var listaSchemi = new Array (";
		for(int i=0; i < elencoSchemi.size(); i++){
		  if(i>0)
		    listaSchemi += ",\n";
		  listaSchemi += "\"" + elencoSchemi.get(i).getCodice().replaceAll("[\"]","\\\\\"") + "-" + elencoSchemi.get(i).getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";;
		}
		listaSchemi += ");\n";
		
	List<Tabella> elencoTabelle = null;
    Schema schema = null;
    Tabella tabella = null;
    String dichiarazioneListaSelect = null;
    String dichiarazioneValueSelect = null;
    for (int i = 0; i< elencoSchemi.size(); i++) {
      schema = elencoSchemi.get(i);
      dichiarazioneListaSelect = "var " + schema.getCodice() + " = new Array ( '',";
      dichiarazioneValueSelect = "var valoriTabelle" + i + " = new Array ( '',";
      elencoTabelle = (List<Tabella>) request.getAttribute("elencoTabelle" + schema.getCodice());
      for (int j = 0; j < elencoTabelle.size(); j++) {
        tabella = elencoTabelle.get(j);
        if (j>0) {
          dichiarazioneListaSelect += ",\n";
          dichiarazioneValueSelect += ",\n";
        }
        dichiarazioneListaSelect += "\"" + tabella.getCodiceMnemonico() + " - " + tabella.getDescrizione().replaceAll("[\"]","\\\\\"") + "\"";
        dichiarazioneValueSelect += "\"" + tabella.getNomeTabella().replaceAll("[\"]","\\\\\"") + "\"";
      }
      dichiarazioneListaSelect += ");\n";
      dichiarazioneValueSelect += ");\n";
	%>

	<%=dichiarazioneListaSelect%>
	<%=dichiarazioneValueSelect%>
	<%
	    }
%>
	<%=listaSchemi%>
	
<%
   /* Generazione array delle estensioni ammesse per la composizione in PDF */
   String[] listaEstPdf = (String[]) request.getAttribute("listaEstPdf");
   String elemlistaEstPdf = "";
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

	function aggiornaOpzioniSelectTabella(indice) {
		var nomeArrayValue = 'valoriTabelle' + (indice-1);
		var nomeArrayText = document.modelliForm.schemaPrinc.options[indice].value;
		var selectDaAggiornare = document.modelliForm.entPrinc;

		if (indice == 0) {
			selectDaAggiornare.length = 1;
			selectDaAggiornare.options[0].text = "";
			selectDaAggiornare.options[0].value = "";
		} else {
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			selectDaAggiornare.selectedIndex = 0;
		}
	}

	function initComboTabella() {
		var schemaPrinc = "${modelliForm.schemaPrinc}";
		var indice = -1;
		var tmp = "";
		if(schemaPrinc != ""){
			for(var i=0; i < listaSchemi.length; i++){
				if(listaSchemi[i].substring(0, listaSchemi[i].indexOf('-')) == schemaPrinc){
				  indice = i;
				  break;
				}
			}
		}

		if (indice >= 0) {
			document.modelliForm.schemaPrinc.selectedIndex = indice + 1;

			var nomeArrayValue = 'valoriTabelle' + (indice);
			var nomeArrayText = document.modelliForm.schemaPrinc.options[indice+1].value;

			var selectDaAggiornare = document.modelliForm.entPrinc;
			aggiornaOpzioniSelect(nomeArrayValue, nomeArrayText, selectDaAggiornare);
			var arrayValori = eval(nomeArrayValue);
			for(var index=1; index < arrayValori.length; index++){
				if(arrayValori[index] == "<c:out value='${modelliForm.entPrinc}'/>"){
					document.modelliForm.entPrinc.value = arrayValori[index];
					break;
				}
			}
		}
	}
	
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
		var cbObj = document.modelliForm.pdf;
		if(estensione == ""){
			estensione=estensioneFile("${modelliForm.nomeFile}");
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
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#" />

<html:form action="/Modello" method="post" enctype="multipart/form-data" >
	<c:choose>
		<c:when test="${modelliForm.idModello eq 0}">
			<html:hidden property="metodo" value="insertModello"/>
		</c:when>
		<c:otherwise>
			<html:hidden property="metodo" value="updateModello"/>
		</c:otherwise> 
	</c:choose>
	<html:hidden property="idModello"/>
	<html:hidden property="prospetto" value="0"/>
	<html:hidden property="profiloOwner"/>

<table class="dettaglio-tab">
  <tr>
    <td class="etichetta-dato"> Tipo documento (*) </td>
    <td class="valore-dato"> 
			<html:select property="tipoModello">	
				<html:options collection="listaTipoModello" property="tipoTabellato" labelProperty="descTabellato" />
	    </html:select>
	  </td>
  </tr>
  <tr>
    <td class="etichetta-dato" >Nome (*)</td>
    <td class="valore-dato"> 
			<html:text property="nomeModello" styleClass="testo" size="50" maxlength="50"/>
		</td>
  </tr>
	<tr>
    <td class="etichetta-dato" >Descrizione</td>
    <td class="valore-dato"> 
			<html:text property="descrModello" styleClass="testo" size="50" maxlength="200"/>
	  </td>
  </tr>
	<tr>
    <td class="etichetta-dato" >File <c:if test="${modelliForm.idModello eq 0}">(*)</c:if></td>
    <td class="valore-dato"> 
	  <c:if test="${modelliForm.idModello ne 0}">
			<!-- a href="Modello.do?idModello=${idModello}&da=modifica&metodo=downloadModello&nomeFile=${modelliForm.nomeFile}" title="Download modello" --><c:out value="${modelliForm.nomeFile}" /><!-- /a -->
	  </c:if>
	    <html:file styleId="fileInput" property="selezioneFile" styleClass="file" size="50" onkeydown="return bloccaCaratteriDaTastiera(event);" onchange="javascript:ctrCheckPdf()"/>
		<html:hidden property="nomeFile"/>
		<html:hidden property="codiceApplicativo"/>
		<html:hidden property="owner"/>
	  </td>
  </tr>
  	<tr>
  	
      <td class="etichetta-dato" >Componi in formato PDF</td>
      <td class="valore-dato">
      	<html:checkbox property="pdf" onclick="javascript:ctrCheckPdf()"/>
      </td>
    </tr>
	<tr>
      <td class="etichetta-dato" >Modello riepilogativo ?</td>
      <td class="valore-dato">
      	<html:checkbox property="riepilogativo" />
      </td>
    </tr>
<c:choose>
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
  <tr>
    <td class="etichetta-dato" >Modello personale</td>
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
  <c:when test='${fn:contains(listaOpzioniUtenteAbilitate, "ou50#")}'>
	<tr>
    <td class="etichetta-dato" >Mostra in Modelli predisposti</td>
    <td class="valore-dato"> 
      <html:checkbox property="disponibile"  />
    </td>
  </tr>
	</c:when>
	<c:otherwise>
		<html:hidden property="disponibile" value="1"/>
	</c:otherwise>
</c:choose>
	<tr>
  	<td class="etichetta-dato">Schema principale (*)</td>
    <td class="valore-dato">
    	<html:select property="schemaPrinc" onchange="javascript:aggiornaOpzioniSelectTabella(document.modelliForm.schemaPrinc.selectedIndex);">
      	<html:option value=""/>
      <c:forEach items="${elencoSchemi}" var="schema">
       	<option value="${schema.codice}">${schema.codice} - ${schema.descrizione}</option>
      </c:forEach>
      </html:select>
    </td>
  </tr>
  <tr>
    <td class="etichetta-dato">Argomento principale (*)</td>
    <td class="valore-dato"> 
    	<html:select property="entPrinc">
        <html:option value=""/>
      </html:select>&nbsp;<elda:linkPopupAttributo mnemonico="POPUPENTPRINC" contextPath="${pageContext.request.contextPath}" />
      <html:hidden property="filtroEntPrinc" />
    </td>
  </tr>
	<tr>
		<td class="comandi-dettaglio" colSpan="2">
	<c:choose>
		<c:when test="${modelliForm.idModello eq 0}">
			<!-- Siamo nella situazione di inserimento -->
			<INPUT type="button" class="bottone-azione" value="Salva" title="Conferma delle modifiche" onclick="javascript:update()" />
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla" onclick="javascript:annulla();"/>
		</c:when>
		<c:otherwise>
			<!-- Siamo nella situazione di modifica-->				
			<INPUT type="button" class="bottone-azione" value="Salva" title="Conferma delle modifiche" onclick="javascript:update()" />
			<INPUT type="button" class="bottone-azione" value="Annulla" title="Annulla modifiche" onclick="javascript:dettaglioModello('<c:out value='${idModello}' />')"/>
		</c:otherwise> 
	</c:choose>
      &nbsp;
    </td>
  </tr>
</table>
</html:form>