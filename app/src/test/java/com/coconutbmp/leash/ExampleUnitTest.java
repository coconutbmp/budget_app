package com.coconutbmp.leash;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void incorrectEmail_noAt(){
        MainActivity main = new MainActivity();
        assertFalse(main.validateEmail("WrongEmail.com"));
    }

    @Test
    public void incorrectEmail_noDot(){
        MainActivity main = new MainActivity();
        assertFalse(main.validateEmail("WrongEmail@gmailcom"));
    }

    @Test
    public void correctEmail(){
        MainActivity main = new MainActivity();
        assertTrue(main.validateEmail("RightEmail@gmail.com"));
    }
}
