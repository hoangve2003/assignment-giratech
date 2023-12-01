package com.example.productmanager.TestingController;

import com.example.productmanager.controller.CategoryController;
import com.example.productmanager.dto.CategoryDTO;
import com.example.productmanager.exception.CategoryNotFoundException;
import com.example.productmanager.model.Category;
import com.example.productmanager.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@WebMvcTest(controllers = CategoryController.class)
//@RequiredArgsConstructor
class CategoryControllerTests {

    private static final String END_POINT_PATH = "/category";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService service;

    @Test
    void testPageCategoryReturn404() throws Exception {
        Mockito.when(service.paginationCategories(Mockito.anyInt(), Mockito.anyInt())).thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(MockMvcRequestBuilders.get(END_POINT_PATH + "/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());

        Mockito.verify(service, Mockito.times(1)).paginationCategories(Mockito.anyInt(), Mockito.anyInt());
    }

    @Test
    void testPaginationCategoriesReturn200() throws Exception {
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(Category.builder().id(UUID.randomUUID()).name("name").description("des").createdAt(new Date()).active(true).build());
        Page<Category> page = new PageImpl<>(categoryList);

        Mockito.when(service.paginationCategories(Mockito.anyInt(), Mockito.anyInt())).thenReturn(ResponseEntity.ok(page));

        mockMvc.perform(MockMvcRequestBuilders.get(END_POINT_PATH + "/all"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Mockito.verify(service, Mockito.times(1)).paginationCategories(Mockito.anyInt(), Mockito.anyInt());

    }

    @Test
    void testAddCategoryReturn400() throws Exception {
        String requestURI = END_POINT_PATH + "/add";
        Category category = new Category();
        category.setName("");
        category.setDescription("");
        String requestBody = objectMapper.writeValueAsString(category);
        mockMvc.perform(MockMvcRequestBuilders.post(requestURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testAddCategoryReturn201() throws Exception {
        String requestURI = END_POINT_PATH + "/add";

        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Hoang").description("Fine")
                .createdAt(new Date())
                .active(true)
                .build();

        CategoryDTO dto = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .active(category.getActive())
                .build();

        Mockito.when(service.addCategory(dto)).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(category));

        String requestBody = objectMapper.writeValueAsString(category);

        mockMvc.perform(MockMvcRequestBuilders.post(requestURI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetCategoryByIdShouldReturn404() throws Exception {
        UUID id = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/" + id;

        Mockito.when(service.getCategoryById(id)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND)).thenThrow(CategoryNotFoundException.class);


        mockMvc.perform(MockMvcRequestBuilders.get(requestUri)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testGetCategoryByIdShouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/" + id;

        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Hoang").description("Fine")
                .createdAt(new Date())
                .active(true)
                .build();

        String request = objectMapper.writeValueAsString(category);

        Mockito.when(service.getCategoryById(id)).thenReturn(ResponseEntity.ok(category));

        mockMvc.perform(MockMvcRequestBuilders.get(requestUri)
                .contentType(MediaType.APPLICATION_JSON).content(request))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateCategory_CategoryNotFound() throws Exception {
        UUID categoryId = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/update/" + categoryId;
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Hoang").description("Fine")
                .createdAt(new Date())
                .active(true)
                .build();

        CategoryDTO dto = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .active(category.getActive())
                .build();

        Mockito.when(service.updateCategory(categoryId, dto)).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND)).thenThrow(CategoryNotFoundException.class);
        String requestBody = objectMapper.writeValueAsString(category);

        mockMvc.perform(MockMvcRequestBuilders.put(requestUri).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateCategory_CategoryBadRequest() throws Exception {
        UUID categoryId = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/update/" + categoryId;
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("").description("")
                .createdAt(null)
                .active(null)
                .build();

        CategoryDTO dto = CategoryDTO.builder()
                .id(UUID.randomUUID())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .active(category.getActive())
                .build();

        Mockito.when(service.updateCategory(categoryId, dto)).thenReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
        String requestBody = objectMapper.writeValueAsString(category);

        mockMvc.perform(MockMvcRequestBuilders.put(requestUri).contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testUpdateCategory() throws Exception {
        UUID categoryId = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/update/" + categoryId;
        Category category = Category.builder()
                .id(UUID.randomUUID())
                .name("Hoang").description("Fine")
                .createdAt(new Date())
                .active(true)
                .build();

        CategoryDTO dto = CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .active(category.getActive())
                .build();

        Mockito.when(service.updateCategory(categoryId, dto)).thenReturn(ResponseEntity.status(HttpStatus.OK).body(category));

        String requestBody = objectMapper.writeValueAsString(category);

        mockMvc.perform(MockMvcRequestBuilders.put(requestUri)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteCategoryReturn404() throws Exception {
        UUID categoryId = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/delete/" + categoryId;
        Mockito.when(service.deleteCategory(categoryId)).thenReturn(ResponseEntity.status(HttpStatus.NOT_FOUND).build()).thenThrow(CategoryNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.delete(requestUri))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteCategoryReturn204() throws Exception {
        UUID categoryId = UUID.randomUUID();
        String requestUri = END_POINT_PATH + "/delete/" + categoryId;
        Mockito.when(service.deleteCategory(categoryId)).thenReturn(ResponseEntity.status(HttpStatus.NO_CONTENT).build());
        mockMvc.perform(MockMvcRequestBuilders.delete(requestUri))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
    }


}

