<%/*
       * Created on 05-dic-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA PAGINA DI LISTA
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://displaytag.sf.net" prefix="display" %>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:if test="${requestScope.kronos eq 1}">
<c:set var="labelDataInizioPeriodo" value="Periodo a partire da"/>
<c:set var="labelDataFinePeriodo" value="Periodo fino a"/>
<c:set var="labelRaggruppamento" value="Raggruppamento"/>
<c:set var="labelGruppo" value="Gruppo"/>
<c:set var="labelGiustificativo" value="Giustificativo"/>
<c:set var="labelEscludi" value="Escludi"/>
<c:set var="labelOpzioniReport" value="Opzioni report"/>
<c:set var="labelChkEscludiSabati" value="Sabati"/>
<c:set var="labelChkEscludiDomeniche" value="Domeniche"/>
<c:set var="labelChkEscludiFeste" value="Feste"/>
<c:set var="labelChkOpzDettaglioDipendente" value="Dettaglio dipendente"/>
<c:set var="labelChkOpzTotaliGenerali" value="Totali generali"/>
<c:set var="labelChkOpzTotaliDipendente" value="Totali per dipendente"/>
<c:set var="labelChkOpzRaggruppaGiustificativo" value="Raggruppa giustificativo"/>
<c:set var="labelChkOpzDescrizioneTurno" value="Descrizione turno"/>
<c:set var="labelChkOpzConteggioMesi" value="Conteggio a mesi"/>
<c:set var="labelChkOpzSoloGiustificativoNote" value="Solo giustif. con note"/>
<c:set var="labelChkOpzMostraNote" value="Mostra note"/>

<c:set var="dataInizioPeriodo" value="KRDATINPER"/>
<c:set var="dataFinePeriodo" value="KRDATFINPER"/>
<c:set var="raggruppamento" value="KRRAGGR"/>
<c:set var="gruppo" value="KRGRUP"/>
<c:set var="giustificativo" value="KRGIUST"/>
<c:set var="escludiSabati" value="KRESCLSAB"/>
<c:set var="escludiDomeniche" value="KRESCLDOM"/>
<c:set var="escludiFeste" value="KRESCLFESTE"/>
<c:set var="opzDettaglioDipendente" value="KROPDETTDIP"/>
<c:set var="opzTotaliGenerali" value="KROPTOTGEN"/>
<c:set var="opzTotaliDipendente" value="KROPTOTDIP"/>
<c:set var="opzRaggruppaGiustificativo" value="KROPRAGGRGIUST"/>
<c:set var="opzDescrizioneTurno" value="KROPDESCTURNO"/>
<c:set var="opzConteggioMesi" value="KROPCONTMESI"/>
<c:set var="opzSoloGiustificativoNote" value="KROPSLGIUSTNOT"/>
<c:set var="opzMostraNote" value="KROPMOSTRANOTE"/>
</c:if>


<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />
<script type="text/javascript" src="${contextPath}/js/controlliFormali.js"></script>

<script type="text/javascript"
  src="${contextPath}/js/forms.js"></script>
  
<script type="text/javascript">
<!--
$(function() {
	$( "input.data" ).datepicker($.datepicker.regional[ "it" ]);
});

<c:set var="array" value='var elencoPromptParametri = new Array ('/>
<c:forEach items="${listaParametri}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} "${parametro.nome}"'/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} "${parametro.nome}",'/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />
<c:remove var="array" />

<c:set var="array" value='var elencoFormatoParametri = new Array ('/>
<c:forEach items="${listaParametri}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} "${fn:trim(parametro.tipo)}"'/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} "${fn:trim(parametro.tipo)}",'/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />
<c:remove var="array" />

<c:set var="array" value='var elencoParametriObbl = new Array ('/>
<c:forEach items="${listaParametri}" var="parametro" varStatus="status">
	<c:choose>
		<c:when test="${status.last}" >
			<c:set var="array" value='${array} "${fn:trim(parametro.obbligatorio)}"'/>
		</c:when>
		<c:otherwise>
			<c:set var="array" value='${array} "${fn:trim(parametro.obbligatorio)}",'/>
		</c:otherwise>
	</c:choose>
</c:forEach>
<c:set var="array" value="${array} );" />
<c:out value="${array}" escapeXml="false" />
<c:remove var="array" />

	function svuotaInput() {
		if (elencoFormatoParametri.length == 1 && elencoFormatoParametri[0] != "U") {
			document.parametriModelloForm.parametriModello.value="";
		} else {
			for (var i = 0; i < elencoFormatoParametri.length; i++)
				if (elencoFormatoParametri[i] != "U") 
					document.parametriModelloForm.parametriModello[i].value="";
		}
		<c:if test="${requestScope.kronos eq 1}">
		document.parametriModelloForm.${dataInizioPeriodo}.value="";
		document.parametriModelloForm.${dataFinePeriodo}.value="";
		document.parametriModelloForm.${raggruppamento}.value="";
		document.parametriModelloForm.${gruppo}.value="";
		document.parametriModelloForm.${giustificativo}.value="";
		document.parametriModelloForm.${escludiSabati}.checked=false;
		document.parametriModelloForm.${escludiDomeniche}.checked=false;
		document.parametriModelloForm.${escludiFeste}.checked=false;
		document.parametriModelloForm.${opzDettaglioDipendente}.checked=false;
		document.parametriModelloForm.${opzTotaliGenerali}.checked=false;
		document.parametriModelloForm.${opzTotaliDipendente}.checked=false;
		document.parametriModelloForm.${opzRaggruppaGiustificativo}.checked=false;
		document.getElementById('sp${opzRaggruppaGiustificativo}').style.display="none";
		document.parametriModelloForm.${opzDescrizioneTurno}.checked=false;
		document.getElementById('sp${opzDescrizioneTurno}').style.display="none";
		//document.parametriModelloForm.${opzConteggioMesi}.checked=false;
		document.parametriModelloForm.${opzSoloGiustificativoNote}.checked=false;
		document.parametriModelloForm.${opzMostraNote}.checked=false;
		</c:if>
	}

	function gestisciSubmit(){
		var esitoObblig = true;
		var esitoFormato = true;

		<c:if test="${requestScope.kronos eq 1}">
		// controllo data inizio periodo
		if(esitoObblig && esitoFormato){
			esitoObblig = controllaCampoInputObbligatorio(document.parametriModelloForm.${dataInizioPeriodo}, "${labelDataInizioPeriodo}");
			if(esitoObblig)
				esitoFormato = controllaInputData(document.parametriModelloForm.${dataInizioPeriodo});
			if(!esitoObblig || !esitoFormato)
				document.parametriModelloForm.${dataInizioPeriodo}.select();
		}
		// controllo data fine periodo
		if(esitoObblig && esitoFormato){
			esitoObblig = controllaCampoInputObbligatorio(document.parametriModelloForm.${dataFinePeriodo}, "${labelDataFinePeriodo}");
			if(esitoObblig)
				esitoFormato = controllaInputData(document.parametriModelloForm.${dataFinePeriodo});
			if(!esitoObblig || !esitoFormato)
				document.parametriModelloForm.${dataFinePeriodo}.select();
		}
		</c:if>
		if(esitoObblig && esitoFormato) {
		var numParametri = elencoParametriObbl.length;
		if(numParametri > 1){
			for(var i=0; i < numParametri && esitoObblig && esitoFormato; i++){
				if(elencoParametriObbl[i] == "1"){
					esitoObblig = controllaCampoInputObbligatorio(document.parametriModelloForm.parametriModello[i], elencoPromptParametri[i]);
					if(esitoObblig)
						esitoFormato = controllaParametro(i);
					if(!esitoObblig || !esitoFormato)
						document.parametriModelloForm.parametriModello[i].select();
				} else {
					esitoObblig = true;
					if(document.parametriModelloForm.parametriModello[i].value != "")
						esitoFormato = controllaParametro(i);
					else
						esitoFormato = true;
					if(!esitoObblig || !esitoFormato){
						document.parametriModelloForm.parametriModello[i].select();
					}
				}
			}
		} else if(numParametri == 1) {
			if(elencoParametriObbl[0] == "1"){
				esitoObblig = controllaCampoInputObbligatorio(document.parametriModelloForm.parametriModello, elencoPromptParametri[0]);
				if(esitoObblig)
					esitoFormato = controllaParametro(0);
				if(!esitoObblig || !esitoFormato)
					document.parametriModelloForm.parametriModello.select();
			}
		}
		}
		
    if(esitoObblig && esitoFormato){
 	  	bloccaRichiesteServer();
		  document.parametriModelloForm.submit();
		}
	}
	
	function controllaParametro(indice){

		var numParametri = elencoParametriObbl.length;
		var campoDiInput = null;
		if(numParametri > 1) 
			campoDiInput = document.parametriModelloForm.parametriModello[indice];
		else
			campoDiInput = document.parametriModelloForm.parametriModello;
		var esito1 = null;
		
		if(elencoFormatoParametri[indice] == "M")
			esito1 = true;
		else if(elencoFormatoParametri[indice] == "D")
  			esito1 = controllaInputData(campoDiInput);
  		else if(elencoFormatoParametri[indice] == 'I')
 		  	esito1 = controllaNumeroIntero(campoDiInput);
 	  	else if(elencoFormatoParametri[indice] == 'F')
 		  	esito1 = controllaNumeroFloating(campoDiInput);
		else if(elencoFormatoParametri[indice] == 'U')
 		  	esito1 = true;
 		else if(elencoFormatoParametri[indice] == 'S')
	 		esito1 = true;
	 	else if(elencoFormatoParametri[indice] == 'N')
	 		esito1 = true;
	 	else if(elencoFormatoParametri[indice] == 'T')
	 		esito1 = true;
	 		
  		return esito1;
	}
	
	function controllaNumeroIntero(unCampoDiInput){
		var esitoInteger = isIntero(unCampoDiInput.value);
	  	if(!esitoInteger){
	  	  alert('Formato del parametro errato: inserire un numero intero');
		  }
		return esitoInteger;
	}
	
	function controllaNumeroFloating(unCampoDiInput){
	 	var esitoFloating = isFloating(unCampoDiInput.value)
		if(!esitoFloating){
	    	alert('Formato del parametro errato: inserire un numero decimale');
		}
		return esitoFloating;
	}

	function controllaInputData(unCampoDiInput){
	  return isData(unCampoDiInput)
	}

	function listaModelli() {
		document.parametriModelloForm.metodo.value = "listaModelli";
		document.parametriModelloForm.submit();
	}
	
	<c:if test="${requestScope.kronos eq 1}">
	function showCheck(oggettoSpan, oggettoCheck, visibile) {
		if (visibile) {
			oggettoSpan.style.display="";
		} else {
			oggettoSpan.style.display="none";
			oggettoCheck.checked=false;
		}
	}
	
	// function utilizzata per il set del conteggio a mesi
	function setDipendenze(selezionato) {
		if (selezionato) {
			alert("In questa modalità verranno esclusi dalla lettura\ni giustificativi a ore.");
			document.parametriModelloForm.${opzDettaglioDipendente}.checked=true;
			document.parametriModelloForm.${opzRaggruppaGiustificativo}.checked=true;
			document.getElementById('sp${opzDettaglioDipendente}').style.display="none";
			document.getElementById('sp${opzRaggruppaGiustificativo}').style.display="none";
		} else {
			document.getElementById('sp${opzDettaglioDipendente}').style.display="";
			document.getElementById('sp${opzRaggruppaGiustificativo}').style.display="";
		}
	}
	</c:if>

	function init() {
	<c:if test="${requestScope.kronos eq 1}">
		// imposta la visibilità di un campo in funzione del valore di un altro campo
		showCheck(document.getElementById('sp${opzDescrizioneTurno}'), document.parametriModelloForm.${opzDescrizioneTurno}, ${requestScope[opzTotaliDipendente] eq 1});
		// imposta la visibilità di 2 campi in base al check su conteggio a mesi
		//<c_choose>
		//<c_when test='${requestScope[opzConteggioMesi] eq 1}'>
		//document.getElementById('sp${opzDettaglioDipendente}').style.display="none";
		//document.getElementById('sp${opzRaggruppaGiustificativo}').style.display="none";
		//</c_when>
		//<c_otherwise>
		document.getElementById('sp${opzDettaglioDipendente}').style.display="";
		showCheck(document.getElementById('sp${opzRaggruppaGiustificativo}'), document.parametriModelloForm.${opzRaggruppaGiustificativo}, ${requestScope[opzDettaglioDipendente] eq 1});
		//</c_otherwise>
		//</c_choose>
	</c:if>
	}
-->
</script>
</HEAD>

<BODY onload="javascript:init();">

<!-- parte per la selezione del modello -->
<TABLE class="arealayout" >
  <TR>
    <TD class="arealavoro" >
          <div class="titolomaschera">Parametri per la composizione</div>

          <div class="contenitore-errori-arealavoro">
				<jsp:include page="/WEB-INF/pages/commons/serverMsg.jsp" />
		  </div>

      <div class="contenitore-popup">

<html:form action="/SalvaParametriModello" >
	<table class="dettaglio-notab">	

	<c:if test='${requestScope.kronos eq 1}'>
		<tr>
			<td class="etichetta-dato">${labelDataInizioPeriodo} (*)
			</td>
			<td class="valore-dato">
					<input type="text" name="${dataInizioPeriodo}" id="${dataInizioPeriodo}" value="${requestScope[dataInizioPeriodo]}" class="data">
					&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
			</td>
		</tr>
		<tr>
			<td class="etichetta-dato">${labelDataFinePeriodo} (*)
			</td>
			<td class="valore-dato">
					<input type="text" name="${dataFinePeriodo}" id="${dataFinePeriodo}" value="${requestScope[dataFinePeriodo]}" class="data">
					&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
			</td>
		</tr>


		<tr>
			<td class="etichetta-dato">${labelRaggruppamento} (**)
			</td>
			<td class="valore-dato">
					<select name="${raggruppamento}" onchange="javascript:document.parametriModelloForm.${gruppo}.selectedIndex = 0;document.parametriModelloForm.${giustificativo}.selectedIndex = 0;">
						<option value="">&nbsp;</option>
						<c:forEach items="${requestScope.listaRaggruppamenti}" var="opzione" varStatus="j">
						<option value="${opzione.tipoTabellato}" <c:if test="${requestScope[raggruppamento] eq opzione.tipoTabellato}">selected="selected"</c:if>>${opzione.descTabellato}</option>
						</c:forEach>
					</select>
			</td>
		</tr>
		<tr>
			<td class="etichetta-dato">${labelGruppo} (**)
			</td>
			<td class="valore-dato">
					<select name="${gruppo}" onchange="javascript:document.parametriModelloForm.${raggruppamento}.selectedIndex = 0; document.parametriModelloForm.${giustificativo}.selectedIndex = 0;">
						<option value="">&nbsp;</option>
						<c:forEach items="${requestScope.listaGruppi}" var="opzione" varStatus="j">
						<option value="${opzione.tipoTabellato}" <c:if test="${requestScope[gruppo] eq opzione.tipoTabellato}">selected="selected"</c:if>>${opzione.descTabellato}</option>
						</c:forEach>
					</select>
			</td>
		</tr>
		<tr>
			<td class="etichetta-dato">${labelGiustificativo} (**)
			</td>
			<td class="valore-dato">
					<select name="${giustificativo}" onchange="javascript:document.parametriModelloForm.${raggruppamento}.selectedIndex = 0; document.parametriModelloForm.${gruppo}.selectedIndex = 0;">
						<option value="">&nbsp;</option>
						<c:forEach items="${requestScope.listaGiustificativi}" var="opzione" varStatus="j">
						<option value="${opzione.tipoTabellato}" <c:if test="${requestScope[giustificativo] eq opzione.tipoTabellato}">selected="selected"</c:if>>${opzione.descTabellato}</option>
						</c:forEach>
					</select>
			</td>
		</tr>
		<tr>
			<td class="etichetta-dato">${labelEscludi}
			</td>
			<td class="valore-dato">
					<input type="checkbox" name="${escludiSabati}" value="1" <c:if test="${requestScope[escludiSabati] eq 1}">checked="checked"</c:if>/>${labelChkEscludiSabati}&nbsp;&nbsp;
					<input type="checkbox" name="${escludiDomeniche}" value="1" <c:if test="${requestScope[escludiDomeniche] eq 1}">checked="checked"</c:if>/>${labelChkEscludiDomeniche}&nbsp;&nbsp;
					<input type="checkbox" name="${escludiFeste}" value="1" <c:if test="${requestScope[escludiFeste] eq 1}">checked="checked"</c:if>/>${labelChkEscludiFeste}
			</td>
		</tr>

		<tr>
			<td class="etichetta-dato">${labelOpzioniReport}
			</td>
			<td class="valore-dato">
	      	<table class="dettaglio-noBorderBottom">
	      		<tr>
	      			<td>
	      			<span id="sp${opzDettaglioDipendente}">
								<input type="checkbox" name="${opzDettaglioDipendente}" value="1" <c:if test="${requestScope[opzDettaglioDipendente] eq 1}">checked="checked"</c:if> onclick="javascript:showCheck(document.getElementById('sp${opzRaggruppaGiustificativo}'), document.parametriModelloForm.${opzRaggruppaGiustificativo}, this.checked);" />${labelChkOpzDettaglioDipendente}
							</span>
	      			</td>
	      			<td>
								<input type="checkbox" name="${opzTotaliGenerali}" value="1" <c:if test="${requestScope[opzTotaliGenerali] eq 1}">checked="checked"</c:if>/>${labelChkOpzTotaliGenerali}
	      			</td>
	      		</tr>	      		
	      		<tr>
	      			<td>
								<input type="checkbox" name="${opzTotaliDipendente}" value="1" <c:if test="${requestScope[opzTotaliDipendente] eq 1}">checked="checked"</c:if> onclick="javascript:showCheck(document.getElementById('sp${opzDescrizioneTurno}'), document.parametriModelloForm.${opzDescrizioneTurno}, this.checked);"/>${labelChkOpzTotaliDipendente}
	      			</td>
	      			<td>
	      			<span id="sp${opzRaggruppaGiustificativo}">
								<input type="checkbox" name="${opzRaggruppaGiustificativo}" value="1" <c:if test="${requestScope[opzRaggruppaGiustificativo] eq 1}">checked="checked"</c:if>/>${labelChkOpzRaggruppaGiustificativo}
							</span>
	      			</td>
	      		</tr>	      		
	      		<tr>
	      			<td>
	      			<span id="sp${opzDescrizioneTurno}">
								<input type="checkbox" name="${opzDescrizioneTurno}" value="1" <c:if test="${requestScope[opzDescrizioneTurno] eq 1}">checked="checked"</c:if>/>${labelChkOpzDescrizioneTurno}
							</span>
	      			</td>
	      			<td>&nbsp;
								<!-- input type="checkbox" name="${opzConteggioMesi}" value="1" <c:if test="${requestScope[opzConteggioMesi] eq 1}">checked="checked"</c:if> onclick="javascript:setDipendenze(this.checked);"/--><!--${labelChkOpzConteggioMesi}-->
	      			</td>
	      		</tr>	      		
	      		<tr>
	      			<td>
								<input type="checkbox" name="${opzSoloGiustificativoNote}" value="1" <c:if test="${requestScope[opzSoloGiustificativoNote] eq 1}">checked="checked"</c:if>/>${labelChkOpzSoloGiustificativoNote}
	      			</td>
	      			<td>
								<input type="checkbox" name="${opzMostraNote}" value="1" <c:if test="${requestScope[opzMostraNote] eq 1}">checked="checked"</c:if>/>${labelChkOpzMostraNote}
	      			</td>
	      		</tr>	      		
	      	</table>
			</td>
		</tr>

	</c:if>

	<c:set var="indice" value="0" />
	<c:forEach items="${listaParametri}" var="parametro" varStatus="ciclo">
		<c:set var="tipoPar" value="${fn:trim(parametro.tipo)}" />
	  <c:if test='${tipoPar ne "U"}'>
	  <tr>
      <td class="etichetta-dato">
      	${fn:trim(parametro.nome)}<c:if test="${fn:trim(parametro.obbligatorio eq 1)}"> (*)</c:if>
      </td>
      <td class="valore-dato">
				
				<c:if test='${tipoPar eq "D"}'>
					<c:choose>
						<c:when test='${fn:length(listaParametri) gt 1}' >
							<c:set var="nomeArrayPar" value="parametriModello[${ciclo.index}]" />
						</c:when>	
						<c:otherwise>
							<c:set var="nomeArrayPar" value="parametriModello" />
						</c:otherwise>
					</c:choose>
					<input type="text" name="parametriModello" value="${listaValori[ciclo.index]}" class="data">
					&nbsp;<span class="formatoParametro">&nbsp;(GG/MM/AAAA)</span>
				</c:if>
				
				<c:if test='${tipoPar eq "M"}'>
					<select name="parametriModello" >
						<option value="">&nbsp;</option>
						<c:set var="arrayCodVal" value="${fn:split(parametro.menu, '|')}" />
						<c:forEach items="${arrayCodVal}" var="opzione" varStatus="j">
							<option value="${(j.index+1)}" <c:if test="${listaValori[ciclo.index] eq (j.index+1)}">selected="selected"</c:if>>${opzione}</option>
						</c:forEach>
					</select>
				</c:if>
				
				<c:if test='${tipoPar eq "I"}'>
					<input type="text" name="parametriModello" value="${listaValori[ciclo.index]}"><span class="formatoParametro">&nbsp;(NNN)</span>
				</c:if>
				
				<c:if test='${tipoPar eq "F"}'>
					<input type="text" name="parametriModello" value="${listaValori[ciclo.index]}"><span class="formatoParametro">&nbsp;(NNN,NN)</span>
				</c:if>
				
				<c:if test='${tipoPar eq "S"}'>
					<input type="text" name="parametriModello" maxlength="512" size="70" value="${listaValori[ciclo.index]}">
				</c:if>
				
				<c:if test='${tipoPar eq "N"}'>
					<textarea name="parametriModello" rows="8" cols="64">${listaValori[ciclo.index]}</textarea>
				</c:if>
				
				<c:if test='${tipoPar eq "T"}'>
					<html:select property="parametriModello">
						<option value="">&nbsp;</option>
						<c:set var="listatab" value="lista${parametro.tabellato}"/>
						<c:forEach items="${requestScope[listatab]}" var="opzione" varStatus="j">
						<option value="${opzione.tipoTabellato}" <c:if test="${listaValori[ciclo.index] eq opzione.tipoTabellato}">selected="selected"</c:if>>${opzione.descTabellato}</option>
						</c:forEach>
					</html:select>
				</c:if>
				<c:if test="${!empty parametro.descrizione}">
					<div class="note">
						<c:out value="${parametro.descrizione}" escapeXml="false" />
					</div>
				</c:if>

			</td>
		</tr>
		</c:if>

		<c:if test='${tipoPar eq "U" && !empty idAccount}'>
				<input type="hidden" name="parametriModello" value="${idAccount}">
		</c:if>

	</c:forEach>
	
		<tr>
		    <td class="comandi-dettaglio" colSpan="2">
		      <INPUT type="button" class="bottone-azione" value="Componi" title="Componi" onclick="javascript:gestisciSubmit();" >
		      <INPUT type="button" class="bottone-azione" value="Reimposta" title="Reimposta" onclick="javascript:svuotaInput();" >
		      <INPUT type="button" class="bottone-azione" value="Torna a elenco modelli" title="Torna a elenco modelli" onclick="javascript:listaModelli();" >
		      &nbsp;
		    </td>
		</tr>
	</table>

	<input type="hidden" name="metodo" value="salvaEComponiModello"/> 
	<html:hidden name="componiModelloForm" property="idModello"/>
	<html:hidden name="componiModelloForm" property="tipo"/>
	<html:hidden name="componiModelloForm" property="nomeModello"/> 
	<html:hidden name="componiModelloForm" property="entita"/> 
	<html:hidden name="componiModelloForm" property="nomeChiavi"/> 
	<html:hidden name="componiModelloForm" property="valori"/>
	<c:forEach items="${componiModelloForm.valChiavi}" var="chiave">
		<input type="hidden" name="valChiavi" value="${chiave}"/>
	</c:forEach>
	<html:hidden name="componiModelloForm" property="noFiltroEntitaPrincipale"/>
	<html:hidden name="componiModelloForm" property="fileComposto"/>
	<html:hidden name="componiModelloForm" property="paginaSorgente"/>
	<html:hidden name="componiModelloForm" property="riepilogativo"/>
</html:form>
        </div>
      </TD>
    </TR>
  </TABLE>
  
<script type="text/javascript">
<!--
	$('textarea[name="parametriModello"]').on('input propertychange', function() {checkInputLength( $(this)[0], 512)});
-->
</script>  
</BODY>
</HTML>
