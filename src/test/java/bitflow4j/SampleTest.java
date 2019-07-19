package bitflow4j;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleTest {

    @Test
    public void testResolveTagTemplate() {
        Sample s = new Sample(null, null, (Date) null);
        s.setTag("x", "y");
        s.setTag("a", "b");
        assertEquals("Hello y, it's b, , $} ${", s.resolveTagTemplate("Hello ${x}, it's ${a}, ${xxxx}, $} ${"));
    }

}