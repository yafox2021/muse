package org.yafox.muse.assign;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.yafox.muse.MockPallet;
import org.yafox.muse.dto.Book;
import org.yafox.muse.dto.Person;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class AssignmentBuilderTest {

    @Test
    public void test() throws Exception {
        MockPallet pallet = new MockPallet();
        pallet.setBasePath("assign/");
        pallet.addBeanType("bookName", StringEvaluation.class);
        pallet.addBeanType("authorName", StringEvaluation.class);
        pallet.addBeanType("age", IntegerEvaluation.class);
        
        String txt = pallet.getString("/assign/assign1.json");
        
        Gson gson = new Gson();
        
        JsonObject jsonConfig = gson.fromJson(txt, JsonObject.class);
        Assignment assignment = AssignmentBuilder.build(jsonConfig, pallet);
        
        assertNotNull(assignment);
        
        Book book = new Book();
        book.setAuthor(new Person());
        
        assignment.assign(book);
        
        assertEquals("book001", book.getName());
        assertEquals("author001", book.getAuthor().getName());
        
    }

}
