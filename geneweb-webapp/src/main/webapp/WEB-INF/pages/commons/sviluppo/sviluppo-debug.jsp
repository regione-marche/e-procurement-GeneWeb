<%
			/*
       * Created on: 14.21 26/10/2007
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
					Pagina con tutti i dati di sviluppo
				Creato da:
					Marco Franceschin
			*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/tags" prefix="elda" %>

<gene:callFunction obj="it.eldasoft.gene.tags.functions.DebugSviluppoFunction" />
<c:if test='${isSviluppo}'>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" />
	<a href="javascript:onOffDebug();" title="Visualizza/nascondi informazioni di debug">
			<span id="imgdebugon" style="display: none; border-style: none;" ><img src="${pageContext.request.contextPath}/img/isDebugOn.png"  alt="" /><b>Informazioni di debug</b></span>
			<span id="imgdebugoff"><img src="${pageContext.request.contextPath}/img/isDebugOff.png" alt="" /><b>Informazioni di debug</b></span>
	</a>
	<div id="msgdebug" style="display: none;font-face: Tahoma;font-size: -2">
		<table border="1" width="100%">			
			<tr>
				<td colspan="2"><b><a href='javascript:showHideTr("gestProtez");' title="Mostra/Nascondi">Gestione Protezioni</a></b></td>
			</tr>
			<tr id="gestProtez" style="display: none;font-face: Tahoma;font-size: -2">
				<td colspan="2">
					<table border="1" width="100%">
						<tr>
							<td colspan="2"><a href="javascript:ricaricaProfilo()" ><b>Ricarica profilo corrente:</b> ${sessionScope.profiloAttivo}</a></td>
						</tr>
						<c:if test='${not empty requestDebugObject.idMaschera}'>
							<tr>
								<td>Id Pagina:</td> <td>${requestDebugObject.idPagina.id} - ${requestDebugObject.idPagina.descr}</td>
							</tr>
						</c:if>
						<tr>
							<td>Id Maschera:</td> <td>${requestDebugObject.idMaschera.id} - ${requestDebugObject.idMaschera.descr}</td>
						</tr>
						<c:if test='${not empty requestDebugObject.listaProtezioni}'>
							<tr>
								<td colspan="2"><b>Protezioni verificate</b></td>
							</tr>
							<%// Scorro tutte le funzioni %>
							<c:forEach items="${requestDebugObject.listaProtezioni}" var="prot" varStatus="status">
								<c:set var="is2Cols" value='${fn:contains("SEZ;MASC;PAGE;FUNZ;MENU;SUBMENU",prot.tipo)}' />
								<c:choose>
									<c:when test="${is2Cols}">
									<tr>
										<td ><font size="-1"><b>${prot.tipo}</b>.<i><b>${prot.azioni}</b>.${prot.id} </i></font></td>			
										<td>
											<font size="-1">
											${prot.subDescr}<span id="${prot.tipo}.${prot.id}" <c:if test='${not prot.ok}'>class="notinserted"</c:if> >${prot.descr}
											<c:if test="${not prot.ok}"><font color="#ff0000">(Non definito in W_OGGETTI)</font></c:if>
											</span>
											<a href='javascript:gestisciOggetto("${prot.tipo}","${prot.id}",${prot.ok});' title="Gestisci W_OGGETTI">
												<img src="${pageContext.request.contextPath}/img/gestOggetti.png"  alt="Gestione oggetti" />
											</a>
											</font>
										</td>
									</tr>
									</c:when>
									<c:otherwise>
										<gene:set name="addPost" >
										 ${addPost}
											<tr>
												<td colspan="2"><font size="-1"><b>${prot.tipo}</b>.<i><b>${prot.azioni}</b>.${prot.id} </i></font></td>
											</tr>
										</gene:set>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<c:if test="${not empty addPost}">
								<tr>
									<td colspan="2"><b>Altre protezioni</b></td>
								</tr>
								${addPost}
							</c:if>
						</c:if>
					</table>
				</td>
			</tr>
			<tr>
					<td colspan="2"><a href='javascript:showHideTr("trRequest");' title="Mostra/Nascondi"><b>Request</b></a></td>
			</tr>
			<tr id="trRequest" style="display: none;font-face: Tahoma;font-size: -2">
				<td colspan="2">
					${requestDebugObject.strRequest}
				</td>
			</tr>
			<tr>
					<td colspan="2"><a href='javascript:showHideTr("trSession");' title="Mostra/Nascondi"><b>Session</b></a></td>
			</tr>
			<tr id="trSession" style="display: none;font-face: Tahoma;font-size: -2">
				<td colspan="2">
					${requestDebugObject.strSession}
				</td>
			</tr>
			<tr>
					<td colspan="2"><a href='javascript:showHideTr("trApplication");' title="Mostra/Nascondi"><b>Application</b></a></td>
			</tr>
			<tr id="trApplication" style="display: none;font-face: Tahoma;font-size: -2">
				<td colspan="2">
					${requestDebugObject.strApplication}
				</td>
			</tr>
			<tr><td colspan="2">---------------------</td></tr>
		</table>
	</div>
	<gene:javaScript>
	
	
	
		var debugView=false;
		function onOffDebug(){
			debugView=!debugView;
			showObj("imgdebugon",debugView);
			showObj("imgdebugoff",!debugView);
			showObj("msgdebug",debugView);
		}
		function gestisciOggetto(tipo, id, oggettoCensito){
			var modoPopup = "MODIFICA";
			var identificativo = ("#"+tipo+"."+id).replace(/\./g,'\\\.');
			var elementoNonInseritoOra = $(identificativo).hasClass("notinserted");
			if (!oggettoCensito && elementoNonInseritoOra)  {
				modoPopup = "NUOVO&metodo=nuovo";
			}
			openPopUpCustom("href=commons/sviluppo/popUpOggetto.jsp&tipo="+tipo+"&id="+id+"&modo="+modoPopup, "gestisciOggetto", 900, 300, "yes", "no");
		}
		function updateDescr(tipo,id,descr){
			var identificativo = ("#"+tipo+"."+id).replace(/\./g,'\\\.');
			var elemento = $(identificativo);
			elemento.removeClass("notinserted");
			elemento.html(descr);
		}
		function showHideTr(idObj){
			// Inizializzaziono
			var obj=getObjectById(idObj);
			if(obj!=null){
				var show=obj.style.display=="none";
				//alert(show);
				if(show)
					obj.style.display="";
				else
					obj.style.display="none";
			}
		}
		function ricaricaProfilo(){
			openPopUpCustom("href=commons/sviluppo/ricaricaProfilo.jsp", "ricaricaProfilo", "300", "200", "no", "no");
		}
	</gene:javaScript>
</c:if>
