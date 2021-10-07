package org.yafox.muse.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.yafox.muse.dto.Student;
import org.yafox.muse.util.BeanUtil;

public class BeanUtilTest {

    @Test
    public void testBean() throws Exception {
        Student student = new Student();
        BeanUtil.set(student, "name", "zhangsan");
        BeanUtil.set(student, "age", 3);
        BeanUtil.set(student, "sex", true);
        
        assertEquals("zhangsan", student.getName());
        assertEquals(3, student.getAge());
        assertEquals(true, student.isSex());
        assertEquals("zhangsan", BeanUtil.get(student, "name"));
        assertEquals(3, BeanUtil.get(student, "age"));
        assertEquals(true, BeanUtil.get(student, "sex"));
    }
    
    @Test
    public void testArray() throws Exception {
        String[] strs = new String[5];
        BeanUtil.set(strs, 1, "a");
        BeanUtil.set(strs, 8, "a");
        assertEquals("a", strs[1]);
        assertNull(strs[0]);
    }
    
    @Test
    public void testCopy() throws Exception {
        Student source = new Student();
        source.setName("zhangsan");
        source.setSn("001");
        
        Student target = new Student();
        target.setAge(29);
        
        BeanUtil.copy(source, target);
        
        assertEquals(0, target.getAge());
        assertEquals("zhangsan", target.getName());
        assertEquals("001", target.getSn());
    }

}
