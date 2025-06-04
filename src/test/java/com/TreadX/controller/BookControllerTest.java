package com.TreadX.controller;

import com.TreadX.book.Enum.BookCategoryEnum;
import com.TreadX.book.controller.BookController;
import com.TreadX.book.request.BookRequest;
import com.TreadX.book.response.BookInfoResponse;
import com.TreadX.book.response.BookInfoResponseWrapper;
import com.TreadX.book.service.BookService;
import com.TreadX.utils.request.PaginationRequest;
import com.TreadX.utils.response.PaginationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void getAllBooks_ShouldReturnBooks() throws Exception {
        List<BookInfoResponse> books = List.of(
                BookInfoResponse.builder()
                        .id(UUID.randomUUID())
                        .title("Spring in Action")
                        .author("Craig Walls")
                        .isbn("9781617294945")
                        .publicationYear(LocalDate.now())
                        .publisher("Manning")
                        .category(BookCategoryEnum.valueOf("Programming"))
                        .language("English")
                        .numberOfPages(520)
                        .build(),
                BookInfoResponse.builder()
                        .id(UUID.randomUUID())
                        .title("Effective Java")
                        .author("Joshua Bloch")
                        .isbn("9780134686097")
                        .publicationYear(LocalDate.now())
                        .publisher("Manning")
                        .category(BookCategoryEnum.valueOf("Programming"))
                        .language("English")
                        .numberOfPages(416)
                        .build()
        );

        PaginationResponse paginationResponse = PaginationResponse.builder()
                .page(1)
                .perPage(books.size())
                .total(100)
                .build();

        BookInfoResponseWrapper responseWrapper = BookInfoResponseWrapper.builder()
                .bookInfo(books)
                .pagination(paginationResponse)
                .build();

        when(bookService.getAllBooks(any(PaginationRequest.class))).thenReturn(responseWrapper);

        mockMvc.perform(get("/api/books?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookService).getAllBooks(any(PaginationRequest.class));
    }

    @Test
    void getBookById_ShouldReturnBook() throws Exception {
        UUID bookId = UUID.randomUUID();
        BookInfoResponse book = BookInfoResponse.builder()
                .id(bookId)
                .title("Spring in Action")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(LocalDate.now())
                .publisher("Manning")
                .category(BookCategoryEnum.valueOf("Programming"))
                .language("English")
                .numberOfPages(520)
                .build();

        when(bookService.getBookInfoById(bookId)).thenReturn(book);

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookService).getBookInfoById(bookId);
    }

    @Test
    void addBook_ShouldCreateBook() throws Exception {
        BookRequest request = BookRequest.builder()
                .title("Spring in Action")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(LocalDate.parse("2020-06-01"))
                .publisher("Manning")
                .category("Programming")
                .language("English")
                .numberOfPages(520)
                .build();

        BookInfoResponse createdBook = BookInfoResponse.builder()
                .id(UUID.randomUUID())
                .title("Spring in Action")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(LocalDate.parse("2020-06-01"))
                .publisher("Manning")
                .category(BookCategoryEnum.valueOf("Programming"))
                .language("English")
                .numberOfPages(520)
                .build();

        when(bookService.addBook(any(BookRequest.class))).thenReturn(createdBook);

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.body.title").value("Spring in Action"))
                .andExpect(jsonPath("$.body.author").value("Craig Walls"));

        verify(bookService).addBook(any(BookRequest.class));
    }

    @Test
    void updateBook_ShouldUpdateBook() throws Exception {
        UUID bookId = UUID.randomUUID();
        BookRequest request = BookRequest.builder()
                .title("Spring Boot Essentials")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(LocalDate.now())
                .publisher("Manning")
                .category("Programming")
                .language("English")
                .numberOfPages(550)
                .build();

        BookInfoResponse updatedBook = BookInfoResponse.builder()
                .id(bookId)
                .title("Spring Boot Essentials")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(LocalDate.now())
                .publisher("Manning")
                .category(BookCategoryEnum.valueOf("Programming"))
                .language("English")
                .numberOfPages(550)
                .build();

        when(bookService.updateBook(any(BookRequest.class), eq(bookId))).thenReturn(updatedBook);

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookService).updateBook(any(BookRequest.class), eq(bookId));
    }

    @Test
    void deleteBook_ShouldDeleteBook() throws Exception {
        UUID bookId = UUID.randomUUID();

        when(bookService.deleteBook(bookId)).thenReturn(true);

        mockMvc.perform(delete("/api/books/{id}", bookId))
                .andExpect(status().isOk());

        verify(bookService).deleteBook(bookId);
    }

    @Test
    public void testCachingEffect() {
        // Initial call without caching
        long startTime = System.currentTimeMillis();
        BookInfoResponseWrapper books = bookService.getAllBooks(PaginationRequest.builder().size(10).page(1).build()); // First call
        long endTime = System.currentTimeMillis();
        System.out.println("First call duration: " + (endTime - startTime) + " ms");

        // Call again to test caching
        long startTimeCached = System.currentTimeMillis();
        BookInfoResponseWrapper booksCached = bookService.getAllBooks(PaginationRequest.builder().size(10).page(1).build()); // Cached call
        long endTimeCached = System.currentTimeMillis();
        System.out.println("Second call duration (cached): " + (endTimeCached - startTimeCached) + " ms");
    }

}
