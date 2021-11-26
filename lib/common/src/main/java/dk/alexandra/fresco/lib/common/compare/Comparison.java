package dk.alexandra.fresco.lib.common.compare;

import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationDirectory;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;
import java.util.function.Function;

/**
 * Interface for comparing numeric values.
 */
public abstract class Comparison implements ComputationDirectory {

  private static Function<ProtocolBuilderNumeric, Comparison> provider = DefaultComparison::new;

  /** Redefine default implementation of Comparison */
  public static void load(Function<ProtocolBuilderNumeric, Comparison> provider) {
    Comparison.provider = provider;
  }

  /**
   * Create a new Comparison using the given builder.
   *
   * @param builder The root builder to use.
   * @return A new Comparison computation directory.
   */
  public static Comparison using(ProtocolBuilderNumeric builder) {
    return provider.apply(builder);
  }

  /**
   * Compares two values and return x == y
   * @param bitLength The maximum bit-length of the numbers to compare. 
   * @param x The first number
   * @param y The second number
   * @return A deferred result computing x == y
   */
  public abstract DRes<SInt> equals(int bitLength, DRes<SInt> x, DRes<SInt> y);
  
  /**
   * Computes x == y.
   *
   * @param x input
   * @param y input
   * @return A deferred result computing x == y. Result will be either [1] (true) or [0] (false).
   */
  public abstract DRes<SInt> equals(DRes<SInt> x, DRes<SInt> y);
  
  /**
   * Computes if x1 &le; x2.
   * @param x1 input
   * @param x2 input
   * @return A deferred result computing x1 &le; x2. Result will be either [1] (true) or [0] (false).
   */
  public abstract  DRes<SInt> compareLEQ(DRes<SInt> x1, DRes<SInt> x2);

  /**
   * Compares if x1 &le; x2, but with twice the possible bit-length.
   * Requires that the maximum bit length is set to something that can handle
   * this scenario. It has to be at least less than half the modulus bit size.
   * 
   * @param x1 input
   * @param x2 input
   * @return A deferred result computing x1 &le; x2. Result will be either [1] (true) or [0] (false).
   */
  public abstract DRes<SInt> compareLEQLong(DRes<SInt> x1, DRes<SInt> x2);

  /**
   * Computes the sign of the value (positive or negative)
   * 
   * @param x The value to compute the sign off
   * @return A deferred result computing the sign. Result will be 1 if the value is positive
   *         (including 0) and -1 if negative.
   */
  public abstract DRes<SInt> sign(DRes<SInt> x);

  /**
   * Test for equality with zero for a bitLength-bit number (positive or negative)
   *
   * @param x the value to test against zero
   * @param bitLength bitlength
   * @return A deferred result computing x == 0. Result will be either [1] (true) or [0] (false)
   */
  public abstract DRes<SInt> compareZero(DRes<SInt> x, int bitLength);
}
