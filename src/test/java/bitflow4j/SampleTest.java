package bitflow4j;

import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class SampleTest {

    @Test
    public void testResolveTagTemplate() {
        Sample s = new Sample(null, null, (Date) null);
        s.setTag("x", "y");
        s.setTag("a", "b");
        Assert.assertEquals("Hello y, it's b, , $} ${", s.resolveTagTemplate("Hello ${x}, it's ${a}, ${xxxx}, $} ${"));
    }

}