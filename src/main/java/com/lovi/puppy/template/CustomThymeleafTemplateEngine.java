package com.lovi.puppy.template;

import com.lovi.puppy.template.impl.CustomThymeleafTemplateEngineImpl;
import io.vertx.ext.web.templ.TemplateEngine;

public interface CustomThymeleafTemplateEngine extends TemplateEngine{

	 String DEFAULT_TEMPLATE_MODE = "XHTML";

	  /**
	   * Create a template engine using defaults
	   *
	   * @return  the engine
	   */
	  static CustomThymeleafTemplateEngine create() {
	    return new CustomThymeleafTemplateEngineImpl();
	  }

	  /**
	   * Set the mode for the engine
	   *
	   * @param mode  the mode
	   * @return a reference to this for fluency
	   */
	  CustomThymeleafTemplateEngine setMode(String mode);

	  /**
	   * Get a reference to the internal Thymeleaf TemplateEngine object so it
	   * can be configured.
	   *
	   * @return a reference to the internal Thymeleaf TemplateEngine instance.
	   */
	  org.thymeleaf.TemplateEngine getThymeleafTemplateEngine();
}
