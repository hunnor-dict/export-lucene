package net.hunnor.dict.lucene.util;

public class RomanNumerals {

  private RomanNumerals() {
  }

  enum Numeral {

    I(1),

    IV(4),

    V(5),

    IX(9),

    X(10),

    XL(40),

    L(50),

    XC(90),

    C(100),

    CD(400),

    D(500),

    CM(900),

    M(1000);

    private int weigth;

    Numeral(int weigth) {
      this.weigth = weigth;
    }

  }

  /**
   * Convert an integer to Roman numerals.
   *
   * @param num the integer to convert
   * @return the Roman numeral with the same value
   */
  public static String roman(int num) {
    if (num <= 0) {
      throw new IllegalArgumentException();
    }
    int rest = num;
    StringBuilder stringBuilder = new StringBuilder();
    final Numeral[] values = Numeral.values();
    for (int i = values.length - 1; i >= 0; i--) {
      while (rest >= values[i].weigth) {
        stringBuilder.append(values[i]);
        rest -= values[i].weigth;
      }
    }
    return stringBuilder.toString();
  }

}
