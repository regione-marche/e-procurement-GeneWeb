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
// Nome:        navbarMenu.js
// Descrizione: file Javascript contenente le funzioni per la gestione del menu' 
//              principale dell'applicazione, da posizionare nella parte alta
//              della pagina. EVITARE QUALSIASI MODIFICA AL FILE.
// Dipendenze:  general.js
//////////////////////////////////////////

function showSubmenuNavbar(nomeobj, contenuto){
  if (!document.all&&!document.getElementById&&!document.layers) return;

  hideSubmenuNavbar();
  var obj = (document.all ? eval("document.all."+nomeobj) : document.getElementById(nomeobj));

  menuNavbarObj=ie4? document.all.subnavmenu : ns6? document.getElementById("subnavmenu") : ns4? document.subnavmenu : "";
  menuNavbarObj.thestyle=(ie4||ns6)? menuNavbarObj.style : menuNavbarObj;
  iframeNavbarObj=ie4? document.all.iframesubnavmenu : ns6? document.getElementById("iframesubnavmenu") : ns4? document.iframesubnavmenu : "";
  iframeNavbarObj.thestyle=(ie4||ns6)? iframeNavbarObj.style : iframeNavbarObj;

  if (ie4||ns6)
    menuNavbarObj.innerHTML=contenuto;
  else {
    menuNavbarObj.document.write('<layer name="gui" bgColor="#C8D7CD" width="250" onmouseout="javascript:hideSubmenuNavbar();">'+contenuto+'</layer>');
    menuNavbarObj.document.close();
  }
  
  if (contenuto != "") {
    // determino la dimensione dell'oggetto da visualizzare
    menuNavbarObj.contentwidth=(ie4||ns6)? menuNavbarObj.offsetWidth : menuNavbarObj.document.gui.document.width;
    menuNavbarObj.contentheight=(ie4||ns6)? menuNavbarObj.offsetHeight : menuNavbarObj.document.gui.document.height;

    // estraggo le coordinate x,y dell'oggetto cliccato
    var eventX=getAbsoluteLeft(obj);
    var eventY=getAbsoluteTop(obj);

    // determino le dimensioni della schermata
    var rightedge=ie4? document.body.clientWidth : window.innerWidth;
    var bottomedge=ie4? document.body.clientHeight : window.innerHeight;

    //cerco di visualizzare l'oggetto interamente nel browser
    if (eventX+menuNavbarObj.contentwidth<rightedge)
      menuNavbarObj.thestyle.left = eventX;
    else
      menuNavbarObj.thestyle.left = eventX+obj.offsetWidth-menuNavbarObj.contentwidth;

    menuNavbarObj.thestyle.top = eventY+obj.offsetHeight;

    iframeNavbarObj.thestyle.top=menuNavbarObj.thestyle.top;
    iframeNavbarObj.thestyle.left=menuNavbarObj.thestyle.left;
    iframeNavbarObj.thestyle.width=menuNavbarObj.offsetWidth;
    iframeNavbarObj.thestyle.height=menuNavbarObj.offsetHeight;
    iframeNavbarObj.thestyle.visibility="visible";
    menuNavbarObj.thestyle.visibility="visible";
  }
}

function hideSubmenuNavbar(){
  if (window.iframeNavbarObj)
    iframeNavbarObj.thestyle.visibility=(ie4||ns6)? "hidden" : "hide";
  if (window.menuNavbarObj)
    menuNavbarObj.thestyle.visibility=(ie4||ns6)? "hidden" : "hide";
}

function highlightSubmenuNavbar(e,state){
  var source_el;
  if (document.all)
    source_el=event.srcElement;
  else if (document.getElementById)
    source_el=e.target;
  if (source_el.className=="navsubmenuitems"){
    source_el.id=(state=="on")? "mouseovernavbar" : "";
  }
  else{
    while(source_el.id!="subnavmenu"){
      source_el=document.getElementById? source_el.parentNode : source_el.parentElement;
      if (source_el.className=="navsubmenuitems"){
        source_el.id=(state=="on")? "mouseovernavbar" : "";
      }
    }
  }
}

function creaVoceSubmenu(href, tabindex, etichetta, abilitato) {
	if(abilitato==null){
		abilitato=true;
	}
	if(abilitato){
		return "<div class=\"navsubmenuitems\"><a href=\"" + href + "\" tabindex=\"" + tabindex + "\">" + etichetta + "</"+"a></"+"div>";
	}
	return "<div class=\"navsubmenuitems\">" + etichetta + "</"+"div>";
}

function creaPopUpSubmenu(href, tabindex, etichetta, abilitato) {
	if(abilitato==null){
		abilitato=true;
	}
	if(abilitato){
		return "<div class=\"menuitems\"><a href=\"" + href + "\" tabindex=\"" + tabindex + "\">" + etichetta + "</a></"+"div>";
	}
	return "<div class=\"navsubmenuitems\">" + etichetta + "</"+"div>";
}

function creaVocePopUpChiusura(contextPath) {
  return "<DIV class=\"chiusuramenu\"><A href=\"javascript:hideMenuPopup();\" ><IMG src=\""+contextPath+"img/chiudi.gif\" height=\"8\" width=\"8\" alt=\"Chiudi\" title=\"Chiudi\"></"+"A></"+"DIV>";;
}

