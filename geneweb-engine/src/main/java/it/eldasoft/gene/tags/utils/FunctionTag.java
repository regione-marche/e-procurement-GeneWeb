package it.eldasoft.gene.tags.utils;

import it.eldasoft.gene.tags.TagSupportGene;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;


public class FunctionTag extends TagSupportGene {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1989966569690826218L;

	private String obj;

	private String parametro;

	public String getObj() {
		return obj;
	}

	public void setObj(String obj) {
		this.obj = obj;
	}

	public String getParametro() {
		return parametro;
	}

	public void setParametro(String parametro) {
		this.parametro = parametro;
	}

	public int doStartTag() throws JspException {
		Object obj = UtilityTags.createObject(this.getObj());
		if (!(obj instanceof AbstractFunzioneTag)) {
			throw new JspException("Attenzione ! Tag callFunction: l'oggetto "
					+ this.getObj()
					+ " non è ereditato da AbstractFunzioneTag !");
		}
        AbstractFunzioneTag fn=((AbstractFunzioneTag) obj);
        fn.setRequest((HttpServletRequest)this.pageContext.getRequest());
        Object params[]=new Object[]{this.getParametro()};
        JspException exc=fn.getJspException(this.pageContext,params);
        if(exc!=null)
          throw exc;
		String lsRet = ((AbstractFunzioneTag) obj).function(this.pageContext,params);
		// Se restituisce qualche cose allora
		if (lsRet != null && lsRet.length() > 0) {
			try {
				this.pageContext.getOut().write(lsRet);
			} catch (IOException e) {
				throw new JspException(e.getMessage(), e);
			}
		}
		return super.doStartTag();
	}

}
