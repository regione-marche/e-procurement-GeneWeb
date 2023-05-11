<%/*
   * Created on 03-05-2013
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
	<c:when test='${not empty param.entitaPartenza}'>
		<c:set var="entitaPartenza" value="${param.entitaPartenza}" />
	</c:when>
	<c:otherwise>
		<c:set var="entitaPartenza" value="${entitaPartenza}" />
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

<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<c:set var="IsG_MODSCADENZPopolata" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.IsG_MODSCADENZPopolataFunction",pageContext,moduloAttivo,entitaPartenza,discriminante)}' />
<c:set var="ricalcolo" value='${gene:callFunction4("it.eldasoft.gene.tags.functions.ListaAttivitaScadenzarioFunction",pageContext,moduloAttivo,entitaPartenza,chiave)}' />


<gene:template file="lista-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_SCADENZ-Lista">
	<gene:setString name="titoloMaschera" value="Lista delle attività"/>
	<gene:redefineInsert name="corpo">

		<c:if test='${not gene:checkProtFunz(pageContext, "INS","LISTANUOVO") or ScadenzarioModificabile ne "1"}' >
			<gene:redefineInsert name="pulsanteListaInserisci" />
			<gene:redefineInsert name="listaNuovo" />
		</c:if>
		<gene:redefineInsert name="listaEliminaSelezione" />
		<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
		<gene:redefineInsert name="listaNuovo">
			<c:if test='${ScadenzarioModificabile eq "1" and gene:checkProtFunz(pageContext,"INS","LISTANUOVO")}'>
			<tr>
				<td class="vocemenulaterale">
					<a href="javascript:listaNuovo();" title="Nuova attività" tabindex="1501">
						Nuova attività</a></td>
			</tr>
			</c:if>
		</gene:redefineInsert>
		
		<gene:set name="titoloMenu">
			<jsp:include page="/WEB-INF/pages/commons/iconeCheckUncheck.jsp" />
		</gene:set>
		
	<c:if test='${not empty chiave}'>
		<c:set var="campiKey" value='${fn:split(chiave,";")}' />
		<c:set var="addKeyRiga" value="" />
		<c:forEach begin="1" end="${fn:length(campiKey)}" step="1" varStatus="indicekey">
			<c:set var="strTmp" value='${fn:substringAfter(campiKey[indicekey.index-1], ":")}' />
			<c:choose>
				<c:when test="${indicekey.last}">
					<c:set var="addKeyRiga" value='${addKeyRiga}G_SCADENZ.KEY${indicekey.index}=T:${strTmp}' />
					<c:set var="whereKey" value='${whereKey} G_SCADENZ.KEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")}' />
				</c:when>
				<c:otherwise>
					<c:set var="addKeyRiga" value='${addKeyRiga}G_SCADENZ.KEY${indicekey.index}=T:${strTmp};' />
					<c:set var="whereKey" value='${whereKey} G_SCADENZ.KEY${indicekey.index}=${gene:concat(gene:concat("\'", strTmp), "\'")} AND ' />
				</c:otherwise>
			</c:choose>
		</c:forEach>

		<c:set var="whereKey" value="${whereKey} AND G_SCADENZ.ENT='${entitaPartenza}' AND G_SCADENZ.PREV=0" />
	</c:if>

		<table class="lista">
			
			<c:if test="${!empty filtroAttivita}">
				<tr>
					<td style="font: 11px Verdana, Arial, Helvetica, sans-serif;">
					 <br><img src="${pageContext.request.contextPath}/img/filtro.gif" alt="Filtro">&nbsp;<span style="color: #ff0028; font-weight: bold;">Lista filtrata</span>
					 <c:if test='${updateLista ne 1}'>
						 &nbsp;&nbsp;&nbsp;[ <a href="javascript:AnnullaFiltro();" ><IMG SRC="${pageContext.request.contextPath}/img/cancellaFiltro.gif" alt="Cancella filtro"></a>
						 <a class="link-generico" href="javascript:AnnullaFiltro();">Cancella filtro</a> ]
					 </c:if>
					</td>
				</tr>
			</c:if>
			
			
			<tr>
				<td>

		<gene:formLista entita="G_SCADENZ" pagesize="50" tableclass="datilista" sortColumn="3" gestisciProtezioni="true"
				where="${whereKey}" >
		<c:if test='${not empty chiave}'>
			<c:set var="key" value="${chiave}" />
			<c:set var="keyParent" value="${chiave}" />
		</c:if>
				
  			<gene:campoLista title="Opzioni<br><center>${titoloMenu}</center>" width="50">
				<c:if test="${currentRow >= 0}" >
					<gene:PopUp variableJs="rigaPopUpMenu${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
						<c:if test='${gene:checkProt(pageContext, "MASC.VIS.GENE.G_SCADENZ-Scheda")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.visualizza" title="Visualizza attività"/>
						</c:if>
						<c:if test='${ScadenzarioModificabile eq "1" and gene:checkProt(pageContext, "MASC.VIS.GENE.G_SCADENZ-Scheda") && gene:checkProt(pageContext, "MASC.MOD.GENE.G_SCADENZ-Scheda") and gene:checkProtFunz(pageContext, "MOD","MOD")}' >
							<gene:PopUpItemResource resource="popupmenu.tags.lista.modifica" title="Modifica attività"/>
						</c:if>
						<c:if test='${ScadenzarioModificabile eq "1" and gene:checkProtFunz(pageContext, "DEL", "LISTADEL")}' >
							<gene:PopUpItem title="Elimina attività" href="eliminaAttivita('${chiaveRigaJava}');"/>
						</c:if>
						<c:if test='${ScadenzarioModificabile eq "1" }' >
							<input type="checkbox" name="keys" value="${chiaveRigaJava}" />
						</c:if>
					</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<input type="hidden" name="keyAdd" value="${addKeyRiga}" />
						
			<c:set var="link" value="javascript:chiaveRiga='${chiaveRigaJava}';listaVisualizza();" />
			<gene:campoLista campo="TIT"  ordinabile="true" href="${link}"/>
			<gene:campoLista title="Data scadenza prevista" campo="DATAFI"  visibile="true"> 
				<c:if test="${datiRiga.G_SCADENZ_TIPOFI == 1}" >
					<span style="float: right; padding-right: 5px;">
						<img width="16" height="16"
							title="Data gestita gestita mediante modifica diretta" 
							alt="Data gestita gestita mediante modifica diretta" 
							src="${pageContext.request.contextPath}/img/write.gif"/>
					</span>
				</c:if>
			</gene:campoLista>
			<gene:campoLista campo="TIPOFI"  visibile="false"/>
			<gene:campoLista title="Data scadenza aggiornata" campo="DATASCAD" ordinabile="true"/>
			<gene:campoLista title="Data scadenza effettiva" campo="DATACONS" ordinabile="true" >
				<c:if test="${datiRiga.G_SCADENZ_TIPOCONS == 1}" >
					<span style="float: right; padding-right: 5px;">
						<img width="16" height="16" 
							title="Data gestita mediante modifica diretta" 
							alt="Data gestita mediante modifica diretta" 
							src="${pageContext.request.contextPath}/img/write.gif"/>
					</span>
				</c:if>
			</gene:campoLista>
			
			<gene:campoLista campo="TIPOCONS" visibile="false"/>
			<gene:campoLista title="Stato" campoFittizio="true" definizione="T10" value="${gene:if(!empty datiRiga.G_SCADENZ_DATACONS,'Completata','Da svolgere') }"/>
			<gene:campoLista campo="DATASCAD"  visibile="false"/>
		</gene:formLista>
				</td>
			</tr>
			<c:if test='${ScadenzarioModificabile ne "1" and gene:checkProtFunz(pageContext, "DEL", "LISTADELSEL")}'>
				<gene:redefineInsert name="pulsanteListaEliminaSelezione" />
				<gene:redefineInsert name="listaEliminaSelezione" />
			</c:if>
			<tr>
				<jsp:include page="/WEB-INF/pages/commons/pulsantiLista.jsp" />
			</tr>
		</table>
		
		<form name="formTimeline" action="${pageContext.request.contextPath}/Timeline.do" method="post">
			<input type="hidden" name="entita" value="" />
			<input type="hidden" name="chiave" value="" />
		</form>	
		
	</gene:redefineInsert>

	<gene:redefineInsert name="addToAzioni">
 		
		<tr>	
		    <td class="vocemenulaterale">
		    	<c:if test='${isNavigazioneDisattiva ne "1" }'>
						<a href="javascript:filtraAttivita();" title="Filtra Attività" tabindex="1502">
				</c:if>
				  Filtra Attività
				<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			</td>
		</tr>

		<c:if test="${datiRiga.rowCount > 0}" >	
			 <c:if test='${ScadenzarioModificabile eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.G_SCADENZ.NotificaPromemoria") }'>
			 	<tr>
			      <td class="vocemenulaterale">
				      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
								<a href="javascript:notificaPromemoria();" title="Notifica promemoria" tabindex="1503">
						</c:if>
						  Notifica promemoria
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				  </td>
				</tr>
			 </c:if>	
			 <c:if test='${ScadenzarioModificabile eq "1" and gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.G_SCADENZ.SalvaComeModello") and fn:contains(listaOpzioniUtenteAbilitate, "ou212#") }'>
				<tr>
			      <td class="vocemenulaterale">
				      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
								<a href="javascript:salvaComeModello();" title="Salva come modello" tabindex="1505">
						</c:if>
						  Salva come modello
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				  </td>
				</tr>
			 </c:if>		
				
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.G_SCADENZ.Calendario") }'>
			    <tr>
			      <td class="vocemenulaterale">
				      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
								<a href="javascript:calendarioSingolo('${param.entitaPartenza}', '${param.chiave}');" title="Calendario" tabindex="1506">
						</c:if>
						  Calendario
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				  </td>
				</tr>
			</c:if>	
			<c:if test='${gene:checkProt(pageContext, "FUNZ.VIS.ALT.GENE.G_SCADENZ.Timeline") }'>
			    <tr>
			      <td class="vocemenulaterale">
				      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
								<a href="javascript:timeline('${param.entitaPartenza}', '${param.chiave}');" title="Sequenza temporale" tabindex="1506">
						</c:if>
						 	Sequenza temporale
						<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
				  </td>
				</tr>
			 </c:if>
		</c:if>	
		<c:if test="${datiRiga.rowCount == 0 && IsG_MODSCADENZPopolata eq 'true'}" >	
			<tr>
		      <td class="vocemenulaterale">
			      	<c:if test='${isNavigazioneDisattiva ne "1" }'>
							<a href="javascript:nuovoDaModello();" title="Nuovo da modello" tabindex="1507">
					</c:if>
					  Nuovo da modello
					<c:if test='${isNavigazioneDisattiva ne "1"}'></a></c:if>			  
			  </td>
			</tr>
		</c:if>		
	
		
	
	</gene:redefineInsert>
	
	<gene:javaScript>
	<c:if test='${not empty chiave}'>
		document.forms[0].keyParent.value="${chiave}";
		//document.forms[0].trovaAddWhere.value="${whereKey}";
	</c:if>
	
	function eliminaAttivita(chiave){
		var valoreChiave = chiave.substring(chiave.indexOf(":")+1);
		var comando = "href=gene/g_scadenz/popup-elimina-attivita.jsp";
		comando += "&id=" + valoreChiave;
		openPopUpCustom(comando, "cancellaAttivita", 500, 250, "yes", "yes");
	}
		
	var discriminante="${discriminante}";
	var ScadenzarioModificabile="${ScadenzarioModificabile }";
	var chiave = "${chiave }";
	var entitaPartenza = "${entitaPartenza }";
	document.forms[0].action = document.forms[0].action + "?discriminante=" + discriminante +"&ScadenzarioModificabile=" + ScadenzarioModificabile+"&chiave=" + chiave+ "&entitaPartenza="+entitaPartenza ;
		
	function nuovoDaModello(){
		var discriminante="${discriminante}";
		var entitaPartenza ="${entitaPartenza }";
		var ScadenzarioModificabile = "${ScadenzarioModificabile }";
		var keyAdd= getValue("keyAdd");
		var chiave = "${chiave }";
		var comando = "href=gene/g_scadenz/lista-modelliAttivita.jsp&discriminante=" + discriminante;
		comando += "&entitaPartenza=" + entitaPartenza + "&ScadenzarioModificabile?" + ScadenzarioModificabile;
		comando += "&keyAdd=" + keyAdd + "&chiave=" + chiave;
		openPopUpCustom(comando, "nuovoDaModello", 720, 350, "yes", "yes");
	}
	
	function calendarioSingolo(entita, chiave) {
		bloccaRichiesteServer();
		document.location.href="${pageContext.request.contextPath}/CalScadenzario.do?"+csrfToken+"&entita=" + entita + "&chiave=" + chiave;
	}
	
	function cronoprogramma(entita, chiave) {
		bloccaRichiesteServer();
		document.location.href="${pageContext.request.contextPath}/Cronoprogramma.do?"+csrfToken+"&entita=" + entita + "&chiave=" + chiave;
	}

	function timeline(entita, chiave) {
		document.formTimeline.entita.value=entita;
		document.formTimeline.chiave.value=chiave;
		bloccaRichiesteServer();
		document.formTimeline.submit();
	}
	
	function notificaPromemoria(){
		var objArrayCheckBox = document.forms[0].keys;
		var arrayLen = "" + objArrayCheckBox.length;
        var numeroSelezionati=0;
        var chiave="";
        var valoriChiavi="";
        if (arrayLen != 'undefined') {
          for (i = 0; i < objArrayCheckBox.length; i++) {
            if (objArrayCheckBox[i].checked){
              numeroSelezionati++;
              chiave=objArrayCheckBox[i].value;
              valoriChiavi += chiave.substring(chiave.indexOf(":")+1) + ";";
            }
          }
        } else {
          if (objArrayCheckBox.checked) {
          	numeroSelezionati++;
          	chiave=objArrayCheckBox.value;
          	valoriChiavi = chiave.substring(chiave.indexOf(":")+1);
          }
	    }
	    
	    if (numeroSelezionati == 0) {
	      alert("Nessun elemento selezionato nella lista");
	  	} else {
	  		if(valoriChiavi.charAt(valoriChiavi.length-1)==';')
	  			valoriChiavi = valoriChiavi.substr(0,valoriChiavi.length-1);
	  		var comando = "href=gene/g_scadenz/popup-notifica-promemoria.jsp";
			comando += "&id=" + valoriChiavi;
			var entitaPartenza="${entitaPartenza }";
			var discriminante = "${discriminante }";
			comando += "&entitaPartenza=" + entitaPartenza + "&discriminante=" + discriminante;
			openPopUpCustom(comando, "notificaPromemoria", 800, 350, "yes", "yes");
				
	  	}
			
	}
	
	function salvaComeModello(){
		var entitaPartenza = "${entitaPartenza }";
	    var discriminante = "${discriminante }";
	    var chiave = "${chiave }";
	    var comando = "href=gene/g_scadenz/popup-salva-modello.jsp";
		comando += "&ent=" + entitaPartenza + "&discriminante=" + discriminante + "&chiave=" + chiave;
		openPopUpCustom(comando, "salvaComeModello", 720, 550, "yes", "yes");
			
	}
	
	function filtraAttivita(){
		var comando = "href=gene/g_scadenz/popup-trova-filtroAttivita.jsp";
		var risultatiPerPagina = document.forms[0].risultatiPerPagina.value;
		comando+="&attivitaPerPagina=" + risultatiPerPagina;
		openPopUpCustom(comando, "impostaFiltro", 850, 300, "yes", "yes");
	}
	
	function AnnullaFiltro(){
	 	var comando = "href=gene/g_scadenz/popup-filtro.jsp&annulla=1";
		openPopUpCustom(comando, "impostaFiltro", 10, 10, "no", "no");
	 }
	
	function salvaPrevisione(){
		var entitaPartenza = "${entitaPartenza }";
	    var chiave = "${chiave }";
	    var comando = "href=gene/g_scadenz/popup-salva-previsione.jsp";
		comando += "&entitaPartenza=" + entitaPartenza + "&chiave=" + chiave;
		openPopUpCustom(comando, "salvaPrevisione", 600, 350, "yes", "yes");
	}
	</gene:javaScript>
</gene:template>