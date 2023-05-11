package it.eldasoft.gene.tags.gestori.submit;

import it.eldasoft.gene.bl.QuartzConfigManager;
import it.eldasoft.gene.db.datautils.DataColumnContainer;
import it.eldasoft.gene.web.struts.tags.gestori.AbstractGestoreEntita;
import it.eldasoft.gene.web.struts.tags.gestori.GestoreException;

import org.quartz.CronExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.context.support.WebApplicationContextUtils;
import it.eldasoft.utils.spring.UtilitySpring;

public class GestoreW_QUARTZ extends AbstractGestoreEntita {

  @Override
  public String getEntita() {
    return "W_QUARTZ";
  }

  @Override
  public void preDelete(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postDelete(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preInsert(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void postInsert(DataColumnContainer datiForm) throws GestoreException {

  }

  @Override
  public void preUpdate(TransactionStatus status, DataColumnContainer datiForm) throws GestoreException {

    String cronExpression = datiForm.getString("W_QUARTZ.CRON_EXPRESSION");
    if (!CronExpression.isValidExpression(cronExpression)) {
      throw new GestoreException("La pianificazione indicata non è una espressione valida", "cronExpression.invalid", null);
    }
  }

  @Override
  public void postUpdate(DataColumnContainer datiForm) throws GestoreException {

    ApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());

    QuartzConfigManager qcm = (QuartzConfigManager) UtilitySpring.getBean("quartzConfigManager", this.getServletContext(),
        QuartzConfigManager.class);

    String beanId = datiForm.getString("W_QUARTZ.BEAN_ID");
    String cronExpression = datiForm.getString("W_QUARTZ.CRON_EXPRESSION");

    qcm.loadQuartzConfig(ctx, beanId, cronExpression);

  }

}
