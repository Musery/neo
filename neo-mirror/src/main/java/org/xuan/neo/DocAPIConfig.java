package org.xuan.neo;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class DocAPIConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI().info(new Info().title("API Document").description("API Document For ALL"));
  }
}
