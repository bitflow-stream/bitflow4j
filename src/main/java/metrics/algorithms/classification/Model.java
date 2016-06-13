package metrics.algorithms.classification;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by anton on 4/23/16.
 * <p>
 * Container for arbitrary objects representing trainable models of classification algorithms.
 * The (online/offline) learning side should set and periodically update the model, while the
 * predicting side should access the model through {@link #getModel()}.
 * <br/>
 * This has two purposes:
 * <ul>
 * <li>Block the model user, until the model is available</li>
 * <li>Synchronize access to the model object, even if it is updated in-place</li>
 * </ul>
 */
public class Model<T extends Serializable> {

    private IOException modelProducerException = null;
    private T model;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public interface ModelUser<T extends Serializable> {
        void useModel(T model) throws IOException;
    }

    public interface ModelUpdater<T extends Serializable> {
        T updateModel(T model);
    }

    public void useModel(ModelUser<T> user) throws IOException {
        T model;
        synchronized (this) {
            model = getModel();
            lock.readLock().lock();
        }
        RuntimeException rt = null;
        IOException io = null;
        try {
            user.useModel(model);
        } catch (IOException e) {
            io = e;
        } catch (RuntimeException e) {
            rt = e;
        } finally {
            lock.readLock().unlock();
        }
        if (io != null) {
            throw io;
        }
        if (rt != null) {
            throw rt;
        }
    }

    /**
     * This should only be used when there is no online learning algorithm that is expected
     * to periodically update the model. If there is, use {@link #useModel(ModelUser)} instead.
     */
    public T getModel() throws IOException {
        synchronized (this) {
            while (model == null) {
                try {
                    checkModelProducerException();
                    wait();
                } catch (InterruptedException e) {
                }
            }
            checkModelProducerException();
            return model;
        }
    }

    /**
     * Should be used for initially setting the model, or when the updated model is a new object.
     * If the model object is updated in-place, use {@link #updateModel(ModelUpdater)} instead.
     */
    public void setModel(T model) {
        synchronized (this) {
            lock.writeLock().lock();
            try {
                this.model = model;
                this.notifyAll();
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    public void updateModel(ModelUpdater<T> updater) {
        synchronized (this) {
            lock.writeLock().lock();
            RuntimeException exc = null;
            try {
                model = updater.updateModel(model);
            } catch (RuntimeException t) {
                exc = t;
            } finally {
                lock.writeLock().unlock();
            }
            if (exc != null) {
                throw exc;
            }
        }
    }

    public void modelProducerFailed(IOException exception) {
        synchronized (this) {
            model = null;
            modelProducerException = exception;
            notifyAll();
        }
    }

    private boolean checkModelProducerException() throws IOException {
        if (modelProducerException != null) {
            throw modelProducerException;
        }
        return true;
    }

}
