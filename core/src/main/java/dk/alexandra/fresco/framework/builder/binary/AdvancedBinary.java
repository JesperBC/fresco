package dk.alexandra.fresco.framework.builder.binary;

import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.ComputationDirectory;
import dk.alexandra.fresco.framework.util.Pair;
import dk.alexandra.fresco.framework.value.SBool;
import java.util.List;

public interface AdvancedBinary extends ComputationDirectory {

  /**
   * Appends a OR protocol to the current protocol.
   *
   * @param left the Computation holding the left argument.
   * @param right the Computation holding the right argument.
   * @return an computation holding the output of the appended protocol.
   */
  DRes<SBool> or(DRes<SBool> left, DRes<SBool> right);

  DRes<SBool> or(DRes<SBool> left, boolean right);



  DRes<SBool> xnor(DRes<SBool> left, DRes<SBool> right);

  DRes<SBool> xnor(DRes<SBool> left, boolean right);

  DRes<SBool> nand(DRes<SBool> left, DRes<SBool> right);

  DRes<SBool> nand(DRes<SBool> left, boolean right);

  /**
   * And from copy. Uses the copy protocol to do an AND operation between the two inputs.
   * 
   * @param left The left secret shared input.
   * @param right The publicly known input.
   * @return left AND right
   */
  DRes<SBool> and(DRes<SBool> left, boolean right);


  /**
   * Appends a conditional select protocol to the current protocol. The output of this protocol on
   * inputs a and b and condition bit c is the bit r := c ? a : b.
   *
   * @param condition the Computation holding the condition on which to select.
   * @param left the Computation holding the left argument.
   * @param right the Computation holding the right argument.
   * @return an computation holding the output of the appended protocol.
   */
  DRes<SBool> condSelect(DRes<SBool> condition, DRes<SBool> left,
      DRes<SBool> right);

  /**
   * Half adder which returns the result in the 0'th position and the carry in the 1'st position of
   * the array.
   * 
   * @param left The first input.
   * @param right The second input.
   * @return A computation which yields the result and the carry.
   */
  DRes<Pair<SBool, SBool>> oneBitHalfAdder(DRes<SBool> left,
      DRes<SBool> right);

  /**
   * Same as {@link #oneBitHalfAdder(DRes, DRes)}, but with an option to also add a
   * potential carry to the addition.
   * 
   * @param left The first input.
   * @param right The second input.
   * @param carry The potential carry from a previous adder.
   * @return A computation which yields the result and the carry.
   */
  DRes<Pair<SBool, SBool>> oneBitFullAdder(DRes<SBool> left, DRes<SBool> right,
      DRes<SBool> carry);

  /**
   * Full adder which works with any number of inputs to the addition. The lefts and rights must
   * have the same size. The Output is a computation which contains an array of size n, where n is
   * the length of the lefts and rights inputs + 1, where the 0'th entry is the carry and the rest
   * is the result. An example: lefts: 1100101 rights: 1101001 inCarry: 1 => output:11001111
   * 
   * @param lefts The first inputs.
   * @param rights The second inputs.
   * @param inCarry The potential carry from a previous adder.
   * @return A computation which yields the results and the carry.
   */
  DRes<List<DRes<SBool>>> fullAdder(List<DRes<SBool>> lefts,
      List<DRes<SBool>> rights, DRes<SBool> inCarry);

  /**
   * Multiplies the left and right numbers and leaves the result in the output. The inputs are not
   * required to have the same size.
   * 
   * @param lefts The left input
   * @param rights The right input
   * @return An array of size lefts.size+rights.size containing the multiplication of the two
   *         numbers.
   */
  DRes<List<DRes<SBool>>> binaryMult(List<DRes<SBool>> lefts,
      List<DRes<SBool>> rights);

  /**
   * Computes the logarithm base 2 of the input number. It is currently up to the application
   * programmer to check if the input number is 0, since the result will otherwise be 0 which is
   * incorrect as log_2(0) = NaN.
   * 
   * @param number The number to compute log_2 on.
   * @return An array containing the log_2(number).
   */
  DRes<List<DRes<SBool>>> logProtocol(List<DRes<SBool>> number);

  /**
   * Increments the number represented by a list of bits with a single secret bit (thus adding
   * either 0 or 1 to the number).
   * 
   * @param large The bit representation of a number
   * @param increment The bit to increment with
   * @return A list representing the new number after the incrementation.
   */
  DRes<List<DRes<SBool>>> bitIncrement(List<DRes<SBool>> large,
      DRes<SBool> increment);

  /**
   * Compares the keys of two key-value pairs and produce a list of pairs so that the first pair has
   * the largest key.
   * 
   * @param leftKeyAndValue A pair of first the key and then the value.
   * @param rightKeyAndValue A pair of first the key and then the value.
   * @return A list of pairs where the first pair has the largest key.
   */
  DRes<List<Pair<List<DRes<SBool>>, List<DRes<SBool>>>>> keyedCompareAndSwap(
      Pair<List<DRes<SBool>>, List<DRes<SBool>>> leftKeyAndValue,
      Pair<List<DRes<SBool>>, List<DRes<SBool>>> rightKeyAndValue);

}