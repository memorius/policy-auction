package net.retakethe.policyauction.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Nick Clarke
 */
public final class Functional {

    public static final class SkippedElementException extends Exception {
        private static final long serialVersionUID = 0L;
    }

    public interface Converter<A, B> {
        B convert(A input);
    }

    public interface Filter<A, B> {
        /**
         * @return the output item for the input item, if passed by the filter
         * @throws SkippedElementException if there is no output for this input item
         */
        B filter(A input) throws SkippedElementException;
    }

    public static <A, B> List<B> map(List<A> input, Converter<A, B> converter) {
        List<B> output = new ArrayList<B>(input.size());
        for (A a : input) {
            output.add(converter.convert(a));
        }
        return output;
    }

    public static <A, B> List<B> filter(List<A> input, Filter<A, B> filter) {
        List<B> output = new LinkedList<B>();
        for (A a : input) {
            try {
                output.add(filter.filter(a));
            } catch (SkippedElementException e) {
                continue;
            }
        }
        return output;
    }
}
