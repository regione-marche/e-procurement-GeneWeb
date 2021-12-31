/*
 * Created on 7-giu-2006
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

//////////////////////////////////////////
// Nome:        floatingMenu.js
// Descrizione: file Javascript contenente le funzioni per la gestione del men? 
//              laterale flottante. EVITARE QUALSIASI MODIFICA AL FILE.
// Dipendenze:  nessuna
//////////////////////////////////////////

var ystart=0,xstart=0;

  function setVariables(){
    if (navigator.appName == "Netscape") {
      if (parseInt(navigator.appVersion) >= 5){
        v=".top=";h=".left=";
        dS="document.getElementById(\"";sD="\").style";eD="\")";
        y="window.pageYOffset";x="window.pageXOffset";
      } else {
        v=".top=";h=".left=";
        dS="document.";sD="";eD="";
        y="window.pageYOffset";x="window.pageXOffset";
      }
    } else {
      v=".pixelTop=";h=".pixelLeft=";
      dS="document.getElementById(\"";sD="\").style";eD="\")";
      y="document.body.scrollTop";x="document.body.scrollLeft";
    }

    checkLocationA();
  }

  function checkLocationA(){ystart=eval(y);xstart=eval(x);}

  function checkLocation(){
    yy=eval(y);xx=eval(x);

    // gestione dell'eventuale men? laterale
    var menuLaterale="menulaterale";
    if (eval(dS+menuLaterale+eD) != null) {
      var movex=0,movey=0,xdiff=0,ydiff=0;
      ydiff=ystart-yy;xdiff=xstart-xx;
      if ((ydiff<(-1))||(ydiff>(1))) movey=Math.round(ydiff/10),ystart-=movey

      if (ystart>=(25+altezzaTestata)){eval(dS+menuLaterale+sD+v+(ystart+10));} else {eval(dS+menuLaterale+sD+v+(25+altezzaTestata));}
      if (xstart>=10){eval(dS+menuLaterale+sD+h+(xstart+5));} else {eval(dS+menuLaterale+sD+h+2);}
    }

		attivaBloccoPagina();
    setTimeout("checkLocation()",10);
  }

function attivaBloccoPagina(){
	// nuova sezione aggiunta solo per la visualizzazione della pagina per il blocco delle richieste al server
	if (bloccoPagina) {
	  var layerWait="bloccaScreen";
	  if (eval(dS+layerWait+eD) != null) {
	    eval(dS+layerWait+sD+v+yy);
	    eval(dS+layerWait+sD+h+xx);
	  }
	
	  var scrittaWait="wait";
	  if (eval(dS+scrittaWait+eD) != null) {
	    iframewaitObj=ie4? document.all.iframewait : ns6? document.getElementById("iframewait") : ns4? document.iframewait : "";
	    iframewaitObj.thestyle=(ie4||ns6)? iframewaitObj.style : iframewaitObj;
	    eval(dS+scrittaWait+sD+v+(Math.floor(document.body.clientHeight/2+yy)));
	    eval(dS+scrittaWait+sD+h+(Math.floor(document.body.clientWidth/2-200+xx)));
	    iframewaitObj.thestyle.top=eval(dS+scrittaWait+sD+".top");
	    iframewaitObj.thestyle.left=eval(dS+scrittaWait+sD+".left");
	    iframewaitObj.thestyle.width=eval(dS+scrittaWait+eD+".offsetWidth");
	    iframewaitObj.thestyle.height=eval(dS+scrittaWait+eD+".offsetHeight");
	    iframewaitObj.thestyle.visibility="visible";
	  }
	  
	  if (document.getElementById){
	    document.getElementById('bloccaScreen').style.visibility='visible';
	    document.getElementById('wait').style.visibility='visible';
	  } else {
	    if (document.layers){ //NS4
	      document.bloccaScreen.visibility = 'show';
	      document.wait.visibility = 'show';
	    } else { //IE4
	      document.all.bloccaScreen.style.visibility = 'visible';
	      document.all.wait.style.visibility = 'visible';
	    }
	  }
	}
}

function checkAttivaBloccoPaginaPopup(){
	yy=eval(y);xx=eval(x);
	attivaBloccoPagina();
	setTimeout('checkAttivaBloccoPaginaPopup()', 10);
}

function highlightSubmenuLaterale(e,state){
  var source_el;
  if (document.all)
    source_el=event.srcElement;
  else if (document.getElementById)
    source_el=e.target;
  if (source_el.className=="vocemenulaterale"){
    source_el.id=(state=="on")? "mouseovermenulaterale" : "";
  }
  else{
    while(source_el.id!="menulaterale"){
      source_el=document.getElementById? source_el.parentNode : source_el.parentElement;
      if (source_el.className=="vocemenulaterale"){
        source_el.id=(state=="on")? "mouseovermenulaterale" : "";
      }
    }
  }
}
