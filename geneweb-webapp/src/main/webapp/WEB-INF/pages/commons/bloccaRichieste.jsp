<%/*!-- JSP che inserisce nella pagina il codice per la visualizzazione dei 
     div per realizzare il blocco dell'utilizzo dei link e bottoni nella 
     videata, in modo da non inviare al server più richieste di fila se 
     l'utente clicca ripetutamente sui comandi azionabili, ma invia solo
     la prima--*/%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<script type="text/javascript">
  var trasparenzaPerBrowser = "";
  

  if (navigator.appName == "Microsoft Internet Explorer") {
  	trasparenzaPerBrowser = " filter:alpha(opacity=20);";
  } else if (navigator.appName == "Netscape") {
  	// SS 24/08/2009: introdotta differenziazione firefox 3.5 e successivi nella gestione
  	// della trasparenza
  	var indiceFirefox = navigator.userAgent.substring(navigator.userAgent.indexOf("Firefox/"));
  	if (indiceFirefox != -1) {
  		if (navigator.userAgent.substring(indiceFirefox+8)>"3.5")
  			trasparenzaPerBrowser = " opacity:0.2";
  		else 
  			trasparenzaPerBrowser = " -moz-opacity:0.2";
  	}
  }
  
  document.writeln('<DIV id="bloccaScreen" style="visibility:hidden; position:absolute; background-color:#808080; z-index:1000; height:100%; width:100%;' + trasparenzaPerBrowser + '"><'+'/DIV>');
  
</script>
<IFRAME class="gene" id="iframewait"></IFRAME>
<DIV id="wait" style="visibility:hidden; position:absolute; z-index:1000; width:400px; background-color:#FFFFFF; border:1px solid #000000; padding:10px; color:#404040; font: 14px Verdana, Arial, Helvetica, sans-serif; text-align:center;">
  <IMG src="${contextPath}/img/wait.gif" alt="">&nbsp;<B>Operazione in corso, attendere...</B>
</DIV>
