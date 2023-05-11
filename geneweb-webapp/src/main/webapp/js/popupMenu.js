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
// Nome:        popupMenu.js
// Descrizione: file Javascript contenente le funzioni per la gestione della popup
//              delle opzioni su una determinata informazionee, sia essa un campo
//              sia essa un record di una lista
// Dipendenze:  general.js
//////////////////////////////////////////


function hideMenuPopup(){
  if (window.iframePopupObj)
    iframePopupObj.thestyle.visibility=(ie4||ns6)? "hidden" : "hide";
  if (window.menuPopupObj)
    menuPopupObj.thestyle.visibility=(ie4||ns6)? "hidden" : "hide";
}

function highlightMenuPopup(e,state){
  var source_el;
  if (document.all)
    source_el=event.srcElement;
  else if (document.getElementById)
    source_el=e.target;
  if (source_el.className=="menuitems"){
    source_el.id=(state=="on")? "mouseoverpopup" : "";
  }
  else{
    while(source_el.id!="popmenu"){
      source_el=document.getElementById? source_el.parentNode : source_el.parentElement;
      if (source_el.className=="menuitems"){
        source_el.id=(state=="on")? "mouseoverpopup" : "";
      }
    }
  }
}

// visualizza, a partire dall'oggetto selezionato "nomeobj", il menu di popup individuato da "contenuto"
function showMenuPopup(nomeobj, contenuto){
  if (!document.all&&!document.getElementById&&!document.layers) return;

  hideMenuPopup();
  var obj = (document.all ? eval("document.all."+nomeobj) : document.getElementById(nomeobj));

  menuPopupObj=ie4? document.all.popmenu : ns6? document.getElementById("popmenu") : ns4? document.popmenu : "";
  menuPopupObj.thestyle=(ie4||ns6)? menuPopupObj.style : menuPopupObj;
  iframePopupObj=ie4? document.all.iframepopmenu : ns6? document.getElementById("iframepopmenu") : ns4? document.iframepopmenu : "";
  iframePopupObj.thestyle=(ie4||ns6)? iframePopupObj.style : iframePopupObj;

  if (ie4||ns6)
    menuPopupObj.innerHTML=contenuto;
  else {
    menuPopupObj.document.write('<layer name="gui" bgColor="#C8D7CD" width="200" onmouseout="javascript:hideMenuPopup();">'+contenuto+'</layer>');
    menuPopupObj.document.close();
  }

  // determino la dimensione dell'oggetto da visualizzare
  menuPopupObj.contentwidth=(ie4||ns6)? menuPopupObj.offsetWidth : menuPopupObj.document.gui.document.width;
  menuPopupObj.contentheight=(ie4||ns6)? menuPopupObj.offsetHeight : menuPopupObj.document.gui.document.height;

  // estraggo le coordinate x,y dell'oggetto cliccato
  var eventX=getAbsoluteLeft(obj);
  var eventY=getAbsoluteTop(obj);

  // determino le dimensioni della schermata
  var rightedge=ie4? document.body.clientWidth : window.innerWidth;
  var bottomedge=ie4? document.body.clientHeight : window.innerHeight;

  //cerco di visualizzare l'oggetto interamente nel browser
  if (eventX+menuPopupObj.contentwidth<rightedge)
    menuPopupObj.thestyle.left = eventX;
  else
    menuPopupObj.thestyle.left = eventX+obj.offsetWidth-menuPopupObj.contentwidth;

  if (eventY+menuPopupObj.contentheight<bottomedge)
    menuPopupObj.thestyle.top = eventY+obj.offsetHeight;
  else
    menuPopupObj.thestyle.top = eventY-menuPopupObj.contentheight;

  // assegno le stesse coordinate anche all'iframe
  iframePopupObj.thestyle.top=menuPopupObj.thestyle.top;
  iframePopupObj.thestyle.left=menuPopupObj.thestyle.left;
  iframePopupObj.thestyle.width=menuPopupObj.offsetWidth;
  iframePopupObj.thestyle.height=menuPopupObj.offsetHeight;

  // rendo l'oggetto visibile
  iframePopupObj.thestyle.visibility="visible";
  menuPopupObj.thestyle.visibility="visible";
}
