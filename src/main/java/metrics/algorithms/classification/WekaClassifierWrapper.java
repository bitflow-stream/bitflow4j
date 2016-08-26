package metrics.algorithms.classification;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

public class WekaClassifierWrapper extends AbstractClassifier {

    private Instances instances;
//        private Map<Double, Instance> instancesMap;

    public WekaClassifierWrapper(){
//            this.samples = new ArrayList<>();
        //TODO: check if we need stable order
//            this.instancesMap = new HashMap<>();
    }

    @Override
    public void buildClassifier(Instances instances) throws Exception {
        if(instances == null) throw new IllegalArgumentException("no instances provided, nothing to train");
        this.instances = instances;
//            for(Instance instance : instances){
//                instancesMap.put(instance.classValue(), instance);
//            }
    }

    @Override
    public double classifyInstance(Instance instance) throws Exception {
        int index = instances.indexOf(instance);
        if(index > 0) return instances.get(index).classValue();
        else throw new Exception("cannot classify an instance, that was not trained (this class is for evaluation only)");
    }

    @Override
    public double[] distributionForInstance(Instance instance) throws Exception {
        return super.distributionForInstance(instance);
    }
}
