package com.olympic.dakar.soap.config;

import com.olympic.dakar.common.exception.ResourceNotFoundException;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.List;
import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {

    private static final String NAMESPACE = "http://olympic.dakar.com/soap/olympic-management";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean(name = "olympic-management")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema olympicSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("OlympicManagementPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace(NAMESPACE);
        wsdl11Definition.setSchema(olympicSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema olympicSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/olympic-management.xsd"));
    }

    /**
     * Rejette (SOAP Fault client) toute requête non conforme au XSD (élément
     * requis manquant, type invalide, élément inconnu...) au lieu de laisser
     * JAXB appliquer silencieusement des valeurs par défaut.
     */
    @Override
    public void addInterceptors(List<EndpointInterceptor> interceptors) {
        PayloadValidatingInterceptor interceptor = new PayloadValidatingInterceptor();
        interceptor.setXsdSchema(olympicSchema());
        interceptor.setValidateRequest(true);
        interceptor.setValidateResponse(true);
        interceptors.add(interceptor);
    }

    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        SoapFaultMappingExceptionResolver resolver = new SoapFaultMappingExceptionResolver();
        resolver.setOrder(1);

        SoapFaultDefinition defaultFault = new SoapFaultDefinition();
        defaultFault.setFaultCode(SoapFaultDefinition.SERVER);
        defaultFault.setFaultStringOrReason("Une erreur inattendue est survenue.");
        resolver.setDefaultFault(defaultFault);

        Properties exceptionMappings = new Properties();
        exceptionMappings.setProperty(ResourceNotFoundException.class.getName(),
                "CLIENT,Ressource introuvable");
        resolver.setExceptionMappings(exceptionMappings);

        return resolver;
    }
}
