package com.workflow.api.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/{path:[^\\.]*}", produces = MediaType.TEXT_HTML_VALUE)
    public String forward() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/static/**", produces = MediaType.ALL_VALUE)
    @ResponseBody
    public Mono<Resource> getStaticResource(ServerWebExchange exchange) {
        String path = exchange.getRequest().getPath().value();
        return Mono.just(new ClassPathResource("static" + path));
    }
}
