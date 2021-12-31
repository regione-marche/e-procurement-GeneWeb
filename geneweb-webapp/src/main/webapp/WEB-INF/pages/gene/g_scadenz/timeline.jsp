<%--
/*
 * Created on: 21/09/2016
 *
 * Copyright (c) EldaSoft S.p.A.
 * Tutti i diritti sono riservati.
 *
 * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
 * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
 * aver prima formalizzato un accordo specifico con EldaSoft.
 */

--%>

<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<gene:template file="scheda-template.jsp" gestisciProtezioni="true" schema="GENE" idMaschera="G_SCADENZ-timeline">


	<gene:redefineInsert name="addHistory">
			<gene:historyAdd titolo='Sequenza temporale' id='CAL-TIMELINE' replaceParam="" />
	</gene:redefineInsert>
	
	<gene:redefineInsert name="documentiAzioni"></gene:redefineInsert>

	<gene:redefineInsert name="head">
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/d3.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/d3kit.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/labella.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/d3kit-timeline.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/rgbcolor.js"></script> 
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/StackBlur.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/canvg.js"></script> 
		
		
		<style type="text/css">
		
		    .axis path{
		      fill: none;
		      stroke-width: 2px;
		      stroke: #222;
		    }
		    
		    .axis line{
		      fill: none;
		      stroke-width: 1px;
		      stroke: #222;
		    }
		    
		    .axis text{
		      font-size: 11px;
		    }
		    
		    path.link{
		      stroke-width: 1px;
		      opacity: 0.75;
		    }
		    
		    circle.dot{
		      stroke-width: 1px;
		      stroke: #222;
		      stroke-opacity: 0.5;
		    }		
		    		    
		    text.label-text{
		      font-size: 11px;
		      white-space: normal;
		      width: 100px;
		      height: 55px;
		    }
		    
		    div.stitle {
		    	padding-top: 5px;
		    	padding-bottom: 0px;
		    	font-weight: bold;
		    	font-size: 11px;
		    }
		    
		    span.legenda {
		    	padding: 2 2 2 2;
		    	color: #FFFFFF;
				border: 1px solid #EFEFEF;
				-moz-border-radius-topleft: 4px; 
				-webkit-border-top-left-radius: 4px; 
				-khtml-border-top-left-radius: 4px; 
				border-top-left-radius: 4px; 
				-moz-border-radius-topright: 4px;
				-webkit-border-top-right-radius: 4px;
				-khtml-border-top-right-radius: 4px;
				border-top-right-radius: 4px;
				-moz-border-radius-bottomleft: 4px; 
				-webkit-border-bottom-left-radius: 4px; 
				-khtml-border-bottom-left-radius: 4px; 
				border-bottom-left-radius: 4px; 
				-moz-border-radius-bottomright: 4px;
				-webkit-border-bottom-right-radius: 4px;
				-khtml-border-bottom-right-radius: 4px;
			}

		    span.legenda-bgcolorOggi {
			    background-color: #B200FF;
			}
		    
		    span.legenda-bgcolorP {
			    background-color: #1F77B4;
			}
			
			span.legenda-bgcolorE_Neutro {
			    background-color: #FF7F0E;
			}
			
			span.legenda-bgcolorE_DiffPiu {
			    background-color: #D62728;
			}
			
			span.legenda-bgcolorE_DiffMeno {
			    background-color: #2CA02C;
			}
	
		  </style>
		
	</gene:redefineInsert>

	<gene:setString name="titoloMaschera" value="Sequenza temporale e confronto tra scadenze previste ed aggiornate/effettive" />

	<gene:redefineInsert name="corpo">
		<table class="dettaglio-notab">
			<tr>
				<td colspan="2" style="padding: 2 2 2 2; border: 0px; background-color: #E8E8E8;">
					<div class="stitle">Legenda</div>
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 4 2 4 8; border: 0px; background-color: #E8E8E8;">	
					<span class="legenda legenda-bgcolorP">&nbsp;&nbsp;&nbsp;</span>&nbsp;Scadenza prevista
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 4 2 4 8; border: 0px; background-color: #E8E8E8;">	
					<span class="legenda legenda-bgcolorE_DiffMeno">&nbsp;&nbsp;&nbsp;</span>&nbsp;Attivit&agrave; completata in data precedente o uguale alla data prevista
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 4 2 4 8; border: 0px; background-color: #E8E8E8;">	
					<span class="legenda legenda-bgcolorE_DiffPiu">&nbsp;&nbsp;&nbsp;</span>&nbsp;Attivit&agrave; completata in data successiva alla data prevista
				</td>
			</tr>
			<tr>
				<td colspan="2" style="padding: 4 2 8 8; border-top: 0px; background-color: #E8E8E8;">	
					<span class="legenda legenda-bgcolorE_Neutro">&nbsp;&nbsp;&nbsp;</span>&nbsp;Attivit&agrave; non completata
				</td>
			</tr>
			
			<tr id="timelineContainer">
				<td style="padding-top: 8px; background-color: #EFEFEF;">
					<div class="stitle" style="padding-left: 155px;">Scadenze previste</div>
					<div class="timeline">
      					<div id="timelineP"></div>
      				</div>
      				
					<img style="position:absolute; top: 270px; left: 166px; opacity: 0; filter: alpha(opacity=0)" id="exportTimelineImg" src="">
      				
				</td>
				<td style="padding-top: 8px; background-color: #EFEFEF;">
					<div class="stitle" style="padding-left: 8px;">Scadenze aggiornate o effettive</div>
					<div class="timeline">
      					<div id="timelineE"</div>
      				</div>
      			</td>
			</tr>
		</table>
		
		<canvas style="display: none;" id="canvasTimeline"></canvas>
		
		<gene:javaScript>
		$(document).ready(function() {
		
			var bgcolorP = "#1F77B4";
			var bgcolorE_Neutro = "#FF7F0E";
			var bgcolorE_DiffPiu = "#D62728";
			var bgcolorE_DiffMeno = "#2CA02C";
			var bgColorE_Oggi = "#B200FF"
			var _ndots = 0;

			function dateDiffInDays(a, b) {
			  var utc1 = Date.UTC(a.getFullYear(), a.getMonth(), a.getDate());
			  var utc2 = Date.UTC(b.getFullYear(), b.getMonth(), b.getDate());
			  return Math.floor((utc2 - utc1) / (1000 * 60 * 60 * 24));
			}
		
			function timef(d) {
				var _d = d.time.getDate();
				var _m = d.time.getMonth() + 1;
				var _y = d.time.getFullYear();
				if (_d < 10) _d = '0' + _d;
				if (_m < 10) _m = '0' + _m;
				return _d + '/' + _m + '/' + _y;
			}
		
			$.ajax({
				url: '${pageContext.request.contextPath}/GetAttivitaTimeline.do',
				type: 'POST',
				dataType: 'json',
				data: {
					entita: '${param.entita}',
					chiave: '${param.chiave}',
					metodo: 'all'
				},
				success: function( data ) {
					if (data) {
						var minTime = data[0];
						var maxTime = data[1];
						var dataP = [];
						var dataE = [];
						
						if (data[2] != null && data[2] != "") {
							var listG_SCADENZ = data[2];
							$.map( listG_SCADENZ, function( itemG_SCADENZ ) {
							
								_ndots++;
								
								if (itemG_SCADENZ[5] == false) {
									var rowP = {
										"time" : new Date(itemG_SCADENZ[1]),
										"name" : itemG_SCADENZ[0],
										"consuntivo" : null,
										"differenza" : null,
										"id" : itemG_SCADENZ[4],
										"colore" : null
									}	
									dataP.push(rowP);
								}
								
								var differenza = dateDiffInDays(new Date(itemG_SCADENZ[1]), new Date(itemG_SCADENZ[2]));
								var colore = "";
								if (itemG_SCADENZ[5] == true) {
									colore = bgColorE_Oggi;
								} else {
									if (itemG_SCADENZ[3] == false) {
									    colore = bgcolorE_Neutro;
								    } else {
										if (differenza > 0) {
											colore = bgcolorE_DiffPiu;	
										} else {
											colore = bgcolorE_DiffMeno;	
										}						    
								    }
								}

								var rowE = {
									"time" : new Date(itemG_SCADENZ[2]),
									"name" : itemG_SCADENZ[0],
									"consuntivo" : itemG_SCADENZ[3],
									"differenza" : differenza,
									"id" : itemG_SCADENZ[4],
									"colore" : colore
								}	
								dataE.push(rowE);
							});
						}
						
						var chartP = new d3KitTimeline('#timelineP', {
						  direction: 'left',
						  initialHeight: 400 + (_ndots * 30),
						  initialWidth: 320,
						  layerGap: 60,
						  domain : [minTime,maxTime],
						  margin: {left: 0, right: 50, top: 30, bottom: 50},
						  textFn: function(d){
						    return timef(d);
						  },
						  dotColor: bgcolorP,
						  labelBgColor: bgcolorP,
						  linkColor: bgcolorP
						});
						chartP.axis.ticks(0).tickSize(2);
						chartP.data(dataP);
						
						var chartE = new d3KitTimeline('#timelineE', {
						  direction: 'right',
						  initialHeight: 400 + (_ndots * 30),
						  initialWidth: 480,
						  layerGap: 60,
						  domain : [minTime,maxTime],
						  margin: {left: 4, right: 0, top: 30, bottom: 50},
						  textFn: function(d){
						  	var _t = "";
						  	if (d.name.length > 55) {
						  		_t = timef(d) + " - " + d.name.substr(0,52) + "..."
						  	} else {
						  		_t = timef(d) + " - " + d.name;
						  	}
						    return _t;
						  },
						  dotColor: function(d){
						  	return d.colore;
						  },
						  labelBgColor: function(d){
						  	return d.colore;
						  },
						  linkColor: function(d){
						    return d.colore;
						  }
						});
						chartE.axis.ticks(0).tickSize(2);
						chartE.data(dataE);
				
						var _timelineP_svg = d3.select('#timelineP').select('.main-layer').append('g');
						var _dotsP = $("#timelineP").find(".dot-layer").find(".dot");
						var _dotsE = $("#timelineE").find(".dot-layer").find(".dot");
						$.map( _dotsP, function( _itemDotP ) {
							var _itemDotPId = _itemDotP.__data__.id;
							$.map( _dotsE, function( _itemDotE ) {
								var _itemDotEId = _itemDotE.__data__.id;
								if (_itemDotPId == _itemDotEId) {
									var _cxP = 4;
									var _cxE = 50;
									var _cyP = _itemDotP.cy.baseVal.value;
									var _cyE = _itemDotE.cy.baseVal.value;
									
									_timelineP_svg.append('line')
										.attr("x1", _cxP)
                        				.attr("y1", _cyP)
						                .attr("x2", _cxE)
                         				.attr("y2", _cyE)
                       					.attr("stroke-width", 2)
                       					.attr("opacity", "0.75")
                       					.attr("stroke-dasharray", "3,3")
                        				.attr("stroke", _itemDotE.__data__.colore);
								}
							});
						});
					}
				},
				error: function() {
					alert('Errori durante la lettura delle attività in scadenza');
				},
				complete: function() {
					var _canvasP = $("#timelineP").html();
					var _canvasE = $("#timelineE").html();
					var _h = 400 + (_ndots * 30);
					canvg('canvasTimeline', '<svg width="800" height="' + _h + '">' + _canvasP + '<g transform="translate(320,000)">' + _canvasE + '</g></svg>');
					var _canvas = document.getElementById("canvasTimeline");
					var _img = _canvas.toDataURL("image/png");
					$("#exportTimelineImg").attr("src",_img);
				}
			});

			
			
		});
		</gene:javaScript>
		
	</gene:redefineInsert>
</gene:template>
