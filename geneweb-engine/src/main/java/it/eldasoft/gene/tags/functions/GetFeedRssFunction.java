package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.commons.web.domain.CostantiGenerali;
import it.eldasoft.gene.tags.utils.AbstractFunzioneTag;
import it.eldasoft.utils.properties.ConfigManager;
import it.eldasoft.utils.utility.UtilityNumeri;
import it.eldasoft.utils.utility.UtilityStringhe;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserException;
import com.sun.cnpi.rss.parser.RssParserFactory;

/**
 * Classe per la lettura dei feed RSS
 * 
 * @author stefano.cestaro
 * 
 */
public class GetFeedRssFunction extends AbstractFunzioneTag {

  static Logger logger = Logger.getLogger(GetFeedRssFunction.class);

  public GetFeedRssFunction() {
    super(1, new Class[] { PageContext.class });
  }

  public String function(PageContext pageContext, Object[] params)
      throws JspException {

    if (logger.isDebugEnabled())
      logger.debug("GetFeedRssFunction: inizio metodo");

    List listaRSSItemCompleta = new Vector();

    int numeroFeedRssValidi = 0;

    // Numero di fonti RSS da leggere
    // Se la property non è definita o si è
    // verificato un errore di formato si assegna il valore 0
    Integer numeroFeedRss = UtilityNumeri.convertiIntero(ConfigManager.getValore(CostantiGenerali.PROP_FEED_RSS_NUMERO));
    if (numeroFeedRss == null) numeroFeedRss = new Integer(0);

    // Numero elementi massimi da leggere (per ogni feed)
    // Se la property non è assegnata o si è
    // verificato un errore di formato si assegna il valore 5
    Integer numeroMassimoElementi = UtilityNumeri.convertiIntero(ConfigManager.getValore(CostantiGenerali.PROP_FEED_RSS_NUMERO_MASSIMO_ELEMENTI));
    if (numeroMassimoElementi == null) numeroMassimoElementi = new Integer(5);

    // Indirizzo del server proxy
    //String proxyHost = UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(CostantiGenerali.PROP_FEED_RSS_PROXY_HOST));
    
    // Numero di porta del proxy
    // Integer proxyPort = UtilityNumeri.convertiIntero(ConfigManager.getValore(CostantiGenerali.PROP_FEED_RSS_PROXY_PORT));
    
    // Proxy User
    String proxyUser = (String) System.getProperties().get("http.proxyUser");
    
    // Proxy Password 
    String proxyPassword = (String) System.getProperties().get("http.proxyPassword");
    
    if (numeroFeedRss.intValue() > 0) {
      for (int i = 1; i <= numeroFeedRss.intValue(); i++) {
        String url = UtilityStringhe.convertiStringaVuotaInNull(ConfigManager.getValore(CostantiGenerali.PROP_FEED_RSS_URL
            + "."
            + i));

        // Se l'indirizzo non è vuoto procedo alla lettura del feed RSS
        if (url != null) {
          numeroFeedRssValidi++;

          List listaRSSItem = new Vector();
          this.readFeedRss(url, numeroMassimoElementi, listaRSSItem, proxyUser, proxyPassword);

          if (!listaRSSItem.isEmpty()) {
            for (int j = 0; j < listaRSSItem.size(); j++) {
              listaRSSItemCompleta.add(listaRSSItem.get(j));
            }
          }

        }
      }
    }

    pageContext.setAttribute("listaRSSItem", listaRSSItemCompleta);

    if (logger.isDebugEnabled())
      logger.debug("GetFeedRssFunction: fine metodo");

    // Restituisce il numero di feed RSS validi
    return new Integer(numeroFeedRssValidi).toString();

  }

  /**
   * Lettura del feed indicato al parametro URL
   * 
   * @param listaRSSItem
   * @param url
   */
  private void readFeedRss(String url, Integer numeroMassimoElementi,
      List listaRSSItem, String proxyUser, String proxyPassword) {

    if (logger.isDebugEnabled()) logger.debug("readFeedRss: inizio metodo");

    try {

      URL feedRssURL = null;
      Rss rss = null;
      RssParser parser = RssParserFactory.createDefault();
      feedRssURL = new URL(url);
      
      
//      if(proxyHost != null && proxyPort != null)
//      {
//          Properties prop = System.getProperties();
//          prop.put("http.proxyHost", proxyHost);
//          prop.put("http.proxyPort", proxyPort.toString());
//      }
//    feedRssURL = new URL(url);
//    Rss rss = parser.parse(feedRssURL);
      
      
      if (proxyUser != null && proxyPassword != null) {
        URLConnection uc = feedRssURL.openConnection ();
        String encoded = new String (Base64.encodeBase64(new String(proxyUser + ":" + proxyPassword).getBytes()));
        uc.setRequestProperty("Proxy-Authorization", "Basic " + encoded);
        uc.connect();
        rss = parser.parse(uc.getInputStream());
      } else {
        rss = parser.parse(feedRssURL);
      }

      // Informazioni generali del feed RSS
      String rssTitle = "";
      if (rss.getChannel().getTitle() != null) {
        rssTitle = rss.getChannel().getTitle().toString();
      }

      String rssLink = "";
      if (rss.getChannel().getLink() != null) {
        rssLink = rss.getChannel().getLink().toString();
      }

      String rssDescription = "";
      if (rss.getChannel().getDescription() != null) {
        rssDescription = rss.getChannel().getDescription().toString();
      }

      String rssPubDate = "";
      if (rss.getChannel().getPubDate() != null) {
        rssPubDate = rss.getChannel().getPubDate().toString();
      }

      // Aggiungo le informazioni generali del feed RSS
      listaRSSItem.add(((Object) (new Object[] { "RSS", rssTitle, rssLink,
          rssDescription, rssPubDate })));

      // Carico tutti gli item privi di categoria
      this.readFeedRssItems(rss, numeroMassimoElementi, null, listaRSSItem);

      // Carico gli item definiti in una qualche categoria,
      // raggruppandoli per categoria.
      // La lista delle categorie deve essere ricavata direttamente dagli item
      // stessi e non dalla collection "rss.getChannel().getCategories()"
      // che nei feed rss provati è sempre vuota
      Collection categories = new Vector();
      Collection itemsCategory = rss.getChannel().getItems();

      if (itemsCategory != null && !itemsCategory.isEmpty()) {
        for (Iterator iItemsCategory = itemsCategory.iterator(); iItemsCategory.hasNext();) {
          Item itemCategory = (Item) iItemsCategory.next();

          if (itemCategory.getCategories() != null) {
            String category = itemCategory.getCategories().toString();
            if (!categories.contains(category)) {
              categories.add(category);

              // Aggiungo l'informazione sulla categoria
              listaRSSItem.add(((Object) (new Object[] { "CATEGORY",
                  category.substring(1, category.length() - 1), "", "", "" })));

              // Lettura di tutti gli item corrispondenti alla categoria
              // corrente
              this.readFeedRssItems(rss, numeroMassimoElementi, category,
                  listaRSSItem);

            }
          }
        }
      }

    } catch (RssParserException e) {
      listaRSSItem.clear();
      listaRSSItem.add(((Object) (new Object[] { "RSS", url, url,
          "Impossibile leggere il feed RSS indicato", "" })));

    } catch (MalformedURLException e) {
      listaRSSItem.clear();
      listaRSSItem.add(((Object) (new Object[] { "RSS", url, url,
          "Impossibile leggere il feed RSS indicato", "" })));

    } catch (IOException e) {
      listaRSSItem.clear();
      listaRSSItem.add(((Object) (new Object[] { "RSS", url, url,
          "Impossibile leggere il feed RSS indicato", "" })));
    }

    if (logger.isDebugEnabled()) logger.debug("readFeedRss: fine metodo");

  }

  /**
   * Lettura dei singoli item del feed RSS
   * 
   * @param listaRSSItem
   * @param numeroMassimoElementi
   * @param rss
   */
  private void readFeedRssItems(Rss rss, Integer numeroMassimoElementi,
      String category, List listaRSSItem) {

    Collection items = rss.getChannel().getItems();
    if (items != null && !items.isEmpty()) {

      int numeroElemento = 0;
      for (Iterator iItems = items.iterator(); iItems.hasNext();) {

        Item item = (Item) iItems.next();

        // Ricavo la categoria dell'item corrente
        String itemCategory = null;
        if (item.getCategories() != null) {
          itemCategory = item.getCategories().toString();
        }

        if ((category != null && itemCategory != null && category.equals(itemCategory))
            || (category == null && itemCategory == null)) {
          numeroElemento++;
          if (numeroElemento <= numeroMassimoElementi.intValue()) {
            String itemTitle = item.getTitle().toString();
            String itemLink = item.getLink().toString();
            String itemDescription = item.getDescription().toString();

            String itemPubDate = "";
            if (item.getPubDate() != null) {
              itemPubDate = item.getPubDate().toString();
            }

            listaRSSItem.add(((Object) (new Object[] { "ITEM", itemTitle,
                itemLink, itemDescription, itemPubDate })));
          }
        }
      }
    }
  }

}
