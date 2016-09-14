package metrics.algorithms.clustering;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import metrics.algorithms.clustering.clustering.BICOClusterer;
import moa.clusterers.AbstractClusterer;

/**
 * Created by malcolmx on 14.09.16.
 */
public class MOAUtil {
    private static Gson gson = new Gson();

    /**
     * Get an {@link AbstractClusterer} from its serialized json representation
     * @param json the json string
     * @return the {@link AbstractClusterer} or null if string could not be parsed
     */
    public static AbstractClusterer getClustererFromJSONString(String json){
//        AbstractClusterer clusterer = null;
        BICOClusterer clusterer = null;
        try {
            clusterer = gson.fromJson(json, new TypeToken<BICOClusterer>(){}.getType());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return (AbstractClusterer) clusterer.getModel();
    }
}
