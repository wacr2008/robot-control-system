package com.sqshq.akka.demo.receiver;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.routing.FromConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;

@RestController
@RequestMapping("/sensors")
public class Controller {

    @Autowired
    private ActorSystem system;

    private ActorRef router;

    @PostConstruct
    public void init() {
        router = system.actorOf(FromConfig.getInstance().props(), "clusterRouter");
    }

    @RequestMapping(value = "/data", method = RequestMethod.POST)
    private DeferredResult<Long> receiveSensorData(@RequestBody String data) {
        DeferredResult<Long> result = new DeferredResult<>();
        system.actorOf(Props.create(ReceiverActor.class, result, router))
                .tell(data, ActorRef.noSender());
        return result;
    }
}
