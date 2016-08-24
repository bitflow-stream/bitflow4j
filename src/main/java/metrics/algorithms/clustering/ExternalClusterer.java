package metrics.algorithms.clustering;

import moa.clusterers.AbstractClusterer;
import moa.clusterers.clustream.Clustream;
import moa.clusterers.clustree.ClusTree;
import moa.clusterers.denstream.WithDBSCAN;
import moa.clusterers.outliers.AbstractC.AbstractC;
import moa.clusterers.outliers.Angiulli.ApproxSTORM;
import moa.clusterers.outliers.Angiulli.ExactSTORM;
import moa.clusterers.outliers.AnyOut.AnyOut;
import moa.clusterers.outliers.MCOD.MCOD;
import moa.clusterers.outliers.SimpleCOD.SimpleCOD;
import moa.clusterers.streamkm.StreamKM;

/**
 *
 * @author fschmidt
 */
public enum ExternalClusterer {
    CLUSTREAM(Clustream::new),
    CLUSTREE(ClusTree::new),
    DENSTREAM(WithDBSCAN::new),
    BICO(moa.clusterers.kmeanspm.BICO::new),
    STREAM_KMEANS(StreamKM::new),
    ABSTRACT_C(AbstractC::new),
    STORM_APPROX(ApproxSTORM::new),
    STORM_EXACT(ExactSTORM::new),
    ANY_OUT(AnyOut::new),
    MCOD(moa.clusterers.outliers.MCOD.MCOD::new),
    SCOD(SimpleCOD::new);



    private final ClustererFactory clusterer;

    ExternalClusterer(ClustererFactory clusterer) {
        this.clusterer = clusterer;
    }

    public AbstractClusterer newInstance() {
        return clusterer.make();
    }

    private interface ClustererFactory {
        AbstractClusterer make();
    }
}
