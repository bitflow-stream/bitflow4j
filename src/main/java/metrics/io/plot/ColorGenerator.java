package metrics.io.plot;

import java.awt.*;

/**
 * Created by mwall on 19.04.16.
 */
public class ColorGenerator{


        private int[] color = {0, 0, 0};
        private int transparency;

        public ColorGenerator(){
            this.transparency=0;
        }

        public ColorGenerator(int transparency){
            this.transparency = transparency;
        }

        public Color getNextColor() {
            this.color[0] = (color[0] + 32) % 256;
            this.color[1] = (color[1] + 128) % 256;
            this.color[2] = (color[2] + 64) % 256;

            return new Color(color[0], color[1], color[2], 128);
        }
}
