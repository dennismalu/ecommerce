package it.progetto.ecommerce.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
@Configuration
public class CorsConfiguration { //implements Filter

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry corsRegistry){
                corsRegistry
                        .addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        //Le richieste fatte da Postman (o qualsiasi client non browser) non sono soggette a
                        //CORS perché CORS è una politica applicata solo dai browser per motivi di sicurezza
                        // - Postman: invia la richiesta direttamente al backend, ignorando del tutto CORS.
                        //   --> Funzionerà sempre, a prescindere da .allowedOrigins("http://localhost:4200")
                        // - Browser (es. Angular su http://localhost:4200): prima di inviare la richiesta POST,
                        //   il browser controlla se il server permette richieste da quell’origine.
                        //.allowCredentials(true)



                        //Se la richiesta Angular include credenziali (ad esempio JWT in header) non posso
                        //usare allowedOrigins("*") ma devo specificare esplicitamente l’origine e abilitare
                        //allowCredentials(true).



                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*");
            }
        };
    }
//
//    @Value("${app.client.url}")
//    private String clientAppUrl = "";
//
//    public CorsConfiguration() {}
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
//            throws IOException, ServletException {
//        HttpServletRequest request = (HttpServletRequest) req;
//        HttpServletResponse response = (HttpServletResponse) res;
//        Map<String, String> map = new HashMap<>();
//        String originHeader = request.getHeader("Origin");
//        response.setHeader("Access-Control-Allow-Origin", originHeader);
//        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT");
//        response.setHeader("Access-Control-Max-Age", "3600");
//        response.setHeader("Access-Control-Allow-Headers", "*");
//
//        if("OPTIONS".equalsIgnoreCase(request.getMethod())) {
//            response.setStatus(HttpServletResponse.SC_OK);
//        }
//        else{
//            chain.doFilter(req, res);
//        }
//    }
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {}
//
//    @Override
//    public void destroy() {}

}
