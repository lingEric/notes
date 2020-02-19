//spring和thymeleaf整合类
@Bean
public SpringResourceTemplateResolver templateResolver(){
	// SpringResourceTemplateResolver automatically integrates with Spring's own
	// resource resolution infrastructure, which is highly recommended.
	SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
	templateResolver.setApplicationContext(this.applicationContext);
	templateResolver.setPrefix("/WEB-INF/templates/");
	templateResolver.setSuffix(".html");
	// HTML is the default value, added here for the sake of clarity.
	templateResolver.setTemplateMode(TemplateMode.HTML);
	// Template cache is true by default. Set to false if you want
	// templates to be automatically updated when modified.
	templateResolver.setCacheable(true);
	return templateResolver;
}

@Bean
public SpringTemplateEngine templateEngine(){
	// SpringTemplateEngine automatically applies SpringStandardDialect and
	// enables Spring's own MessageSource message resolution mechanisms.
	SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	templateEngine.setTemplateResolver(templateResolver());
	// Enabling the SpringEL compiler with Spring 4.2.4 or newer can
	// speed up execution in most scenarios, but might be incompatible
	// with specific cases when expressions in one template are reused
	// across different data types, so this flag is "false" by default
	// for safer backwards compatibility.
	templateEngine.setEnableSpringELCompiler(true);
	return templateEngine;
}