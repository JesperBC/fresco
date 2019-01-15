package dk.alexandra.fresco.lib.math.integer.logical;

import dk.alexandra.fresco.framework.Application;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThread;
import dk.alexandra.fresco.framework.TestThreadRunner.TestThreadFactory;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.value.OInt;
import dk.alexandra.fresco.framework.value.SInt;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;

public class LogicalOperationsTests {

  public static class TestXorKnown<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<BigInteger> left = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.ZERO);
        private final List<BigInteger> right = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ZERO);

        @Override
        public void test() {
          Application<List<DRes<BigInteger>>, ProtocolBuilderNumeric> app =
              root -> {
                DRes<List<DRes<SInt>>> leftClosed = root.numeric().knownAsDRes(left);
                List<OInt> rightOInts = root.getOIntFactory().fromBigInteger(right);
                DRes<List<DRes<SInt>>> xored = root.logical()
                    .pairWiseXorKnown(() -> rightOInts, leftClosed);
                return root.collections().openList(xored);
              };
          List<BigInteger> actual = runApplication(app).stream().map(DRes::out)
              .collect(Collectors.toList());
          List<BigInteger> expected = Arrays.asList(
              BigInteger.ZERO,
              BigInteger.ONE,
              BigInteger.ONE,
              BigInteger.ZERO
          );
          Assert.assertEquals(expected, actual);
        }
      };
    }
  }

  public static class TestAndKnown<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<BigInteger> left = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.ZERO);
        private final List<BigInteger> right = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ZERO);

        @Override
        public void test() {
          Application<List<DRes<BigInteger>>, ProtocolBuilderNumeric> app =
              root -> {
                DRes<List<DRes<SInt>>> leftClosed = root.numeric().knownAsDRes(left);
                List<OInt> rightOInts = root.getOIntFactory().fromBigInteger(right);
                DRes<List<DRes<SInt>>> anded = root.logical()
                    .pairWiseAndKnown(() -> rightOInts, leftClosed);
                return root.collections().openList(anded);
              };
          List<BigInteger> actual = runApplication(app).stream().map(DRes::out)
              .collect(Collectors.toList());
          List<BigInteger> expected = Arrays.asList(
              BigInteger.ONE,
              BigInteger.ZERO,
              BigInteger.ZERO,
              BigInteger.ZERO
          );
          Assert.assertEquals(expected, actual);
        }
      };
    }
  }

  public static class TestAnd<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<BigInteger> left = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.ZERO);
        private final List<BigInteger> right = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ZERO);

        @Override
        public void test() {
          Application<List<DRes<BigInteger>>, ProtocolBuilderNumeric> app =
              root -> {
                DRes<List<DRes<SInt>>> leftClosed = root.numeric().knownAsDRes(left);
                DRes<List<DRes<SInt>>> rightClosed = root.numeric().knownAsDRes(right);
                DRes<List<DRes<SInt>>> anded = root.logical()
                    .pairWiseAnd(leftClosed, rightClosed);
                return root.collections().openList(anded);
              };
          List<BigInteger> actual = runApplication(app).stream().map(DRes::out)
              .collect(Collectors.toList());
          List<BigInteger> expected = Arrays.asList(
              BigInteger.ONE,
              BigInteger.ZERO,
              BigInteger.ZERO,
              BigInteger.ZERO
          );
          Assert.assertEquals(expected, actual);
        }
      };
    }
  }

  public static class TestOr<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<BigInteger> left = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.ZERO);
        private final List<BigInteger> right = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ZERO);

        @Override
        public void test() {
          Application<List<DRes<BigInteger>>, ProtocolBuilderNumeric> app =
              root -> {
                DRes<List<DRes<SInt>>> leftClosed = root.numeric().knownAsDRes(left);
                DRes<List<DRes<SInt>>> rightClosed = root.numeric().knownAsDRes(right);
                DRes<List<DRes<SInt>>> anded = root.logical().pairWiseOr(leftClosed, rightClosed);
                return root.collections().openList(anded);
              };
          List<BigInteger> actual = runApplication(app).stream().map(DRes::out)
              .collect(Collectors.toList());
          List<BigInteger> expected = Arrays.asList(
              BigInteger.ONE,
              BigInteger.ONE,
              BigInteger.ONE,
              BigInteger.ZERO
          );
          Assert.assertEquals(expected, actual);
        }
      };
    }
  }

  public static class TestOrNeighbors<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<BigInteger> list = Arrays.asList(
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ONE,
            BigInteger.ZERO,
            BigInteger.ZERO);

        @Override
        public void test() {
          Application<List<DRes<BigInteger>>, ProtocolBuilderNumeric> app =
              root -> {
                DRes<List<DRes<SInt>>> leftClosed = root.numeric().knownAsDRes(list);
                return root.seq(seq -> {
                  DRes<List<DRes<SInt>>> orred = seq.logical().orNeighbors(leftClosed.out());
                  return seq.collections().openList(orred);
                });
              };
          List<BigInteger> actual = runApplication(app).stream().map(DRes::out)
              .collect(Collectors.toList());
          List<BigInteger> expected = Arrays.asList(
              BigInteger.ONE,
              BigInteger.ONE,
              BigInteger.ZERO
          );
          Assert.assertEquals(expected, actual);
        }
      };
    }
  }

  public static class TestOrList<ResourcePoolT extends ResourcePool> extends
      TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {
        private final List<BigInteger> input1 = Arrays.asList(BigInteger.ZERO,
            BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ONE);
        private final List<BigInteger> input2 = Arrays.asList(BigInteger.ZERO,
            BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO, BigInteger.ZERO);
        private final List<BigInteger> input3 = Arrays.asList(BigInteger.ZERO,
            BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ZERO);
        private final List<BigInteger> input4 = Arrays.asList(BigInteger.ZERO,
            BigInteger.ZERO, BigInteger.ONE, BigInteger.ZERO, BigInteger.ONE);

        @Override
        public void test() {
          List<List<BigInteger>> inputLists = Arrays.asList(input1, input2,
              input3, input4);
          List<BigInteger> expectedOutput = Arrays.asList(BigInteger.ONE,
              BigInteger.ZERO, BigInteger.ONE, BigInteger.ONE);

          Application<List<BigInteger>, ProtocolBuilderNumeric> app = root -> {
            List<DRes<BigInteger>> results = inputLists.stream().map(
                current -> root.numeric().open(root.logical().orOfList(root.numeric().knownAsDRes(
                    current)))).collect(Collectors.toList());
            return () -> results.stream().map(DRes::out).collect(Collectors
                .toList());
          };
          List<BigInteger> actual = runApplication(app);
          Assert.assertArrayEquals(expectedOutput.toArray(), actual.toArray());
        }
      };
    }
  }

  public static class TestNot<ResourcePoolT extends ResourcePool>
      extends TestThreadFactory<ResourcePoolT, ProtocolBuilderNumeric> {

    @Override
    public TestThread<ResourcePoolT, ProtocolBuilderNumeric> next() {

      return new TestThread<ResourcePoolT, ProtocolBuilderNumeric>() {

        @Override
        public void test() {
          Application<List<DRes<BigInteger>>, ProtocolBuilderNumeric> app =
              root -> {
                DRes<SInt> notOne = root.logical().not(root.numeric().known(BigInteger.ONE));
                DRes<SInt> notZero = root.logical().not(root.numeric().known(BigInteger.ZERO));
                List<DRes<SInt>> notted = Arrays.asList(notOne, notZero);
                return root.collections().openList(() -> notted);
              };
          List<BigInteger> actual = runApplication(app).stream().map(DRes::out)
              .collect(Collectors.toList());
          List<BigInteger> expected = Arrays.asList(
              BigInteger.ZERO,
              BigInteger.ONE
          );
          Assert.assertEquals(expected, actual);
        }
      };
    }
  }

}
