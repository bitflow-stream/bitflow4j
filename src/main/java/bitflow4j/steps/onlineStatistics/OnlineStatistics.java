package bitflow4j.steps.onlineStatistics;

public class OnlineStatistics {

    private long mN = 0L;
    private double mM = 0.0;
    private double mS = 0.0;

    public void push(double x) {
        ++mN;
        double nextM = mM + (x - mM) / mN;
        mS += (x - mM) * (x - nextM);
        mM = nextM;
    }

    public void remove(double x) {
        if (mN == 0L) {
            throw new IllegalStateException("Cannot unhandle after 0 samples.");
        }
        if (mN == 1L) {
            mN = 0L;
            mM = 0.0;
            mS = 0.0;
            return;
        }
        double mOld = (mN * mM - x) / (mN - 1L);
        mS -= (x - mM) * (x - mOld);
        mM = mOld;
        --mN;
    }

    public long numSamples() {
        return mN;
    }

    public double mean() {
        return mM;
    }

    public double variance() {
        return mN > 1 ? mS / mN : 0.0;
    }

    public double varianceUnbiased() {
        return mN > 1 ? mS / (mN - 1) : 0.0;
    }

    public double standardDeviation() {
        return Math.sqrt(variance());
    }

    public double standardDeviationUnbiased() {
        return Math.sqrt(varianceUnbiased());
    }

}