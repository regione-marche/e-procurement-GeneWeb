<%/*
       * Created on 15-giu-2006
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE IL TEMPLATE DELLA HOMEPAGE
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="listaOpzioniDisponibili" value="${fn:join(opzDisponibili,'#')}#"/>
<c:set var="listaOpzioniUtenteAbilitate" value="${fn:join(profiloUtente.funzioniUtenteAbilitate,'#')}#" />

<HTML lang="it">
<HEAD>
<jsp:include page="/WEB-INF/pages/commons/headStd.jsp" />

<script type="text/javascript">
<!--
<jsp:include page="/WEB-INF/pages/commons/checkDisabilitaBack.jsp" />

  // al click nel documento si chiudono popup e menu
  if (ie4||ns6) document.onclick=hideSovrapposizioni;

  function hideSovrapposizioni() {
	hideMenuPopup();
    hideSubmenuNavbar();
  }
  
function mostraModelli() {
	if (${fn:contains(listaOpzioniDisponibili, "OP1#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou50#") || fn:contains(listaOpzioniUtenteAbilitate, "ou51#"))}) {
		if (document.getElementById("tModelli").style.display == "")
			nascondiModelli();
		else
			document.getElementById("tModelli").style.display = "";
		}
	}
	
  function nascondiModelli() {
  	if (${fn:contains(listaOpzioniDisponibili, "OP1#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou50#") || fn:contains(listaOpzioniUtenteAbilitate, "ou51#"))})
		document.getElementById("tModelli").style.display = "none";
	}

function mostraReport() {
	if (${fn:contains(listaOpzioniDisponibili, "OP2#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || fn:contains(listaOpzioniUtenteAbilitate, "ou49#"))}) {
		if (document.getElementById("tReport").style.display == "")
			nascondiReport();
		else
			document.getElementById("tReport").style.display = "";
		}
	}
	
  function nascondiReport() {
  		if (${fn:contains(listaOpzioniDisponibili, "OP2#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || fn:contains(listaOpzioniUtenteAbilitate, "ou49#"))})
			document.getElementById("tReport").style.display = "none";
	}

	function nascondiTutto() {
		nascondiModelli();
		nascondiReport();
		}
  
-->
</script>

<jsp:include page="/WEB-INF/pages/commons/jsSubMenuComune.jsp"/>
<jsp:include page="/WEB-INF/pages/commons/jsSubMenuSpecifico.jsp"/>

</HEAD>

<BODY onload="setVariables();checkLocation();initPage();nascondiTutto();checkBrowser();" >
<TABLE class="arealayout">
<%/*questa definizione dei gruppi di colonne serve a fissare la dimensione dei td in modo da vincolare la posizione iniziale del menù di navigazione
	     sopra l area lavoro appena al termine del menù contestuale */%>
	<colgroup width="150px"></colgroup>
	<colgroup width="*"></colgroup>
	<TBODY>
		<TR class="testata">
			<TD colspan="2">
				<jsp:include page="/WEB-INF/pages/commons/testata.jsp"/>
			</TD>
		</TR>
		<TR class="menuprincipale" >
			<TD><img src="${contextPath}/img/spacer-azionicontesto.gif" alt=""></TD>
			<TD>
      <table class="contenitore-navbar">
				<tbody>
					<tr>
						<jsp:include page="/WEB-INF/pages/commons/menuSpecifico.jsp"/>
						<jsp:include page="/WEB-INF/pages/commons/menuComune.jsp"/>
					</tr>
				</tbody>
			</table>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I SOTTOMENU DEL MENU PRINCIPALE DI NAVIGAZIONE -->
			<IFRAME class="gene" id="iframesubnavmenu"></iframe>
			<div id="subnavmenu" class="subnavbarmenuskin"
				onMouseover="highlightSubmenuNavbar(event,'on');"
				onMouseout="highlightSubmenuNavbar(event,'off');"></div>

			<!-- PARTE NECESSARIA PER VISUALIZZARE I POPUP MENU DI OPZIONI PER CAMPO -->
			<IFRAME class="gene" id="iframepopmenu"></iframe>
			<div id="popmenu" class="popupmenuskin"
				onMouseover="highlightMenuPopup(event,'on');"
				onMouseout="highlightMenuPopup(event,'off');"></div>

			</TD>
		</TR>
		<TR>
			<TD class="menuazioni">
				<div id="menulaterale">				
				<gene:template file="menuAzioni-template.jsp">
					<gene:historyClear/>
				</gene:template>
				</div>
			</TD>
			<TD class="arealavoro">
			
				<jsp:include page="/WEB-INF/pages/commons/areaPreTitolo.jsp" />
			
				<table class="dettaglio-home">
				  <tr>
				  	<td colspan="2">
				  		&nbsp;
				  	</td>
				  </tr>
				  <jsp:include page="/WEB-INF/pages/commons/browserSupportati.jsp">
						<jsp:param name="colspan" value="2" />
				  </jsp:include>
				  <tr> 
				  	<td colspan="2" class="voce">
				  		<table cellspacing="10" cellpadding="5" class="dettaglio-home">
				  			<tr>
							    <td class="sotto-voce" width="49">
							    	<a href="${contextPath}/geneGenric/ListaRicerchePredefinite.do" tabindex="2000" class="link-generico">
								    	<img  src="${contextPath}/img/lista_report.gif" width="49" height="50" title="Report predefiniti" alt="Report predefiniti">
								    </a>
							    </td>
							    <td valign="middle">
				    				<p><b>
								    	<a href="${contextPath}/geneGenric/ListaRicerchePredefinite.do" tabindex="2001" class="link-generico">
							          		Report predefiniti
								        </a>
							        </b></p>
							        <p>
							        	Vai alla lista dei report disponibili
							        </p>
							    </td>
								</tr>
							</table>
						</td>
				  </tr>
				  
  			  <c:if test='${fn:contains(listaOpzioniDisponibili, "OP2#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou48#") || fn:contains(listaOpzioniUtenteAbilitate, "ou49#"))}'>
				  
				  <tr> 
				    <td colspan="2" class="voce">
				    	<table cellspacing="10" cellpadding="5" class="dettaglio-home">
				    		<tr>
				    			<td class="sotto-voce" width="49">
				    				<a href="javascript:mostraReport();" tabindex="2010" class="link-generico">
								    	<img src="${contextPath}/img/report.gif" width="48" height="49" title="Report" alt="Report">
								    </a>
								</td>
							    <td class="sotto-voce" valign="middle">
							    	<p><b>
							    		<a href="javascript:mostraReport();" tabindex="2011" class="link-generico">
							          		Report
							        	</a>
							        </b></p>
							        <p>
							        	Trova report da gestire oppure crea un nuovo report di analisi dati
							        </p>
							    </td>
				  			</tr>
  						    <tr id="tReport"> 
							  	<td>&nbsp;</td>
							    <td>
							    	<table class="dettaglio-home">
							        	<tr> 
								          	<td class="sotto-voce">
								          		<a href="${contextPath}/geneGenric/InitTrovaRicerche.do" tabindex="2020" class="link-generico">
								          			<img src="${contextPath}/img/trova.jpg"	title="Trova report" alt="Trova report"></a>&nbsp;
							          	       	<a href="${contextPath}/geneGenric/InitTrovaRicerche.do" tabindex="2021" class="link-generico">Trova report</a>
								            </td>
							        	</tr>
							        	<tr>
							          		<td class="sotto-voce">
							          			<a href="${contextPath}/geneGenric/CreaNuovaRicerca.do" tabindex="2022" class="link-generico">
								          			<img src="${contextPath}/img/add.gif" title="Crea nuovo report" alt="Crea nuovo report"></a>&nbsp;
								    	      	<a href="${contextPath}/geneGenric/CreaNuovaRicerca.do" tabindex="2023" class="link-generico">Crea report</a>
								          	</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>	
					</td>
				</tr>
			</c:if>
			<c:if test='${fn:contains(listaOpzioniDisponibili, "OP1#") && (fn:contains(listaOpzioniUtenteAbilitate, "ou50#") || fn:contains(listaOpzioniUtenteAbilitate, "ou51#"))}'>
				<tr> 
				    <td colspan="2" class="voce">
				    	<table cellspacing="10" cellpadding="5" class="dettaglio-home">
				    		<tr>
				    			<td class="sotto-voce" width="49">
				    				<a href="javascript:mostraModelli();" tabindex="2030" class="link-generico">
								    	<img src="${contextPath}/img/modello.gif" width="45" height="49" alt="Modelli" title="Modelli">
								    </a>
								</td>
							    <td class="sotto-voce" valign="middle">
							    	<p><b>
							    		<a href="javascript:mostraModelli();" tabindex="2031" class="link-generico">Modelli</a>
							        </b></p>
							        <p>
							        	Trova modelli di documento da gestire oppure crea un nuovo documento
							        </p>
							    </td>
				  			</tr>
  						    <tr id="tModelli"> 
							  	<td>&nbsp;</td>
							    <td>
							    	<table class="dettaglio-home">
							        	<tr> 
								          	<td class="sotto-voce">
									          	<a href="${contextPath}/geneGenmod/InitTrovaModelli.do" tabindex="2040" class="link-generico">
								          			<img src="${contextPath}/img/trova.jpg"	alt="Trova modelli" title="Trova modelli"></a>&nbsp;
							          	       	<a href="${contextPath}/geneGenmod/InitTrovaModelli.do" tabindex="2041" class="link-generico">Trova modelli</a>
								            </td>
							        	</tr>
							        	<tr> 
							          		<td class="sotto-voce">
								          		<a href="${contextPath}/geneGenmod/Modello.do?metodo=creaModello" tabindex="2042" class="link-generico">
								          			<img src="${contextPath}/img/add.gif" alt="Crea nuovo modello" title="Crea nuovo modello"></a>&nbsp;
								          		<a href="${contextPath}/geneGenmod/Modello.do?metodo=creaModello" tabindex="2043" class="link-generico">Crea modello</a>
								          	</td>
										</tr>
									</table>
								</td>
							</tr>
						</table>	
					</td>
				</tr>
			</c:if>
			<c:if test='${profiloUtente.utenteLdap == 0 && gene:checkProt(pageContext,"SUBMENU.VIS.UTILITA.Cambia-password")}'>
				  <tr> 
				  	<td colspan="2" class="voce">
				  		<table cellspacing="10" cellpadding="5" class="dettaglio-home">
				  			<tr>
							    <td class="sotto-voce" width="49">
							    	<a href="${contextPath}/geneAdmin/InitCambiaPasswordAdmin.do?metodo=cambioBase&amp;provenienza=menu" tabindex="2050" class="link-generico">
								    	<img src="${contextPath}/img/password.jpg" width="49" height="53" alt="Cambia password" title="Cambia password">
								    </a>
							    </td>
							    <td valign="middle">
				    				<p><b>
								    	<a href="${contextPath}/geneAdmin/InitCambiaPasswordAdmin.do?metodo=cambioBase&amp;provenienza=menu" tabindex="2051" class="link-generico">
							          		Cambia password
								        </a>
							        </b></p>
							        <p>
							        	Cambia la password di accesso alla tua utenza
							        </p>
							    </td>
							</tr>
						</table>
					</td>
				  </tr>
			</c:if>
				</table>


			</TD>
		</TR>

		<TR>
			<TD COLSPAN="2">
			<div id="footer">
				<jsp:include page="/WEB-INF/pages/commons/footer.jsp" />
			</div>
			</TD>
		</TR>


	</TBODY>
</TABLE>
</BODY>
</HTML>
