package ru.netology.server.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.netology.server.logger.Logger;
import ru.netology.server.model.ConfirmOperation;
import ru.netology.server.model.Transfer;
import ru.netology.server.service.Service;

@RestController
@RequestMapping("/")
public class Controller {

    private final Service service;
    private final Logger logger;

    public Controller(Service service, Logger logger) {
        this.service = service;
        this.logger = logger;
    }

    @PostMapping("transfer")
    public Object transfer(@RequestBody Transfer transfer) {
        return service.runTransaction(transfer, logger);
    }

    @PostMapping("confirmOperation")
    public Object confirmOperation(@RequestBody ConfirmOperation confirmOperation) {
        return service.confirmOperation(confirmOperation, logger);
    }
}
