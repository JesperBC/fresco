package dk.alexandra.fresco.tools.mascot.field;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import dk.alexandra.fresco.tools.mascot.arithm.CollectionUtils;

public class FieldElementCollectionUtils {

  /**
   * Multiplies two lists of field elements, pair-wise.
   * 
   * @param leftFactors
   * @param rightFactors
   * @return list of result
   */
  public static List<FieldElement> pairWiseMultiply(List<FieldElement> leftFactors,
      List<FieldElement> rightFactors) {
    if (leftFactors.size() != rightFactors.size()) {
      throw new IllegalArgumentException("Lists must be same size");
    }
    return pairWiseMultiplyStream(leftFactors, rightFactors).collect(Collectors.toList());
  }

  /**
   * Multiplies two lists of field elements, pair-wise.
   * 
   * @param leftFactors
   * @param rightFactors
   * @return stream of result
   */
  static Stream<FieldElement> pairWiseMultiplyStream(List<FieldElement> leftFactors,
      List<FieldElement> rightFactors) {
    return IntStream.range(0, leftFactors.size())
        .mapToObj(idx -> {
          FieldElement l = leftFactors.get(idx);
          FieldElement r = rightFactors.get(idx);
          return l.multiply(r);
        });
  }

  /**
   * Computes inner product of two lists of field elements.
   * 
   * @param left
   * @param right
   * @return
   */
  public static FieldElement innerProduct(List<FieldElement> left, List<FieldElement> right) {
    if (left.size() != right.size()) {
      throw new IllegalArgumentException("Lists must have same size");
    }
    return CollectionUtils.sum(pairWiseMultiplyStream(left, right));
  }

  /**
   * Computes inner product of elements and powers of twos. <b> e0 * 2**0 + e1 * 2**1 + ... + e(n -
   * 1) * 2**(n - 1)
   * 
   * @param elements
   * @return
   */
  public static FieldElement recombine(List<FieldElement> elements, BigInteger modulus,
      int modBitLength) {
    if (elements.size() > modBitLength) {
      throw new IllegalArgumentException("Number of elements cannot exceed bit-length");
    }
    List<FieldElement> generators = FieldElementGeneratorCache.getGenerators(modulus, modBitLength);
    return innerProduct(elements, generators.subList(0, elements.size()));
  }

  /**
   * Transposes matrix of field elements.
   * 
   * @param mat
   * @return
   */
  public static List<List<FieldElement>> transpose(List<List<FieldElement>> mat) {
    // TODO: switch to doing fast transpose
    return CollectionUtils.transpose(mat);
  }

  /**
   * Duplicates each element stretchBy times.
   * 
   * @param elements
   * @param stretchBy
   * @return
   */
  public static List<FieldElement> stretch(List<FieldElement> elements, int stretchBy) {
    List<FieldElement> stretched = new ArrayList<>(elements.size() * stretchBy);
    for (FieldElement element : elements) {
      for (int c = 0; c < stretchBy; c++) {
        stretched.add(new FieldElement(element));
      }
    }
    return stretched;
  }

  /**
   * Appends padding elements to end of list numPads times.
   * 
   * @param elements
   * @param padElement
   * @param numPads
   * @return
   */
  public static List<FieldElement> padWith(List<FieldElement> elements, FieldElement padElement,
      int numPads) {
    List<FieldElement> copy = new ArrayList<>(elements);
    copy.addAll(Collections.nCopies(numPads, padElement));
    return copy;
  }

}
