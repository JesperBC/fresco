package dk.alexandra.fresco.suite.crt;

import dk.alexandra.fresco.framework.ProtocolEvaluator;
import dk.alexandra.fresco.framework.TestThreadRunner;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.builder.numeric.field.BigIntegerFieldDefinition;
import dk.alexandra.fresco.framework.builder.numeric.field.FieldDefinition;
import dk.alexandra.fresco.framework.builder.numeric.field.MersennePrimeFieldDefinition;
import dk.alexandra.fresco.framework.configuration.NetworkConfiguration;
import dk.alexandra.fresco.framework.configuration.NetworkUtil;
import dk.alexandra.fresco.framework.network.socket.SocketNetwork;
import dk.alexandra.fresco.framework.sce.SecureComputationEngine;
import dk.alexandra.fresco.framework.sce.SecureComputationEngineImpl;
import dk.alexandra.fresco.framework.sce.evaluator.BatchEvaluationStrategy;
import dk.alexandra.fresco.framework.sce.evaluator.BatchedProtocolEvaluator;
import dk.alexandra.fresco.framework.sce.evaluator.EvaluationStrategy;
import dk.alexandra.fresco.logging.DefaultPerformancePrinter;
import dk.alexandra.fresco.logging.PerformanceLogger;
import dk.alexandra.fresco.logging.PerformanceLoggerCountingAggregate;
import dk.alexandra.fresco.logging.PerformancePrinter;
import dk.alexandra.fresco.suite.ProtocolSuiteNumeric;
import dk.alexandra.fresco.suite.crt.datatypes.resource.CRTDataSupplier;
import dk.alexandra.fresco.suite.crt.datatypes.resource.CRTDummyDataSupplier;
import dk.alexandra.fresco.suite.crt.datatypes.resource.CRTResourcePool;
import dk.alexandra.fresco.suite.crt.datatypes.resource.CRTResourcePoolImpl;
import dk.alexandra.fresco.suite.crt.suites.DummyProtocolSupplier;
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticResourcePool;
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticResourcePoolImpl;
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticSInt;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Abstract class which handles a lot of boiler plate testing code. This makes running a single test
 * using different parameters quite easy.
 */
public class AbstractDummyCRTTest {

  protected static final FieldDefinition DEFAULT_FIELD_LEFT =
      MersennePrimeFieldDefinition.find(64);
  protected static final FieldDefinition DEFAULT_FIELD_RIGHT = new BigIntegerFieldDefinition(
      new BigInteger(128 + 40, new Random(1234)).nextProbablePrime());

  public void runTest(
      TestThreadRunner.TestThreadFactory<CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>, ProtocolBuilderNumeric> f,
      EvaluationStrategy evalStrategy, int noOfParties) {

    List<Integer> ports = new ArrayList<>(noOfParties);
    for (int i = 1; i <= noOfParties; i++) {
      ports.add(9000 + i * (noOfParties - 1));
    }

    Map<Integer, NetworkConfiguration> netConf =
        NetworkUtil.getNetworkConfigurations(ports);
    Map<Integer,
        TestThreadRunner.TestThreadConfiguration<
            CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>,
            ProtocolBuilderNumeric>
        > conf = new HashMap<>();

    for (int playerId : netConf.keySet()) {

      BatchEvaluationStrategy<CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>> batchEvaluationStrategy =
          evalStrategy.getStrategy();
      DummyArithmeticResourcePool rpLeft = new DummyArithmeticResourcePoolImpl(playerId,
          noOfParties, DEFAULT_FIELD_LEFT);
      DummyArithmeticResourcePool rpRight = new DummyArithmeticResourcePoolImpl(playerId,
          noOfParties, DEFAULT_FIELD_RIGHT);

      ProtocolSuiteNumeric<CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>> ps =
          new CRTProtocolSuite<>(
              new DummyProtocolSupplier(new Random(0), DEFAULT_FIELD_LEFT),
              new DummyProtocolSupplier(new Random(1), DEFAULT_FIELD_RIGHT));
      ProtocolEvaluator<CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>> evaluator =
          new BatchedProtocolEvaluator<>(batchEvaluationStrategy, ps);

      NetworkConfiguration partyNetConf = netConf.get(playerId);
      SecureComputationEngine<CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>, ProtocolBuilderNumeric> sce =
          new SecureComputationEngineImpl<>(ps, evaluator);

      CRTDataSupplier dataSupplier = new CRTDummyDataSupplier(playerId, noOfParties,
          DEFAULT_FIELD_LEFT, DEFAULT_FIELD_RIGHT,
          x -> new DummyArithmeticSInt(DEFAULT_FIELD_LEFT.createElement(x)),
          y -> new DummyArithmeticSInt(DEFAULT_FIELD_RIGHT.createElement(y)));

      TestThreadRunner.TestThreadConfiguration<
          CRTResourcePool<DummyArithmeticResourcePool, DummyArithmeticResourcePool>,
          ProtocolBuilderNumeric> ttc =
          new TestThreadRunner.TestThreadConfiguration<>(sce,
              () -> new CRTResourcePoolImpl<>(playerId, noOfParties, dataSupplier, rpLeft, rpRight),
              () -> new SocketNetwork(partyNetConf));
      conf.put(playerId, ttc);
    }

    TestThreadRunner.run(f, conf);
  }

}