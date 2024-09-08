package com.adobe.bookstore.bookstock;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.adobe.bookstore.bookstock.exceptions.NonExistentBookException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Slice Web MVC tests for the {@link BookStockResource} class. */
@WebMvcTest(BookStockResource.class)
public class BookStockResourceWebMvcTest {

  /** The instance of {@link MockMvc} used in the tests. */
  @Autowired private MockMvc mvc;

  /** The mocked instance of {@link BookStockService} used in the tests. */
  @MockBean private BookStockService bookStockService;

  /**
   * Tests that the {@link BookStockResource#getStockById(String)} method works correctly if the
   * book exists.
   */
  @Test
  public void getStockById_whenBookExists_shouldReturnBook() throws Exception {

    String bookId = "12345-67890";
    BookStock expectedBookStock = new BookStock(bookId, "Some Book", 10);
    when(bookStockService.getBookById(bookId)).thenReturn(expectedBookStock);

    // Mock the server response.
    mvc.perform(
            get(String.format("/api/books_stock/%s", bookId)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.isbn").value(bookId))
        .andExpect(jsonPath("$.title").value("Some Book"))
        .andExpect(jsonPath("$.stock").value(10));
  }

  /**
   * Tests that the {@link BookStockResource#getStockById(String)} method returns a 404 Not Found if
   * the book does not exist.
   */
  @Test
  public void getStockById_whenBookDoesNotExist_shouldReturnNotFound() throws Exception {

    String bookId = "12345-67890";
    when(bookStockService.getBookById(bookId)).thenThrow(new NonExistentBookException(bookId));

    // This time we have a Spring context running, so the exception handler is in the context.
    // It will catch the NonExistentBookException and return a 404 response.
    mvc.perform(
            get(String.format("/api/books_stock/%s", bookId)).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").value("Book does not exist"))
        .andExpect(jsonPath("$.bookId").value(bookId));
  }
}
