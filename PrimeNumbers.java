import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * A class to find and print and/or save (to a text file) all prime numbers in order or to find the next prime number after a given start number and print/save it.
 * Original work by Thomas Hardern 2017 (taeh@st-andrews.ac.uk), 2017.
 * Some mathematical methods inspired by Numberphile from https://www.youtube.com/watch?v=lEvXcTYqtKU
 */
public class PrimeNumbers {

    private static final BigInteger ZERO = new BigInteger("0");
    private static final BigInteger ONE = new BigInteger("1");
    private static final BigInteger TWO = new BigInteger("2");
    private static final BigInteger THREE = new BigInteger("3");
    private static final BigInteger FOUR = new BigInteger("4");
    private static final BigInteger FIVE = new BigInteger("5");

    public static void main(String[] args) {
        String input = getString("Please input whether you would like the program to print all (\"A\") the prime numbers, or the next (\"N\") after a given number.");
        while (!(input.equals("A") || input.equals("N"))) {
            input = getString("Your input was not valid, please input either \"A\" or \"N\". Please input whether you would like the program to print all (\"A\") the prime numbers, or the next (\"N\") after a given number.");
        }
        if (input.equals("A")) printAll();
        else findNext();
    }

    private static BigInteger incrementForLoop(BigInteger termNoIn) {
        if (!termNoIn.equals(TWO)) {
            return termNoIn.add(TWO);
        } else {
            return termNoIn.add(ONE);
        }
    }

    private static void printAll() {
        BigInteger termNo = ONE;
        for (BigInteger i = TWO; true; i = incrementForLoop(i)) {
            if (isPrime(i)) {
                System.out.println("The " + getOrdinal(addCommas(termNo.toString())) + " prime number is: " + addCommas(i.toString()) + ".");
                termNo = termNo.add(ONE);
            }
        }
    }

    private static void findNext() {
        BigInteger startPoint = getBigInteger("Please give the BigInteger value to start from.");
        if (startPoint.equals(TWO)) {
            System.out.println("The first prime number after 2 is 3.");
        } else {
            String startPointString = startPoint.toString();
            if (isEven(startPoint)) startPoint = startPoint.subtract(ONE);
            boolean isFound = false;
            for (BigInteger i = startPoint.add(TWO); !isFound; i = incrementForLoop(i)) {
                if (isPrime(i)) {
                    System.out.println("The first prime number after " + addCommas(startPointString) + " is " + addCommas(i.toString()) + ".");
                    isFound = true;
                }
            }
        }
    }

    private static BigInteger findLucasNumber(BigInteger termNumIn) {
        /*
            Bring in number as term number in Lucas Sequence; find the value at that term.
         */
        if( termNumIn.intValue() == 0 ) return TWO;
        if( termNumIn.intValue() == 1 ) return ONE;
        return findLucasNumber(termNumIn.subtract(ONE)).add(findLucasNumber(termNumIn.subtract(TWO)));
        //todo: convert this from recursion to a general formula function
    }

    private static boolean lucasTest(BigInteger numIn) {
        /*
            Bring in a number to be tested against the Lucas Sequence to see if it is not, or is probably prime.
            Take numIn --> find number at that point in the sequence. Subtract 1. That is lucasNum.
            if numIn is a factor of lucasNum, return true : meaning numIn is very likely to be prime
            else return false: numIn is definitely not prime
         */

        BigInteger lucasNum = findLucasNumber(numIn).subtract(ONE);
        return (isFactor(numIn, lucasNum));
    }

    private static boolean LucasLehmertest (BigInteger numIn) {
        /*
            if numIn = x = 2 ^ y - 1

            x + 1 = z = 2 ^ y

            L-L(y - 1) = w

            if x % w == 0 then x is prime, else x is not prime.
         */
        BigInteger z = numIn.add(ONE); // so no -1 on side of 2 ^ y
        if (!isEven(z)) return false;
        BigDecimal y = new BigDecimal(Math.log(z.doubleValue())); // find y : log base 2 of z
        // todo: make above log operation more accurate by using external library for direct log operation on BigInteger / BigDecimal values in place of Math.log
        if (!y.remainder(new BigDecimal(0)).equals(new BigDecimal("0"))) return false; // if log base 2 of z doesnt have remainder 0 return false
        BigInteger w = getLucasLehmerNumber(y.toBigInteger().subtract(ONE)); // find L-L number at term (y - 1), set to w
        return ((numIn.remainder(w)).equals(ZERO)); // if numIn % w == 0 return true, fallback return false
    }

    private static BigInteger getLucasLehmerNumber(BigInteger termNo) {
        // x(n+1) = (x(n)) ^ 2 - 2
        if (termNo.equals(ZERO)) return FOUR;
        BigInteger lastTerm = getLucasLehmerNumber(termNo.subtract(ONE));
        BigInteger lastTermSquared = lastTerm.pow(2);
        return lastTermSquared.subtract(TWO);
    }

    private static boolean isPrime(BigInteger numIn) {
        if (numIn.equals(TWO) || numIn.equals(FIVE)) return true;
        if (isEven(numIn)) return false;
        if (String.valueOf(numIn.toString().charAt(numIn.toString().length() - 1)).equals("5")) return false;
        for (BigInteger i = THREE; isLessThanOrEqualTo(i, half(numIn)); i = incrementForLoop(i)) {
            if (isFactor(i, numIn)) return false;
        }
        return true;
    }

    private static boolean isLessThanOrEqualTo(BigInteger firstNo, BigInteger secondNo) {
        String resultString = String.valueOf(firstNo.compareTo(secondNo));
        return (resultString.equals("-1") || resultString.equals("0"));
    }

    private static String getOrdinal(String inputString) {
        String penDigit = "";
        if (inputString.length() > 1) penDigit = String.valueOf(inputString.charAt(inputString.length() - 2)); // penultimate - second to last
        String lastDigit = String.valueOf(inputString.charAt(inputString.length() - 1));
        if (!penDigit.equals("1")) {
            if (lastDigit.equals("1")) {
                return inputString + "st";
            }
            if (lastDigit.equals("2")) {
                return inputString + "nd";
            }
            if (lastDigit.equals("3")) {
                return inputString + "rd";
            }
        }
        return inputString + "th"; // effectively an else: default
    }

    private static String getString(String instructions) {
        System.out.println(instructions);
        Scanner reader = new Scanner(System.in);
        try {
            return reader.next();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Your input was invalid; please give a valid String input.");
            return getString(instructions);
        }
    }

    private static BigInteger getBigInteger(String instructions) {
        System.out.println(instructions);
        Scanner reader = new Scanner(System.in);
        BigInteger toReturn;
        try {
            toReturn = reader.nextBigInteger();
        }
        catch (Exception e) {
            System.out.println();
            System.out.println("Your input was invalid; please give a valid, positive, BigInteger type numeric value.");
            toReturn = getBigInteger(instructions);
        }
        return toReturn;
    }

    private static boolean isLessThan(BigInteger firstNo, BigInteger secondNo) {
        int result = firstNo.compareTo(secondNo);
        return (String.valueOf(result).equals("-1"));
    }

    private static boolean isEven (BigInteger numIn) {
        return numIn.mod(new BigInteger("2")).equals(BigInteger.ZERO);
    }

    private static boolean isFactor(BigInteger factor, BigInteger number) {
        return (number.mod(factor).toString().equals("0"));
        //todo: redo this with BigDecimal to allow for higher no of terms (?)
    }

    private static BigInteger half(BigInteger numIn) {
        if (isEven(numIn)) return numIn.divide(TWO);
        numIn = numIn.add(ONE);
        return numIn.divide(TWO);
    }

    /**
     * this method is adapted from the Commifier class in my Fibonacci repo.
     * @param numAsString the String value of the number coming in; probably will be a BigInteger with .toString() called on it
     * @return a String of the number in returned with commas
     */
    private static String addCommas(String numAsString) {
        StringBuilder numWithCommasStringBuilder = new StringBuilder();

        if (numAsString.length() > 3) {

            for (int i = numAsString.length() - 1; i >= 0; i--) {
                if (i < numAsString.length() - 2 && numAsString.length() > 3) {
                    if ((i + 2) % 3 == 0) {
                        numWithCommasStringBuilder = new StringBuilder("," + numAsString.substring(numAsString.length() - 3, numAsString.length()) + numWithCommasStringBuilder.toString());
                        numAsString = numAsString.substring(0, numAsString.length() - 3);
                    }
                } else if (numAsString.length() < 4) {
                    numWithCommasStringBuilder = new StringBuilder(numAsString + numWithCommasStringBuilder.toString());
                    numAsString = "";
                }
            }

        }
        else numWithCommasStringBuilder = new StringBuilder(numAsString);
        return numWithCommasStringBuilder.toString();
    }

}