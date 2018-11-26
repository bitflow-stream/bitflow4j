package bitflow4j.window;

import bitflow4j.Sample;

/**
 * Created by anton on 11.01.17.
 */
public class SynchronizedSampleWindow {

    public final int maxSize;
    private int size = 0;
    private Link first = null;
    private Link last = null;

    public SynchronizedSampleWindow(int size) {
        this.maxSize = size;
    }

    public void push(Sample sample) {
        synchronized (this) {
            if (size == 0) {
                first = last = new Link(sample);
                size++;
            } else {
                Link next = new Link(sample);
                last.setNext(next);
                last = next;
                if (size >= maxSize) {
                    first = first.next;
                } else {
                    size++;
                }
            }
            notifyAll();
        }
    }

    public Link tryHead() {
        return first;
    }

    public int size() {
        return size;
    }

    public synchronized Link head() {
        while (first == null) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        return first;
    }

    public static class Link {

        public final Sample sample;
        private Link next = null;

        Link(Sample sample) {
            this.sample = sample;
        }

        public Link tryNext() {
            return next;
        }

        public Link next() {
            if (next != null) {
                return next;
            }
            synchronized (this) {
                while (next == null) {
                    try {
                        wait();
                    } catch (InterruptedException ignored) {
                    }
                }
                return next;
            }
        }

        synchronized void setNext(Link next) {
            this.next = next;
            notifyAll();
        }

    }

}
