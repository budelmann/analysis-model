package edu.hm.hafner.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

/**
 * Abstract Test Pattern fuer Null-Safe List Implementierungen.
 *
 * @author budelmann
 */
abstract class NullSafeListTest extends ListTest{

    @Test
    void addSeperateValues() {
        List<Integer> sut = create(0);
        Integer first = 1;
        Integer second = 2;
        assertThat(sut).isEmpty();
        sut.add(first);
        sut.add(1, second);
        assertThat(sut).isNotEmpty();
        assertThat(sut).containsOnly(first, second);
    }

    @Test
    void addAllCollections() {
        List<Integer> sut = create(0);
        List<Integer> coll = Arrays.asList(1,2,3);

        sut.addAll(coll);
        assertThat(sut).hasSize(3);
        assertThat(sut).containsExactly(1,2,3);
        sut.clear();
        assertThat(sut).isEmpty();

        sut.add(0, 4);
        sut.addAll(1, coll);
        assertThat(sut).containsExactly(4,1,2,3);
    }

    @Test
    void setTestAndBomb() {
        List<Integer> sut = create(0);
        List<Integer> coll = Arrays.asList(1,2,3);

        sut.addAll(coll);
        sut.set(1,4);
        assertThat(sut).containsExactly(1,4,3);
        assertThatThrownBy(() -> sut.set(0, null)).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> sut.set(-1, null)).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void addNullBomb() {
        List<Integer> sut = create(0);
        assertThatThrownBy(() -> sut.add(null)).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> sut.add(0, null)).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void addAllCollectionContainingNullBomb() {
        List<Integer> sut = create(0);
        assertThatThrownBy(() -> sut.addAll(Arrays.asList(1, 2, null))).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> sut.addAll(0, Arrays.asList(1, 2, null))).isExactlyInstanceOf(NullPointerException.class);
    }

    @Test
    void addNPEBeforeOutOfBoundsBomb() {
        List<Integer> sut = create(0);
        assertThatThrownBy(() -> sut.add(-1, 0)).isExactlyInstanceOf(IndexOutOfBoundsException.class);
        assertThatThrownBy(() -> sut.addAll(-1, Collections.emptyList())).isExactlyInstanceOf(IndexOutOfBoundsException.class);

        assertThatThrownBy(() -> sut.add(-1, null)).isExactlyInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> sut.addAll(-1, Arrays.asList(1, null))).isExactlyInstanceOf(NullPointerException.class);
    }


    /**
     * Tests the class {@link NullSafeList}.
     *
     * @author budelmann
     */
    static class NullSafeDecoratorTest extends NullSafeListTest {
        @Override
        protected List<Integer> create(final int numberOfInitialElements) {
            return new NullSafeList<>(new ArrayList<>(Stream.iterate(0, t -> t+1).limit(numberOfInitialElements).collect(
                    Collectors.toList())));
        }

        @Test
        void FactoryDefaultConstructor() {
            List<Integer> sut = new LinkedList<>(Arrays.asList(1,2,3,4,5));
            Collection<Integer> coll = Collections.checkedCollection(
                    Collections.synchronizedList(
                            NullSafeCollections.nullSafeList(sut)), Integer.class);
            assertThat(coll).hasSize(5);

            Collection<Integer> coll2 = Collections.checkedCollection(
                    Collections.synchronizedList(
                            NullSafeCollections.nullSafeList(new ArrayList<>(), Arrays.asList(1,2,3))), Integer.class);
            assertThat(coll2).containsExactly(1,2,3);

            Collection<Integer> coll3 = Collections.checkedCollection(
                    Collections.synchronizedList(
                            NullSafeCollections.nullSafeList(new ArrayList<>(), 10)), Integer.class);
            assertThat(coll3).isEmpty();
        }
    }


    /**
     * Tests the class {@link NullSafeListInheritance}.
     *
     * @author budelmann
     */
    static class NullSafeInheritanceTest extends NullSafeListTest {
        @Override
        protected List<Integer> create(final int numberOfInitialElements) {
            List<Integer> list = new NullSafeListInheritance<>();
            list.addAll(Stream.iterate(0, t -> t+1).limit(numberOfInitialElements).collect(
                    Collectors.toList()));
            return list;
        }
    }
}