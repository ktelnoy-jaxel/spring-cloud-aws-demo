package com.jaxel.awsclientsdemo.message.api;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping(value = "/message")
public class MessagingController {

    private MessagingService messagingService;

    @PostMapping
    @ResponseStatus(code = CREATED)
    Object sendMessage(@RequestBody Message msg) {
        log.info("Got request to send: {}", msg);
        return messagingService.send(msg);
    }

    @ExceptionHandler
    @ResponseStatus(code = INTERNAL_SERVER_ERROR)
    public void handle(Exception e) {
        log.warn("Unexpected error: {}", e.toString());
    }
}
