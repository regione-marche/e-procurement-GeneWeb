package it.eldasoft.gene.db.sql.sqlparser;

import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.utility.UtilityDate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class JdbcParametro implements Serializable {

  /**
   * UID
   */
  private static final long serialVersionUID = 6273913853146686855L;

  /**
   * Definizione del tipo di campo
   */
  public static final char TIPO_DATA       = 'D';

  public static final char TIPO_NUMERICO   = 'N';

  public static final char TIPO_DECIMALE   = 'F';

  public static final char TIPO_TESTO      = 'T';

  public static final char TIPO_BINARIO    = 'B';

  /** Tipo dato utilizzato dal decoratore di campi */
  public static final char TIPO_ENUMERATO  = 'E';

  public static final char TIPO_INDEFINITO = ' ';

  private Object           value;

  private char             tipo;

  /**
   * Costruttore che crea un parametro
   * 
   * @param tipo
   * @param value
   */
  public JdbcParametro(char tipo, Object value) {
    this.tipo = tipo == TIPO_INDEFINITO ? getTipo(value) : tipo;
    this.value = value;
  }

  /**
   * Costruttore di un parametro partendo da una stringa con
   * tipoParametro:ValoreParametro
   * 
   * @param parvalue
   */
  public JdbcParametro(String parvalue) {
    char lcTipo = JdbcParametro.TIPO_TESTO;
    if (parvalue == null) parvalue = "";
    // Estraggo il tipo se settato
    if (parvalue.length() >= 2
        && parvalue.charAt(1) == ':'
        && isValidType(parvalue.charAt(0))) {
      lcTipo = parvalue.charAt(0);
      parvalue = parvalue.substring(2);
    }
    // Setto l'oggetto solo se il valore non è vuoto (significa nullo)
    if (parvalue.length() > 0) {
      switch (lcTipo) {
      case JdbcParametro.TIPO_DATA:
        this.value = new Timestamp(UtilityDate.convertiData(parvalue,
            UtilityDate.FORMATO_GG_MM_AAAA).getTime());
        this.tipo = JdbcParametro.TIPO_DATA;
        break;
      case JdbcParametro.TIPO_DECIMALE:
        this.value = Double.valueOf(parvalue);
        this.tipo = TIPO_DECIMALE;
        break;
      case TIPO_NUMERICO:
        this.value = Long.valueOf(parvalue);
        this.tipo = TIPO_NUMERICO;
        break;
      case JdbcParametro.TIPO_BINARIO:
        this.value = new ByteArrayOutputStream();
        try {
            ((ByteArrayOutputStream)this.value).write(parvalue.getBytes());
          } catch (IOException e) {
            // se la stringa è diversa da null, impossibile che venga emessa
            // un'eccezione in fase di scrittura di uno stream in memoria
          }
        this.tipo = TIPO_BINARIO;
        break;
      default: // Di default si tratta di una stringa
        this.value = new String(parvalue);
        this.tipo = TIPO_TESTO;

      }
    } else {
      this.value = null;
      switch (lcTipo) {
      case JdbcParametro.TIPO_DATA:
      case JdbcParametro.TIPO_DECIMALE:
      case TIPO_NUMERICO:
      case TIPO_BINARIO:
        this.tipo = lcTipo;
        break;
      default: // Di default si tratta di una stringa
        this.tipo = TIPO_TESTO;
      }
    }
  }

  public static JdbcParametro getParametro(char tipoCampo, String valore) {
    StringBuffer buf = new StringBuffer("");
    buf.append(tipoCampo);
    buf.append(":");
    if (valore != null) buf.append(valore);
    return new JdbcParametro(buf.toString());
  }

  /**
   * Funzione che converte una stringa in un valore
   * 
   * @param parvalue
   *        Stringa nel formato [Tipo]:[Valore]
   * @return Valore convertito
   */
  public static JdbcParametro getParametro(String parvalue) {
    return new JdbcParametro(parvalue);
  }

  /**
   * Conversione a stringa
   * 
   * @param addTipo
   *        Flag che dice di aggiungere il tipo all'inizio della stringa
   */
  public String toString(boolean addTipo) {
    StringBuffer buf = new StringBuffer();
    if (addTipo) {
      buf.append(this.tipo);
      buf.append(":");
    }
    buf.append(this.getStringValue());
    return buf.toString();
  }

  /**
   * Converte in stringa senza mettere il tipo all'inizio
   */
  public String toString() {
    return this.toString(false);
  }

  /**
   * Converte il valore in stringa in base al suo tipo
   * 
   * @return dato in formato stringa
   */
  public String getStringValue() {
    String ret;
    if (value == null) return "";
    if (this.value instanceof Timestamp) {
      return UtilityDate.convertiData(new Date(
          ((Timestamp) this.value).getTime()), UtilityDate.FORMATO_GG_MM_AAAA);
    }
    if (this.value instanceof Date)
      return UtilityDate.convertiData((Date) this.value,
          UtilityDate.FORMATO_GG_MM_AAAA);
    if (this.value instanceof ByteArrayOutputStream)
      return ((ByteArrayOutputStream)this.value).toString();
    // Se si tratta
    if (this.value instanceof Double) {
      DecimalFormatSymbols simbols = new DecimalFormatSymbols();
      simbols.setDecimalSeparator('.');
      DecimalFormat decFormat = new DecimalFormat("#.##########", simbols);
      return decFormat.format(((Double) this.value).doubleValue());
    } else
      ret = this.value.toString();
    if ((this.tipo == TIPO_DECIMALE || this.tipo == TIPO_NUMERICO)
        && ret.indexOf('.') >= 0) {
      int pos = ret.indexOf('.');
      // Elimino tutti gli 0 a destra e l'eventuale punto
      for (int i = ret.length() - 1; i >= pos
          && new String(".0").indexOf(ret.charAt(i)) >= 0; i--) {
        ret = ret.substring(0, i);
      }
    }
    return ret;
  }

  /**
   * @return Returns the tipo.
   */
  public char getTipo() {
    return tipo;
  }

  /**
   * @return Returns the value.
   */
  public Object getValue() {
    // Restituisco null anche se si tratta di una stringa vuota
    if (tipo == JdbcParametro.TIPO_TESTO) {
      if (this.value instanceof String) {
        if (((String) this.value).length() == 0) return null;
      }
    }
    return value;
  }

  /**
   * Funzione che controlla che un tipo sia uno dei tipi validi
   * 
   * @param tipo
   *        Tipo di campo
   * @return
   */
  public static boolean isValidType(char tipo) {
    switch (tipo) {
    case JdbcParametro.TIPO_DATA:
    case JdbcParametro.TIPO_DECIMALE:
    case JdbcParametro.TIPO_NUMERICO:
    case JdbcParametro.TIPO_TESTO:
    case JdbcParametro.TIPO_BINARIO:
      return true;
    }
    return false;
  }

  /**
   * Funzione che reinizializza il parametro
   * 
   * @param par
   *        settaggio del parametro
   */
  public void set(JdbcParametro par) {
    // Setta il parametro solo se è di tipo uguale
    if (par.getTipo() == this.getTipo()) {
      this.value = par.getValue();
    } else
      this.set(new JdbcParametro(this.getTipo() + ":" + par.toString(false)));

  }

//  /**
//   * Funzione che trasforma un parametro in stringa inseribile direttamente in
//   * un SQL
//   * 
//   * @param object
//   *        Oggetto da inserire
//   * @param tipoDB
//   *        Tipo di database
//   * @return
//   */
//  public static String toString(Object object, char tipoDB) {
//    if (object instanceof Timestamp) {
//      Timestamp dato = (Timestamp) object;
//      StringBuffer buf = new StringBuffer("");
//      switch (tipoDB) {
//      // case SqlManager.DATABASE_ACCESS_PER_COMPOSITORE:
//      // // "{ts '"+String(ad_data,"yyyy-mm-dd hh:mm:ss")+"'}"
//      // buf.append("{ts '");
//      // buf.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dato));
//      // buf.append("'}");
//      // break;
//      case SqlManager.DATABASE_ORACLE_PER_COMPOSITORE:
//        break;
//      case SqlManager.DATABASE_SQL_SERVER_PER_COMPOSITORE:
//        break;
//      case SqlManager.DATABASE_POSTGRES_PER_COMPOSITORE:
//        break;
//      case SqlManager.DATABASE_DB2_PER_COMPOSITORE:
//        break;
//      default:
//        buf.append(dato.toString());
//      }
//      return buf.toString();
//    } else if (object instanceof Date) {
//      Date dato = (Date) object;
//      StringBuffer buf = new StringBuffer("");
//      switch (tipoDB) {
//      // case SqlManager.DATABASE_ACCESS_PER_COMPOSITORE:
//      // // "{ts '"+String(ad_data,"yyyy-mm-dd hh:mm:ss")+"'}"
//      // buf.append("{ts '");
//      // buf.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dato));
//      // buf.append("'}");
//      // break;
//      case SqlManager.DATABASE_ORACLE_PER_COMPOSITORE:
//        // "TO_DATE('"+String(ad_data,"dd-mm-yyyy hh:mm:ss")+"','DD-MM-YYYY
//        // HH24:MI:SS')"
//        buf.append("TO_DATE('");
//        buf.append(new SimpleDateFormat("dd-MM-yyyy hh:mm:ss").format(dato));
//        buf.append("','DD-MM-YYYY HH24:MI:SS')");
//        break;
//      case SqlManager.DATABASE_SQL_SERVER_PER_COMPOSITORE:
//        // "CONVERT(datetime,'"+String(ad_data,"yyyy-mm-dd hh:mm:ss")+"' ,20)"
//        buf.append("CONVERT(datetime,'");
//        buf.append(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(dato));
//        buf.append("' ,20)");
//        break;
//      case SqlManager.DATABASE_DB2_PER_COMPOSITORE:
//      case SqlManager.DATABASE_POSTGRES_PER_COMPOSITORE:
//        // sarebbe da definire questa sezione
//      default:
//        buf.append(dato.toString());
//      }
//      return buf.toString();
//    } else if (object instanceof Long) {
//      Long dato = (Long) object;
//      return String.valueOf(dato.longValue());
//    } else if (object instanceof Double) {
//      Double dato = (Double) object;
//      return String.valueOf(dato.doubleValue());
//    } else if (object instanceof String) {
//      String dato = (String) object;
//      StringBuffer buf = new StringBuffer("'");
//
//      switch (tipoDB) {
//      // case SqlManager.DATABASE_ACCESS_PER_COMPOSITORE:
//      // // ls_ret=gnv_gvarfun.e00_of_replace(ls_ret,"'","'+chr(39)+'")
//      // // Sostituisce il | con chr(124)
//      // // ls_ret=gnv_gvarfun.e00_of_replace(ls_ret,"|","'+chr(124)+'")
//      // dato = UtilityStringhe.replace(dato, "'", "'+chr(39)+'");
//      // dato = UtilityStringhe.replace(dato, "|", "'+chr(124)+'");
//      // break;
//      case SqlManager.DATABASE_ORACLE_PER_COMPOSITORE:
//        // ls_ret=e00_of_replace(ls_ret,"'","''")
//        dato = UtilityStringhe.replace(dato, "'", "''");
//        // ls_ret=e00_of_replace(ls_ret,"--","-'||CHR(45)||'")
//        dato = UtilityStringhe.replace(dato, "--", "-'||CHR(45)||'");
//        // ls_ret=e00_of_replace(ls_ret,"~~","'||CHR(126)||'")
//        dato = UtilityStringhe.replace(dato, "~", "'||CHR(126)||'");
//        // ls_ret=gnv_gvarfun.e00_of_replace(ls_ret,"~r","'||CHR(13)||'")
//        dato = UtilityStringhe.replace(dato, "\r", "'||CHR(13)||'");
//        // ls_ret=gnv_gvarfun.e00_of_replace(ls_ret,"~n","'||CHR(10)||'")
//        dato = UtilityStringhe.replace(dato, "\n", "'||CHR(10)||'");
//        break;
//      case SqlManager.DATABASE_SQL_SERVER_PER_COMPOSITORE:
//      case SqlManager.DATABASE_POSTGRES_PER_COMPOSITORE:
//      case SqlManager.DATABASE_DB2_PER_COMPOSITORE:
//        dato = UtilityStringhe.replace(dato, "'", "''");
//        // probabilmente andranno indicate anche altre replace per
//        // PostgreSQL o DB2, quindi andrebbe differenziato il codice
//        break;
//      }
//      buf.append(dato);
//      buf.append("'");
//      return buf.toString();
//    }
//    return null;
//  }

  public int compare(JdbcParametro par2) {
    if (this.getTipo() == par2.getTipo()) {
      if (this.getValue() != null && par2.getValue() != null) {
        switch (this.getTipo()) {
        case JdbcParametro.TIPO_DATA:
          return ((Timestamp) this.value).compareTo((Timestamp) par2.getValue());
        case JdbcParametro.TIPO_DECIMALE:
          return ((Double) this.value).compareTo((Double) par2.getValue());
        case TIPO_NUMERICO:
          return ((Long) this.value).compareTo((Long) par2.getValue());
        case TIPO_BINARIO:
          return ((ByteArrayOutputStream) this.value).toString().compareTo(((ByteArrayOutputStream) par2.getValue()).toString());
        default: // Di default si tratta di una stringa
          return ((String) this.value).compareTo((String) par2.getValue());
        }
      } else if (this.getValue() == null && par2.getValue() == null) {
        return 0;
      } else if (this.getValue() == null)
        return -1;
      else
        return 1;

    } else {
      // Se i parametri sono diversi allora confronto la loro trasformazione in
      // stringa
      this.toString().compareTo(par2.toString());
    }
    return 0;
  }
  /**
   * Confronta l'uguaglianza tra 2 oggetti di tipo JdbcParametro
   * 
   * @param par2 Il secondo parametro
   * @return true se hanno lo stesso valore
   */
  public boolean equals(Object par2) {
	  if (par2 instanceof JdbcParametro)
		  return (this.compare((JdbcParametro) par2) == 0);
	  else
		  return false;
  }

  /**
   * Funzione che restituisce il valore long
   * 
   * @return Valore o null se ci sono problemi
   * @throws JdbcException
   */
  public Long longValue() throws GestoreException {

    switch (this.getTipo()) {
    case TIPO_NUMERICO:
      return (Long) this.value;
    case TIPO_DECIMALE:
      if (this.value == null) return null;
      return new Long(((Double) this.value).longValue());
    default:
      throw new GestoreException("Impossibile convertire il valore di tipo "
          + this.getTipo()
          + " in Long", "convertParam.long");
    }
  }

  public Double doubleValue() throws GestoreException {
    switch (this.getTipo()) {
    case TIPO_NUMERICO:
      if (this.value == null) return null;
      return new Double(((Long) this.value).doubleValue());
    case TIPO_DECIMALE:
      if (this.value == null) return null;
      return (Double) this.value;
    default:
      throw new GestoreException("Impossibile convertire il valore di tipo "
          + this.getTipo()
          + " in Double", "convertParam.long");
    }
  }

  public String stringValue() throws GestoreException {
    switch (this.getTipo()) {
    case TIPO_TESTO:
      return (String) this.value;
    default:
      throw new GestoreException("Impossibile convertire il valore di tipo "
          + this.getTipo()
          + " in String", "convertParam.long");
    }
  }

  public Timestamp dataValue() throws GestoreException {
    switch (this.getTipo()) {
    case TIPO_DATA:
      if (this.value == null) return null;
      if (this.value instanceof Timestamp)
        return (Timestamp) this.value;
      else
        return new Timestamp(((Date) this.value).getTime());
    default:
      throw new GestoreException("Impossibile convertire il valore di tipo "
          + this.getTipo()
          + " in Data", "convertParam.long");
    }
  }

  public ByteArrayOutputStream byteArrayOutputStreamValue() throws GestoreException {
    switch (this.getTipo()) {
    case TIPO_BINARIO:
      return (ByteArrayOutputStream) this.value;
    default:
      throw new GestoreException("Impossibile convertire il valore di tipo "
          + this.getTipo()
          + " in ByteArrayOutputStream", "convertParam.long");
    }
  }

  public static char getTipo(Object object) {
    if (object instanceof String)
      return TIPO_TESTO;
    else if (object instanceof Long)
      return TIPO_NUMERICO;
    else if (object instanceof Double)
      return TIPO_DECIMALE;
    else if (object instanceof Timestamp) return TIPO_DATA;
    else if (object instanceof ByteArrayOutputStream) return TIPO_BINARIO;
    return TIPO_INDEFINITO;
  }

}
