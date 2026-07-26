package de.marcschuler.onyxserver;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@OnyxTest
class UtilTest {

    @Test
    void testReorderSimple(){
        var list = new ArrayList<>(List.of("a","b"));
        Util.reorder(list,0,1);
        assertEquals(List.of("b","a"),list);
    }

    @Test
    void testReorder(){
        var list = new ArrayList<>(List.of("a","b","c","d"));
        Util.reorder(list,2,0);
        assertEquals(List.of("c","a","b","d"),list);
    }

}