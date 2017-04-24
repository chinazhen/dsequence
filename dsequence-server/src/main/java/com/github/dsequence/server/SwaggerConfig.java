package com.github.dsequence.server;

import com.mangofactory.swagger.configuration.SpringSwaggerConfig;
import com.mangofactory.swagger.models.dto.ApiInfo;
import com.mangofactory.swagger.plugin.EnableSwagger;
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger 框架配置
 *
 * @author ChenWoZe(YAN.WANG)
 * @version 1.0.0 createTime: 2016/4/15
 * @see SpringSwaggerConfig
 */
@Slf4j
@EnableSwagger
@Configuration
public class SwaggerConfig {

    @Value(value = "true")
    private boolean enabled;

    @Autowired
    private SpringSwaggerConfig springSwaggerConfig;

    @Bean
    public SwaggerSpringMvcPlugin swaggerSpringMvcPlugin() {
        return new SwaggerSpringMvcPlugin(springSwaggerConfig)
                .includePatterns("/api/.*")
                .apiInfo(new ApiInfo("Mapi Boot API", "API version 1.0", "", "", "", ""))
                .enable(enabled);
    }

}