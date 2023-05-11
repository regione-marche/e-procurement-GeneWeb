package it.eldasoft.gene.tags.gestori.submit;

import java.sql.SQLException;

import org.springframework.transaction.TransactionStatus;

import it.eldasoft.gene.bl.GenChiaviManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.UtilityStruts;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;
import it.eldasoft.utils.spring.UtilitySpring;

/**
 * Gestore per update dei dati della pagina sedute di gara
 *
 * @author Marcello.Caminiti
 */
public class GestoreQformPreview extends AbstractGestoreEntita {

    @Override
  public String getEntita() {
        return "WSDMCONFI";
    }

    public GestoreQformPreview() {
      super(false);
    }

    public GestoreQformPreview(boolean isGestoreStandard) {
      super(isGestoreStandard);
    }

    @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {
    }

    @Override
  public void postDelete(DataColumnContainer datiForm)
            throws GestoreException {
    }

    @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {
    }

    @Override
  public void postInsert(DataColumnContainer datiForm)
            throws GestoreException {

    }

    @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm)
            throws GestoreException {

      String idPreview = UtilityStruts.getParametroString(this.getRequest(),
          "idPreview");
      String numElementi = UtilityStruts.getParametroString(this.getRequest(),
          "numElementi");
      String salvataggioDati = UtilityStruts.getParametroString(this.getRequest(),
          "salvataggioDati");
      Long idPreviewLong = null;
      String ricaricaParametri = "NO";
      if("SI".equals(salvataggioDati)) {
        GenChiaviManager genChiaviManager = (GenChiaviManager) UtilitySpring.getBean("genChiaviManager",
            this.getServletContext(), GenChiaviManager.class);

        Long nElementi = null;

        if(idPreview == null || "".equals(idPreview)) {
          int id = genChiaviManager.getNextId("QFORMCONFITEMP.IDPREVIEW");
          idPreviewLong = new Long(id);
        }else {
          idPreviewLong = new Long(idPreview);
          try {
            this.sqlManager.update("delete from QFORMCONFITEMP where idpreview=?", new Object[] {idPreviewLong});
          } catch (SQLException e) {
            throw new GestoreException("Errore nella cancellazione delle occorrenze di QFORMCONFITEMP con IDPREVIEW=" + idPreview, null, e);
          }
        }


        nElementi = new Long(numElementi);
        String descrizione = "";
        String valore = "";
        String chiave = "";
        int idTabellaTemp=0;
        String valarray="";
        for(int i=1; i<=nElementi.intValue();i++) {
          valore = datiForm.getString("VALORE_" + i);
          descrizione = UtilityStruts.getParametroString(this.getRequest(),"DESCRI_NASCOSTO_" + i);
          chiave = UtilityStruts.getParametroString(this.getRequest(),"CHIAVE_NASCOSTO_" + i);
          valarray = UtilityStruts.getParametroString(this.getRequest(),"VALARRAY_" + i);
          idTabellaTemp = genChiaviManager.getNextId("QFORMCONFITEMP");
          try {
            this.sqlManager.update("insert into QFORMCONFITEMP(ID,IDPREVIEW,CHIAVE,VALORE,DESCRI,VALARRAY) values(?,?,?,?,?,?)",
                new Object[] {new Long(idTabellaTemp),idPreviewLong,chiave,valore,descrizione,valarray});
          } catch (SQLException e) {
            throw new GestoreException("Errore nell'inserimento delle occorrenze di QFORMCONFITEMP con IDPREVIEW=" + idPreview, null, e);
          }
        }
      }else {
        idPreviewLong = new Long(idPreview);
        ricaricaParametri = "SI";
      }
      this.getRequest().setAttribute("idPreview", idPreviewLong.toString());
      this.getRequest().setAttribute("ricaricaParametri", ricaricaParametri);
    }

    @Override
  public void postUpdate(DataColumnContainer datiForm)
            throws GestoreException {
    }

}