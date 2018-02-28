package dk.alexandra.fresco.decimal.fixed;

import dk.alexandra.fresco.decimal.DefaultAdvancedRealNumeric;
import dk.alexandra.fresco.decimal.SReal;
import dk.alexandra.fresco.framework.DRes;
import dk.alexandra.fresco.framework.builder.numeric.ProtocolBuilderNumeric;
import dk.alexandra.fresco.framework.value.SInt;

public class AdvancedFixedNumeric extends DefaultAdvancedRealNumeric {

  public AdvancedFixedNumeric(ProtocolBuilderNumeric builder) {
    super(builder, scope -> new FixedNumeric(scope));
  }

  @Override
  public DRes<SReal> sqrt(DRes<SReal> x) {
    return builder.seq(seq -> {
      SFixed cast = (SFixed) x.out();
      DRes<SInt> underlyingInt = cast.getSInt();
      int scale = cast.getScale();
      DRes<SInt> intResult = seq.advancedNumeric().sqrt(underlyingInt,
          seq.getBasicNumericContext().getMaxBitLength());
      if (scale % 2 != 0) {
        throw new IllegalArgumentException("Scale should be even");
      }
      return new SFixed(intResult, scale / 2);
    });
    
  }

}
