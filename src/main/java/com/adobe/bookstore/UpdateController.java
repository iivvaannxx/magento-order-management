package com.adobe.bookstore;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/** Defines the controller for handling SSE updates. */
@RestController
@RequestMapping("/api/notifications")
public class UpdateController {

  /** The list of SSE emitters. */
  private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

  /** Returns an SSE emitter for the stock updates. */
  @GetMapping
  public SseEmitter getEmitter() {

    SseEmitter emitter = new SseEmitter();
    emitters.add(emitter);

    emitter.onCompletion(() -> emitters.remove(emitter));
    emitter.onTimeout(() -> emitters.remove(emitter));

    return emitter;
  }

  /**
   * Sends a stock update event to all connected clients.
   *
   * @param bookId The identifier of the book.
   * @param newStock The new stock of the book.
   */
  public void sendStockUpdate(String bookId, int newStock) {
    emitters.forEach(
        emitter -> {
          try {
            emitter.send(
                SseEmitter.event()
                    .name("stock_update")
                    .data(Map.of("bookId", bookId, "newStock", newStock)));
          } catch (IOException e) {
            emitter.complete();
            emitters.remove(emitter);
          }
        });
  }

  /**
   * Sends an order update event to all connected clients. This goes for both new orders and deleted
   * orders.
   *
   * @param orderId The identifier of the order.
   */
  public void sendOrderUpdate(String orderId) {
    emitters.forEach(
        emitter -> {
          try {
            emitter.send(SseEmitter.event().name("order_update").data(Map.of("orderId", orderId)));
          } catch (IOException e) {
            emitter.complete();
            emitters.remove(emitter);
          }
        });
  }

  /**
   * Sends a heartbeat event to all connected clients every 30 seconds. This is used to keep the
   * connection alive.
   */
  @Scheduled(fixedRate = 30000) // Send heartbeat every 30 seconds
  public void sendHeartbeat() {
    emitters.forEach(
        emitter -> {
          try {
            emitter.send(SseEmitter.event().name("heartbeat").data("ping"));
          } catch (IOException e) {
            emitter.complete();
            emitters.remove(emitter);
          }
        });
  }
}
