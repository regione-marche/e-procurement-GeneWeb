<%/*
       * Created on 26-ago-2010
       *
       * Copyright (c) EldaSoft S.p.A.
       * Tutti i diritti sono riservati.
       *
       * Questo codice sorgente e' materiale confidenziale di proprieta' di EldaSoft S.p.A.
       * In quanto tale non puo' essere distribuito liberamente ne' utilizzato a meno di 
       * aver prima formalizzato un accordo specifico con EldaSoft.
       */

      // PAGINA CHE CONTIENE LA HOMEPAGE DI PL
      %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.eldasoft.it/genetags" prefix="gene"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<c:set var="contextPath" value="${pageContext.request.contextPath}" />

<c:set var="numeroFeedRssValidi" value='${gene:callFunction("it.eldasoft.gene.tags.functions.GetFeedRssFunction",pageContext)}'/>			
<c:if test="${numeroFeedRssValidi ne '0'}">

	<script type="text/javascript" src="${contextPath}/js/prototype.js"></script>
	<script type="text/javascript" src="${contextPath}/js/newsMarquee.js"></script>

	<table class="dettaglio-home" style="width:300px; border:1px solid; border-color: #707070;">
		<tr>
			<td style="padding: 2px; border-bottom: 1px solid; border-color: #BFBFBF; background-color: #F9F9F9;">
				<img width="22" height="22" title="Feed RSS" alt="Feed RSS" src="${pageContext.request.contextPath}/img/rssfeed.png"/>
				<b>N e w s</b>
			</td>	
			<td align="right" style="padding: 2px; border-bottom: 1px solid; border-color: #BFBFBF; background-color: #F9F9F9;">	
				<img width="14" height="14" src="${pageContext.request.contextPath}/img/scrollup.jpg" onmouseover="javascript:scroll_up();" onmouseout="stopScroll();" alt="Scroll Up" />
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<div id="feedrss" style="height:350px; overflow:hidden;">
					<div>
						<table>
							<tr> 
								<td style="padding-left:10px; padding-right:10px;">
									<table class="dettaglio-home" style="width:300px;">	
										<c:forEach items="${listaRSSItem}" step="1" var="RSSItem" varStatus="status" >
											<c:choose>
												<c:when test="${RSSItem[0] eq 'RSS'}">
													<tr>
														<td style="PADDING-TOP: 18px;">
															<a class="link-generico" target="_blank" href="${RSSItem[2]}"> 
																<img width="18" height="18" title="${RSSItem[1]}" alt="${RSSItem[1]}" src="${pageContext.request.contextPath}/img/rssfeed.png"/>
															</a>
														</td>
														<td style="PADDING-TOP: 18px;">
															<b>
															<a class="link-generico" target="_blank" href="${RSSItem[2]}" title="${RSSItem[1]}">${RSSItem[1]}</a>
															</b>
															
															<c:if test="${!empty RSSItem[4]}">
																<br>
																<i style="color: #707070;">${RSSItem[4]}</i>
															</c:if>
															
															<c:if test="${RSSItem[1] ne RSSItem[3]}">
																<br>													
																${RSSItem[3]}
															</c:if>
														</td>
													</tr>
												</c:when>
												<c:when test="${RSSItem[0] eq 'CATEGORY'}">
													<tr>
														<td colspan="2" style="PADDING-TOP: 10px;">
															<i>Categoria:</i> <b>${RSSItem[1]}</b>										
														</td>									
													</tr>
												</c:when>
												<c:otherwise>
													<tr>
														<td  colspan="2" style="PADDING-LEFT: 18px; PADDING-TOP:7px; PADDING-BOTTOM:7px;">
														<a class="link-generico" target="_blank" href="${RSSItem[2]}" title="${RSSItem[1]}">${fn:substring(RSSItem[1],0,40)}...</a>
														<br>
														<c:if test="${!empty RSSItem[4]}">
															<i style="color: #707070;">${RSSItem[4]}</i>
															<br>
														</c:if>
														${RSSItem[3]}
														</td>
													</tr>
												</c:otherwise>
											</c:choose>
										</c:forEach>
									</table>
								</td>
							</tr>
						</table>
					</div>
				</div>
			</td>
		</tr>
		<tr>
			<td align="right" colspan="2" style="padding: 2px; border-top: 1px solid; border-color: #BFBFBF; background-color: #F9F9F9;">
				<img width="14" height="14" src="${pageContext.request.contextPath}/img/scrolldown.jpg" onmouseover="javascript:scroll_down();" onmouseout="stopScroll();" alt="Scroll Down" />
			</td>
		</tr>

	</table>	
	
	<script type="text/javascript">
	  var interval;
	
	  var feedrss = document.getElementById('feedrss');
	
	  var vertical = new verticalMarquee('feedrss');
	  
	  function scroll(scrollby) {
	  	feedrss.scrollTop = feedrss.scrollTop+scrollby;
		vertical.pauseScroll();
	  }
	  
	  function scroll_down() {
		scroll(6);
		interval = setTimeout('scroll_down()', 20);
	  }
	  
	  function scroll_up() {
		scroll(-6);
		interval = setTimeout('scroll_up()', 20);
	  }
	  
	  function stopScroll() {
		if (interval) clearTimeout(interval);
		vertical.playScroll();
	  }
	  
	</script>
	
</c:if>		
