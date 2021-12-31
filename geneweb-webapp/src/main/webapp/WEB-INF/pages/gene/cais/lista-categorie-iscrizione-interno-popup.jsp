<%
	/*
   * Created on: 15:30 19/03/2008
   *
   * Copyright (c) EldaSoft S.p.A.
   * Tutti i diritti sono riservati.
   *
   * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
   * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
   * aver prima formalizzato un accordo specifico con EldaSoft.
   */
   
   /*
		Descrizione: Lista delle categorie degli appalti
		Creato da:   Luca Giacomazzo
	  */
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<c:set var="indiceRiga" value="-1"/>
<c:set var="numCambi" value="0"/>

<c:set var="esisteCategorieLavori150" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.EsisteCategoriaPerTipoAppaltoFunction", pageContext, "4")}' />
<c:set var="esisteCategorieServiziProfessionali" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.EsisteCategoriaPerTipoAppaltoFunction", pageContext, "5")}' />
<c:set var="tmp" value='${trovaAddWhere}' /> 
<c:choose>
	<c:when test='${(!empty tmp) and fn:contains(tmp, "V_CAIS_TIT.TIPLAVG=") and (! fn:contains(tmp, "null"))}' >
		<c:set var="tipoCategoria" value="${fn:substring(tmp, fn:indexOf(tmp, 'V_CAIS_TIT.TIPLAVG=')+19, fn:indexOf(tmp, 'V_CAIS_TIT.TIPLAVG=')+19+1)}" />
	</c:when>
	<c:otherwise>
		<c:set var="tipoCategoria" value="1" />
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test='${tipoCategoria eq 1}' >
		<c:set var="filtroLista" value="CAISIM <> 'OG' and CAISIM <> 'OS' " />
	</c:when>
	<c:when test='${tipoCategoria eq 2}' >
		<c:set var="filtroLista" value="1 = 1" />
	</c:when>
	<c:otherwise>
		<c:set var="filtroLista" value="1 = 1" />
	</c:otherwise>
</c:choose>
<c:set var="filtroLista" value="${filtroLista} and (ISARCHI is null or ISARCHI<>'1')" />


<c:if test='${(!empty param.archValueChiave)}'>
	<c:set var="parametro" value="%${param.archValueChiave}%" />
	<c:set var="filtroUlteriore" value='${gene:callFunction2("it.eldasoft.gene.tags.functions.GetUlterioreFiltroCategorieFunction", pageContext, parametro)}' />
	<c:set var="filtroLista" value="${filtroLista} ${filtroUlteriore }" />
</c:if>

	<gene:setString name="titoloMaschera" value="Selezione della categoria d'iscrizione"/>
	<gene:redefineInsert name="corpo">
	
		<gene:formLista pagesize="25" tableclass="datilista" entita="V_CAIS_TIT" inserisciDaArchivio="false" where="${filtroLista}" sortColumn="12" >
			
			<table class="dettaglio-noBorderBottom">
				<tr>
					<td colspan="2">
						Categorie per:
						<span id="spanLavori">
							<c:choose>
								<c:when test="${esisteCategorieLavori150 eq 'true'}">
									<input type="radio" value="1" id="radioLavori" name="filtroCategoria" <c:if test='${tipoCategoria eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(1);" />${descTipoCategoria[0].descTabellato}
									&nbsp;
									<span id="spanLavori150">
										<input type="radio" value="4" id="radioLavori150" name="filtroCategoria" <c:if test='${tipoCategoria eq 4}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(4);"/>${descTipoCategoria[1].descTabellato}
									</span>
								</c:when>
								<c:otherwise>
									<input type="radio" value="1" id="radioLavori" name="filtroCategoria" <c:if test='${tipoCategoria eq 1}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(1);" />${descTipoCategoria[0].descTabellato}
								</c:otherwise>
							</c:choose>
							&nbsp;
						</span>
						<span id="spanForniture">
							<input type="radio" value="2" id="radioForniture" name="filtroCategoria" <c:if test='${tipoCategoria eq 2}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(2);"/>${descTipoCategoria[2].descTabellato}
							&nbsp;
						</span>
						
						<span id="spanServizi">
							<c:choose>
								<c:when test="${esisteCategorieServiziProfessionali eq 'true'}">
									<input type="radio" value="3" id="radioServizi" name="filtroCategoria" <c:if test='${tipoCategoria eq 3}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(3);"/>${descTipoCategoria[3].descTabellato}
									&nbsp;
									<span id="spanServiziProfessionali">
										<input type="radio" value="5" id="radioServiziProfessionali" name="filtroCategoria" <c:if test='${tipoCategoria eq 5}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(5);"/>${descTipoCategoria[4].descTabellato}
									</span>
								</c:when>
								<c:otherwise>
									<input type="radio" value="3" id="radioServizi" name="filtroCategoria" <c:if test='${tipoCategoria eq 3}'>checked="checked"</c:if> onclick="javascript:cambiaCategoria(3);"/>${descTipoCategoria[3].descTabellato}
								</c:otherwise>
							</c:choose>
						</span>
						
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr><td colspan="2">
					Filtra 
					<input type="text" id="filtroUlteriore" name="filtroUlteriore" value="${ fn:toLowerCase(param.archValueChiave)}" style="font: 11px Verdana, Arial, Helvetica, sans-serif;" />
					<INPUT type="button"  value='Applica' title='Applica' onclick='javascript:cambiaCategoria(-100);' style="font: 11px Verdana, Arial, Helvetica, sans-serif;"/>
					</td></tr>
				<tr><td>&nbsp;</td></tr>
			</table>		
			
			
			
			
			<c:set var="oldData" value="${newData}"/>
			<c:set var="newData" value="${datiRiga.V_CAIS_TIT_TITCAT }"/>
						
			<gene:campoLista campoFittizio="true" visibile="false">
				<%/* Nel caso in cui siano diversi inframezzo il titolo */%>
				<c:if test="${oldData != newData}">
					<td colspan="4">
						<b>${datiRiga.TAB5_TAB5DESC } </b>
					</td>
				</tr>
						
				<tr class="odd">
				<c:set var="numCambi" value="${numCambi + 1}"/>
				</c:if>
			</gene:campoLista>
	
			<gene:campoLista title="Opzioni" width="50" >
				<c:if test="${currentRow >= 0}" >
				<gene:PopUp variableJs="jvarRow${currentRow}" onClick="chiaveRiga='${chiaveRigaJava}'">
					<gene:PopUpItem title="Seleziona" href="javascript:archivioSeleziona(${datiArchivioArrayJs});"/>
				</gene:PopUp>
				</c:if>
			</gene:campoLista>
			<gene:campoLista title="" width="22" >
				<c:choose>
					<c:when test="${datiRiga.V_CAIS_TIT_NUMLIV > 1}">
						<img width="22" height="16" title="Categoria di livello ${datiRiga.V_CAIS_TIT_NUMLIV}" alt="Categoria di livello ${datiRiga.V_CAIS_TIT_NUMLIV}" src="${pageContext.request.contextPath}/img/livelloCategoria${datiRiga.V_CAIS_TIT_NUMLIV}.gif"/>
					</c:when>
					<c:otherwise>
						&nbsp;
					</c:otherwise>
				</c:choose>
			</gene:campoLista>
			<gene:campoLista campo="CAISIM" visibile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})" ordinabile="false"/>
			<gene:campoLista campo="DESCAT" visibile="true" href="javascript:archivioSeleziona(${datiArchivioArrayJs})" ordinabile="false"/>
			<gene:campoLista campo="ACONTEC" visibile="false" />
			<gene:campoLista campo="QUAOBB" visibile="false" />
			<gene:campoLista campo="TIPLAVG" visibile="false" />
			<gene:campoLista campo="CAISIM" visibile="false" />
			<gene:campoLista campo="CAISORD" visibile="false" />
			<gene:campoLista campo="TITCAT"  visibile="false"/>
			<gene:campoLista campo="NUMORD" visibile="false" />
			<gene:campoLista campo="TAB5DESC" entita = "TAB5" where ="TAB5.TAB5COD = 'G_j05' and TAB5.TAB5TIP = V_CAIS_TIT.TITCAT" visibile="false" />
			<gene:campoLista campo="ISARCHI" visibile="false" />
			<gene:campoLista campo="ISFOGLIA"  visibile = "false"/>
			<gene:campoLista campo="NUMLIV"  visibile = "false"/>
				
			<%/* La variabile numCambi serve per poter mantendere il layout */%>
			<c:set var="indiceRiga" value="${indiceRiga + 1}"/>
			
			<%/* Questa parte di codice setta lo stile della riga in base che sia un titolo oppure una riga di dati */%>
			<gene:campoLista visibile="false" >
				<th style="display:none">
					<c:if test="${oldData != newData}"><script type="text/javascript">
						var nomeForm = document.forms[0].name;
						var indice = ${indiceRiga};
                        /*
						if(indice==2){
                        	indice =  indice -1 ;
                        }
                        */
                        
                        document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className =document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi }  ) - 1].className;
						document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } ) - 1 ].className = "white";
					</script></c:if>
				</th>
			</gene:campoLista>
			<gene:campoLista visibile="false">
	             <th style="display:none">
			         <c:if test="${datiRiga.V_CAIS_TIT_ISFOGLIA eq '2'}">
			         <c:set var="numliv" value="${datiRiga.V_CAIS_TIT_NUMLIV}"/>
		                 <script type="text/javascript">
			                 var nomeForm = document.forms[0].name;
 							 var indice = ${indiceRiga};
 							 
 							 document.getElementById("tab" + nomeForm).rows[indice  + (${numCambi } )].className = "livello"+${numliv};
		                 </script>
		             </c:if>
	             </th>
		     </gene:campoLista>
			
		</gene:formLista>
  </gene:redefineInsert>
  <gene:javaScript>
  
	function cambiaCategoria(tipoCategoria){
		
		//E' stato premuto il stato Applica, quindi si deve ricavare il tipo di categoria selezionata		
		if(tipoCategoria==-100){
			var indice = document.forms[0].filtroCategoria.length;
			
			for(var i=0;i<indice;i++){
				if(document.forms[0].filtroCategoria[i].checked){
					tipoCategoria = document.forms[0].filtroCategoria[i].value;
					
				}
			}
		}		
		
		var valore=document.getElementById("filtroUlteriore").value;
		if(valore != null && valore!="" && valore.length>0){
			valore = valore.toUpperCase();
			
		}
		var condizioneWhere = "V_CAIS_TIT.TIPLAVG=" + tipoCategoria + "${param.filtroLotto}";
		var parentFormName = eval('window.opener.activeArchivioForm');
		eval("window.opener.document." + parentFormName + ".archWhereLista").value = condizioneWhere;
    	var nomeCampoArchivio = null;
		
		/*
		if(parentFormName == "formCategoriaPrevalenteGare"){
			if(document.archivioSchedaForm.archCampoChanged.value == "V_CAIS_TIT.CAISIM")
				nomeCampoArchivio = "CATG_CATIGA";
			if(document.archivioSchedaForm.archCampoChanged.value == "V_CAIS_TIT.DESCAT")
				nomeCampoArchivio = "V_CAIS_TIT_DESCAT";
		} else if(parentFormName.indexOf("formUlterioreCategoriaGare") >= 0){
			var idUltCat = parentFormName.substr("formUlterioreCategoriaGare".length);
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.CAISIM") >= 0)
				nomeCampoArchivio = "OPES_CATOFF_" + idUltCat;
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.DESCAT") >= 0)
				nomeCampoArchivio = "V_CAIS_TIT_DESCAT_" + idUltCat;
		} else if(parentFormName == "formCategoriaPrevalente"){
			if(document.archivioSchedaForm.archCampoChanged.value == "V_CAIS_TIT.CAISIM")
				nomeCampoArchivio = "CATAPP_CATIGA";
			if(document.archivioSchedaForm.archCampoChanged.value == "V_CAIS_TIT.DESCAT")
				nomeCampoArchivio = "V_CAIS_TIT_DESCAT";
		} else if(parentFormName.indexOf("formUlterioreCategoria") >= 0){
			var idUltCat = parentFormName.substr("formUlterioreCategoria".length);
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.CAISIM") >= 0)
				nomeCampoArchivio = "ULTAPP_CATOFF" + idUltCat;
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.DESCAT") >= 0)
				nomeCampoArchivio = "V_CAIS_TIT_DESCAT" + idUltCat;
		} else if(parentFormName.indexOf("formCategoriaElenco") >= 0){
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.CAISIM") >= 0)
				nomeCampoArchivio = "OPES_CATOFF";
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.DESCAT") >= 0)
				nomeCampoArchivio = "CAIS_DESCAT";
		} else if(parentFormName.indexOf("formCategoriaImpresa") >= 0){
			var idCatIscr = parentFormName.substr("formCategoriaImpresa".length);
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.CAISIM") >= 0)
				nomeCampoArchivio = "CATE_CATISC_" + idCatIscr;
			if(document.archivioSchedaForm.archCampoChanged.value.indexOf("V_CAIS_TIT.DESCAT") >= 0)
				nomeCampoArchivio = "V_CAIS_TIT_DESCAT_" + idCatIscr;
		} else {
		  // la funzione viene richiamata dalla pagina del subappalto
		  if(document.archivioSchedaForm.archCampoChanged.value == "V_CAIS_TIT.CAISIM")
				nomeCampoArchivio = "SUBA_CATLAV";
			if(document.archivioSchedaForm.archCampoChanged.value == "V_CAIS_TIT.DESCAT")
				nomeCampoArchivio = "V_CAIS_TIT_DESCAT";
		}
		
		if(nomeCampoArchivio != null) {
			eval("window.opener.document.forms[0]." + nomeCampoArchivio).value = document.archivioSchedaForm.archValueCampoChanged.value.replace(/%/g, "");
		}
		*/
		
		// la seguente riga serve a modificare il nome della popup, in modo da
		// gestire la chiusura della presente e la riapertura della stessa in un'altra
		// popup in modo indipendente, evitando un problema di sequenzialità in IE per cui
		// con tale browser la close di una popup non è nel momento atteso		
		window.name = parentFormName + "Old";
		window.opener.arch = window.opener.getArchivio(parentFormName);
		
		var archValueChiave= "${param.archValueChiave }";
		if(archValueChiave == valore){
			window.opener.arch.fnLista(nomeCampoArchivio);
		}else{
			//Nella pagina è stato modificato il valore del filtro, quindi si deve aggiornare il valore
			//del campo archValueChiave della finestra chiamante
			eval("window.opener.document." + parentFormName + ".archValueChiave").value = valore;
			eval("window.opener.document." + parentFormName + ".metodo").value = "lista";
			window.opener.arch.submit(true);
		}
	}
	
	//Disabilitato il pulsante invio
	function stopRKey(evt) {
	  var evt = (evt) ? evt : ((event) ? event : null);
	  var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
	  if ((evt.keyCode == 13) && (node.type=="text"))  {return false;}
	}

	document.onkeypress = stopRKey; 
	
	
	
	
	</gene:javaScript>
