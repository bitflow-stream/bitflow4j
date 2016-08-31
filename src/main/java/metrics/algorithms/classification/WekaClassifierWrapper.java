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
//        if(instance == null) System.out.println("WTF null instance passed to evaluator....");
//        int index = instances.indexOf(instance);
//        if(index > 0) return instances.get(index).classValue();
//        else throw new Exception("cannot classify an instance, that was not trained (this class is for evaluation only)");
        return this.getClassForInstance(instance);
    }

//    @Override
    private double getClassForInstance(Instance instance) throws IllegalArgumentException {
        CITInstance citInstance = null;
        try {
            citInstance = (CITInstance) instance;
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Instance must be subclass of CITInstance");
        }
        //TODO typecheck later
        for (Instance inst : this.instances){
            int index = ((CITInstance)inst).getIndex();
            if(index == citInstance.getIndex()){
                return inst.classValue();
            }
        }
        throw new IllegalArgumentException("Unknown instance, cannot classify");
    }

    @Override
    public double[] distributionForInstance(Instance instance) throws Exception {
        return super.distributionForInstance(instance);
    }
}
