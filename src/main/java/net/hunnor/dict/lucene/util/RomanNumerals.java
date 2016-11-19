package net.hunnor.dict.lucene.util;

/**
 * Utility class for converting integers to Roman numerals.
 */
public final class RomanNumerals {

	/**
	 * Hide default constructor.
	 */
	private RomanNumerals() {
	}

	/**
	 * Constants for Roman numerals.
	 */
	enum Numeral {

		/**
		 * Constant for Roman numeral I.
		 */
		I(1),

		/**
		 * Constant for Roman numeral IV.
		 */
		IV(4),

		/**
		 * Constant for Roman numeral V.
		 */
		V(5),

		/**
		 * Constant for Roman numeral IX.
		 */
		IX(9),

		/**
		 * Constant for Roman numeral X.
		 */
		X(10),

		/**
		 * Constant for Roman numeral XL.
		 */
		XL(40),

		/**
		 * Constant for Roman numeral L.
		 */
		L(50),

		/**
		 * Constant for Roman numeral XC.
		 */
		XC(90),

		/**
		 * Constant for Roman numeral C.
		 */
		C(100),

		/**
		 * Constant for Roman numeral CD.
		 */
		CD(400),

		/**
		 * Constant for Roman numeral D.
		 */
		D(500),

		/**
		 * Constant for Roman numeral CM.
		 */
		CM(900),

		/**
		 * Constant for Roman numeral M.
		 */
		M(1000);

		/**
		 * The integer value of the Roman numeral.
		 */
		private int weigth;

		/**
		 * Constructor with the weight field.
		 * @param w the weight to set
		 */
		Numeral(final int w) {
			this.weigth = w;
		}
	}

	/**
	 * Convert an integer to Roman numerals.
	 * @param n the integer to convert
	 * @return the Roman numeral with the same value
	 */
	public static String roman(final int n) {
		if (n <= 0) {
			throw new IllegalArgumentException();
		}
		int rest = n;
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
