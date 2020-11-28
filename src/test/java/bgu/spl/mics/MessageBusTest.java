package bgu.spl.mics;
import bgu.spl.mics.example.messages.ExampleBroadcast;
import bgu.spl.mics.example.messages.ExampleEvent;
import bgu.spl.mics.example.services.ExampleBroadcastListenerService;
import bgu.spl.mics.example.services.ExampleEventHandlerService;
import bgu.spl.mics.example.services.ExampleMessageSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MessageBusTest {

    private MessageBusImpl messageBus;
    private ExampleBroadcastListenerService firstBroadcastListenerService;
    private ExampleBroadcastListenerService secondBroadcastListenerService;
    private ExampleEventHandlerService eventHandlerService;
    private ExampleMessageSenderService messageSenderService;
    private ExampleBroadcast broadcast;
    private ExampleEvent event;
    private Future<String> future;

    @BeforeEach
    public void setUp(){
        messageBus = new MessageBusImpl();
        firstBroadcastListenerService = new ExampleBroadcastListenerService("broadcastListenerService", new String[]{"1"});
        secondBroadcastListenerService = new ExampleBroadcastListenerService("broadcastListenerService", new String[]{"1"});
        eventHandlerService = new ExampleEventHandlerService("eventHandlerService", new String[]{"1"});
        future = new Future<>();
    }

    //No need to test
    @Test
    public void testSubscribeEvent()
    {
        assertDoesNotThrow(() -> messageBus.sendEvent(null));
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.subscribeEvent(event.getClass() , null));
        messageBus.register(eventHandlerService);
        messageBus.register(messageSenderService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        messageBus.sendEvent(event);
        Message message = null;
        try {
            message = messageBus.awaitMessage(eventHandlerService);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(message);
        assertEquals(message, event);
    }

    //No need to test
    @Test
    public void testSubscribeBroadcast() {
        assertDoesNotThrow(() -> messageBus.sendBroadcast(null));
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"broadcast"});
        broadcast = new ExampleBroadcast(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.subscribeBroadcast(broadcast.getClass() , null));
        messageBus.register(firstBroadcastListenerService);
        messageBus.register(secondBroadcastListenerService);
        messageBus.register(messageSenderService);
        messageBus.subscribeBroadcast(broadcast.getClass(), firstBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), secondBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), messageSenderService);
        messageBus.sendBroadcast(broadcast);
        Message message1 = null;
        Message message2 = null;
        try {
            message1 = messageBus.awaitMessage(firstBroadcastListenerService);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            message2 = messageBus.awaitMessage(secondBroadcastListenerService);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(message1);
        assertEquals(message1, broadcast);
        assertNotNull(message2);
        assertEquals(message2, broadcast);
    }

    @Test
    public void testComplete() {
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        future = messageBus.sendEvent(event);
        messageBus.complete(event, "completed");
        assertEquals(future.get(), "completed");
    }

    @Test
    public void testSendBroadcast() {
        assertDoesNotThrow(() -> messageBus.sendBroadcast(null));
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"broadcast"});
        broadcast = new ExampleBroadcast(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.subscribeBroadcast(broadcast.getClass() , null));
        messageBus.register(firstBroadcastListenerService);
        messageBus.register(secondBroadcastListenerService);
        messageBus.register(messageSenderService);
        messageBus.subscribeBroadcast(broadcast.getClass(), firstBroadcastListenerService);
        messageBus.subscribeBroadcast(broadcast.getClass(), secondBroadcastListenerService);
        messageBus.sendBroadcast(broadcast);
        Message message1 = null;
        Message message2 = null;
        try {
            message1 = messageBus.awaitMessage(firstBroadcastListenerService);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            message2 = messageBus.awaitMessage(secondBroadcastListenerService);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(message1);
        assertEquals(message1, broadcast);
        assertNotNull(message2);
        assertEquals(message2, broadcast);
    }

    @Test
    public void testSendEvent() {
        assertDoesNotThrow(() -> messageBus.sendEvent(null));
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        assertDoesNotThrow(() -> messageBus.subscribeEvent(event.getClass() , null));
        messageBus.register(eventHandlerService);
        messageBus.register(messageSenderService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        messageBus.sendEvent(event);
        Message message = null;
        try {
            message = messageBus.awaitMessage(eventHandlerService);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(message);
        assertEquals(message, event);
    }

    //No need to test
    @Test
    public void testRegister() {
        assertDoesNotThrow(() -> messageBus.register(null));
        messageBus.register(eventHandlerService);
        assertEquals(eventHandlerService.getName(), "EventMicroService");
        try {
            messageBus.unregister(eventHandlerService);
            assertEquals(eventHandlerService.getName(), "EventMicroService");
        }
        catch (IllegalStateException e) {
            fail("Microservice was registered before unregistered but still an exception was sent");
        }
    }

    //No need to test
    @Test
    public void testUnregister() {
        try {
            messageBus.unregister(null);
            messageBus.unregister(eventHandlerService);
            fail("Unregistered Microservice was received but no exception was sent");
        }
        catch (IllegalStateException e) {
            messageBus.register(eventHandlerService);
            assertEquals(eventHandlerService.getName(), "EventMicroService");
            messageBus.unregister(eventHandlerService);
            assertEquals(eventHandlerService.getName(), "EventMicroService");
        }
    }

    @Test
    public void testAwaitMessage() {
        try {
            messageBus.awaitMessage(null);
            fail("Null Microservice was received but no exception was sent");
        }
        catch (IllegalStateException | InterruptedException e) {
            assertTrue(e instanceof  IllegalStateException);
        }
        try {
            messageBus.awaitMessage(eventHandlerService);
            fail("Unregistered Microservice was received but no exception was sent");
        }
        catch (IllegalStateException | InterruptedException e) {
            assertTrue(e instanceof  IllegalStateException);
        }
        messageSenderService = new ExampleMessageSenderService("messageSenderService", new String[]{"event"});
        event = new ExampleEvent(messageSenderService.getName());
        messageBus.register(eventHandlerService);
        messageBus.subscribeEvent(event.getClass(), eventHandlerService);
        Message message = null;
        try {
            message = messageBus.awaitMessage(eventHandlerService);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(message);
        assertEquals(message, event);
    }
}