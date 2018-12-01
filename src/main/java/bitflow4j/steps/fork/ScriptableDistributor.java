package bitflow4j.steps.fork;

import bitflow4j.Pipeline;
import bitflow4j.misc.Pair;
import bitflow4j.misc.TreeFormatter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface ScriptableDistributor extends Distributor, TreeFormatter.FormattedNode {

    void setSubPipelines(Collection<Pair<String, Pipeline>> subPipelines);

    static Collection<Object> formattedSubPipelines(Collection<Pair<String, Pipeline>> subPipelines) {
        return subPipelines.stream().map(TitledPipeline::new).collect(Collectors.toList());
    }

    abstract class Default implements ScriptableDistributor {

        protected Collection<Pair<String, Pipeline>> subPipelines;
        protected List<String> availableKeys;

        @Override
        public void setSubPipelines(Collection<Pair<String, Pipeline>> subPipelines) {
            this.subPipelines = subPipelines;
            availableKeys = subPipelines.stream().map(Pair::getLeft).sorted().collect(Collectors.toList());
        }

        @Override
        public String toString() {
            return String.format("A %s (%s sub pipelines)", getClass().getSimpleName(), subPipelines.size());
        }

        @Override
        public Collection<Object> formattedChildren() {
            return formattedSubPipelines(subPipelines);
        }
    }

    class TitledPipeline implements TreeFormatter.FormattedNode {

        private final String title;
        private final Pipeline pipeline;

        public TitledPipeline(String title, Pipeline pipeline) {
            this.title = title;
            this.pipeline = pipeline;
        }

        public TitledPipeline(Pair<String, Pipeline> pair) {
            this.title = pair.getLeft();
            this.pipeline = pair.getRight();
        }

        @Override
        public String toString() {
            return title;
        }

        @Override
        public Collection<Object> formattedChildren() {
            return pipeline.formattedChildren();
        }

    }

}
