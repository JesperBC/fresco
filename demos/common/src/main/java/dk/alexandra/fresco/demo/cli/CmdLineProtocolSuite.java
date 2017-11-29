package dk.alexandra.fresco.demo.cli;

import dk.alexandra.fresco.framework.sce.resources.ResourcePool;
import dk.alexandra.fresco.framework.sce.resources.ResourcePoolImpl;
import dk.alexandra.fresco.framework.sce.resources.storage.FilebasedStreamedStorageImpl;
import dk.alexandra.fresco.framework.sce.resources.storage.InMemoryStorage;
import dk.alexandra.fresco.suite.ProtocolSuite;
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticProtocolSuite;
import dk.alexandra.fresco.suite.dummy.arithmetic.DummyArithmeticResourcePoolImpl;
import dk.alexandra.fresco.suite.dummy.bool.DummyBooleanProtocolSuite;
import dk.alexandra.fresco.suite.spdz.SpdzProtocolSuite;
import dk.alexandra.fresco.suite.spdz.SpdzResourcePool;
import dk.alexandra.fresco.suite.spdz.SpdzResourcePoolImpl;
import dk.alexandra.fresco.suite.spdz.configuration.PreprocessingStrategy;
import dk.alexandra.fresco.suite.spdz.storage.DataSupplier;
import dk.alexandra.fresco.suite.spdz.storage.DataSupplierImpl;
import dk.alexandra.fresco.suite.spdz.storage.DummyDataSupplierImpl;
import dk.alexandra.fresco.suite.spdz.storage.SpdzStorage;
import dk.alexandra.fresco.suite.spdz.storage.SpdzStorageConstants;
import dk.alexandra.fresco.suite.spdz.storage.SpdzStorageImpl;
import dk.alexandra.fresco.suite.tinytables.online.TinyTablesProtocolSuite;
import dk.alexandra.fresco.suite.tinytables.prepro.TinyTablesPreproProtocolSuite;
import java.io.File;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.cli.ParseException;

/**
 * Utility for reading all configuration from command line.
 * <p>
 * A set of default configurations are used when parameters are not specified at runtime.
 * </p>
 */
public class CmdLineProtocolSuite {

  private final int myId;
  private final int noOfPlayers;
  private final ProtocolSuite<?, ?> protocolSuite;
  private final ResourcePool resourcePool;

  static String getSupportedProtocolSuites() {
    String[] strings = {"dummybool", "dummyarithmetic", "spdz", "tinytables", "tinytablesprepro"};
    return Arrays.toString(strings);
  }

  public CmdLineProtocolSuite(String protocolSuiteName, Properties properties, int myId,
      int noOfPlayers) throws ParseException {
    this.myId = myId;
    this.noOfPlayers = noOfPlayers;
    switch (protocolSuiteName) {
      case "dummybool":
        this.protocolSuite = new DummyBooleanProtocolSuite();
        this.resourcePool =
            new ResourcePoolImpl(myId, noOfPlayers, new Random(), new SecureRandom());
        break;
      case "dummyarithmetic":
        this.protocolSuite = dummyArithmeticFromCmdLine(properties);
        BigInteger mod = new BigInteger(properties.getProperty("modulus",
            "6703903964971298549787012499123814115273848577471136527425966013026501536706464354255445443244279389455058889493431223951165286470575994074291745908195329"));
        this.resourcePool =
            new DummyArithmeticResourcePoolImpl(
                myId, noOfPlayers,
                new Random(), new SecureRandom(), mod);
        break;
      case "spdz":
        this.protocolSuite = SpdzConfigurationFromCmdLine(properties);
        this.resourcePool =
            createSpdzResourcePool(new Random(), new SecureRandom(), properties);
        break;
      case "tinytablesprepro":
        this.protocolSuite = tinyTablesPreProFromCmdLine(properties);
        this.resourcePool =
            new ResourcePoolImpl(myId, noOfPlayers, new Random(), new SecureRandom());
        break;
      case "tinytables":
        this.protocolSuite = tinyTablesFromCmdLine(properties);
        this.resourcePool =
            new ResourcePoolImpl(myId, noOfPlayers, new Random(), new SecureRandom());
        break;
      default:
        throw new ParseException("Unknown protocol suite: " + protocolSuiteName);
    }
  }

  public ResourcePool getResourcePool() {
    return resourcePool;
  }

  public ProtocolSuite<?, ?> getProtocolSuite() {
    return this.protocolSuite;
  }


  private ProtocolSuite<?, ?> dummyArithmeticFromCmdLine(Properties properties) {
    BigInteger mod = new BigInteger(properties.getProperty("modulus",
        "6703903964971298549787012499123814115273848577471136527425966013026501536706464354255445443244279389455058889493431223951165286470575994074291745908195329"));
    int maxBitLength = Integer.parseInt(properties.getProperty("maxbitlength", "150"));
    return new DummyArithmeticProtocolSuite(mod, maxBitLength);
  }

  private ProtocolSuite<?, ?> SpdzConfigurationFromCmdLine(Properties properties) {
    Properties p = getProperties(properties);
    // TODO: Figure out a meaningful default for the below
    final int maxBitLength = Integer.parseInt(p.getProperty("spdz.maxBitLength", "64"));
    if (maxBitLength < 2) {
      throw new RuntimeException("spdz.maxBitLength must be > 1");
    }
    return new SpdzProtocolSuite(maxBitLength);
  }

  private Properties getProperties(Properties properties) {
    return properties;
  }

  private SpdzResourcePool createSpdzResourcePool(Random rand,
      SecureRandom secRand, Properties properties) {
    String strat = properties.getProperty("spdz.preprocessingStrategy");
    final PreprocessingStrategy strategy = PreprocessingStrategy.valueOf(strat);
    DataSupplier supplier;
    switch (strategy) {
      case DUMMY:
        supplier = new DummyDataSupplierImpl(myId, noOfPlayers);        
        break;
      case STATIC:
        int noOfThreadsUsed = 1;        
        String storageName =
            SpdzStorageConstants.STORAGE_NAME_PREFIX + noOfThreadsUsed + "_" + myId + "_" + 0
            + "_";
        supplier = new DataSupplierImpl(new FilebasedStreamedStorageImpl(new InMemoryStorage()), storageName, noOfPlayers);        
        break;      
      default:
        throw new ConfigurationException("Unkonwn preprocessing strategy: " + strategy);
    }
    SpdzStorage store = new SpdzStorageImpl(supplier);
    try {
      return new SpdzResourcePoolImpl(myId, noOfPlayers, rand, secRand, store);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Your system does not support the necessary hash function.", e);
    }
  }

  private ProtocolSuite<?, ?> tinyTablesPreProFromCmdLine(Properties properties)
      throws ParseException, IllegalArgumentException {
    String tinytablesFileOption = "tinytables.file";
    String tinyTablesFilePath = properties.getProperty(tinytablesFileOption, "tinytables");
    return new TinyTablesPreproProtocolSuite(myId, new File(tinyTablesFilePath));
  }

  private ProtocolSuite<?, ?> tinyTablesFromCmdLine(Properties properties)
      throws ParseException, IllegalArgumentException {

    String tinytablesFileOption = "tinytables.file";
    String tinyTablesFilePath = properties.getProperty(tinytablesFileOption, "tinytables");
    return new TinyTablesProtocolSuite(myId, new File(tinyTablesFilePath));
  }
}