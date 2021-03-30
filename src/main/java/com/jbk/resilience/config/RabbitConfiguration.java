package com.jbk.resilience.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "kuhnen.rabbit")
public class RabbitConfiguration {

    @NotNull(message = "User do rabbit não pode ser nulo.")
    private String user;

    @NotNull(message = "Password do rabbit não pode ser nulo.")
    private String password;

    @NotEmpty(message = "Vhost do rabbit não pode ser vazio.")
    private String vhost = "/";

}
