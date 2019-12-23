package it.visualsoftware.notificator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
//import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {
	private String version;
	
	public SwaggerConfiguration(@Value ("${http.version}") String version) {
		this.version = version;
	}
	
	@Bean
	public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2).select()
            .apis(RequestHandlerSelectors.basePackage("it.visualsoftware.notificator"))
            .paths(PathSelectors.any())  
            .build().apiInfo(apiEndPointsInfo());
    }
    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder().title("Documentazione Notificator")
            .description("Notificator è un applicazione spring che integra due scheduler necessari al remind e all'invio delle notifiche da Leadmanager"
            		+ "Il primo scheduler si attiva al minuto 50 con cadenza oraria e si occupa di schedulare tutte le notifiche che dovranno essere inviate nei prossimi 60 minuti \n"
            		+ "Il secondo scheduler si attiva ogni minuto e si occupa dell'invio delle notifiche, l'invio avviene 10 minuti prima della scadenza")
//            .contact(new Contact("Visual Software srl", "https://www.visualsoftware.it/", "info@visualsoftware.it"))
//            .license("Apache 2.0")
//            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
            .version(version)
            .build();
    }
	


}
