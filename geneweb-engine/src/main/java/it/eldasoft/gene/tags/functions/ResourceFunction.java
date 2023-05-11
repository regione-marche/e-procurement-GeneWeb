package it.eldasoft.gene.tags.functions;

import it.eldasoft.gene.tags.utils.UtilityTags;

/**
 * Classe che si incarica di eseguire la lettura dei resource manager
 * 
 * @author cit_franceschin
 * 
 */
public class ResourceFunction {

  /**
   * Funzione che estrae il dato
   * 
   * @param key
   * @return
   */
  public static String get(String key, String params[]) {
    return UtilityTags.getResource(key, params, true);
  }

  public static String get(String key, Object obj[]) {
    if (obj != null) {
      String params[] = new String[obj.length];
      for(int i=0;i<obj.length;i++)
        if(obj[i]!=null)
          params[i]=obj[i].toString();
        else
          params[i]="";
      return get(key,params);
    }
    return get(key);
  }

  public static String get(String key) {

    return get(key, new String[] {});
  }

  public static String get(String key, String param1) {
    return get(key, new String[] { param1 });
  }

  public static String get(String key, String param1, String param2) {
    return get(key, new String[] { param1, param2 });
  }

  public static String get(String key, String param1, String param2,
      String param3) {
    return get(key, new String[] { param1, param2, param3 });
  }

  public static String get(String key, String param1, String param2,
      String param3, String param4) {
    return get(key, new String[] { param1, param2, param3, param4 });
  }

}
